CREATE DATABASE IF NOT EXISTS :dbName: CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE :dbName:;

START TRANSACTION READ WRITE;
DROP PROCEDURE IF EXISTS on_disconnect;
DELIMITER //
CREATE PROCEDURE on_disconnect()
BEGIN
	DECLARE discard_conn_done BOOLEAN DEFAULT FALSE;
	DECLARE cluster_belongs VARBINARY(16);
    DECLARE cluster_cursor CURSOR FOR SELECT cluster_id FROM conn WHERE conn_id = CONNECTION_ID();
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET discard_conns_done = TRUE;
    
    OPEN cluster_cursor;
    
    discard_conn: LOOP
		FETCH cluster_cursor INTO cluster_belongs;
		IF discard_conn_done THEN
			LEAVE discard_conns;
		END IF;
		START TRANSACTION READ WRITE; # then here's only one thread is discarding the shit.
		DELETE FROM conn WHERE (cluster_id = cluster_belongs AND conn_id = CONNECTION_ID());
		IF NOT EXISTS(SELECT conn_id FROM conn WHERE cluster_id = cluster_belongs) THEN
			COMMIT;
			BEGIN
				DECLARE discard_cluster_ref_done BOOLEAN DEFAULT FALSE;
				DECLARE address BIGINT;
				DECLARE num_reference INT;
				DECLARE ref_cursor CURSOR FOR SELECT addr, ref FROM conn_ref WHERE cluster_id = cluster_belongs;
				DECLARE CONTINUE HANDLER FOR NOT FOUND SET discard_cluster_ref_done = TRUE;
				
				OPEN ref_cursor;
				
				discard_cluster_ref: LOOP
					FETCH ref_cursor INTO address, num_reference;
					IF discard_cluster_ref_done THEN
						LEAVE discard_cluster_ref;
					END IF;
					UPDATE heap SET ref = ref - num_reference WHERE addr = address;
				END LOOP;
				
				CLOSE ref_cursor;
                
                DELETE FROM conn_ref WHERE cluster_id = cluster_belongs;
			END;
			BEGIN
				DECLARE discard_cluster_lock_done BOOLEAN DEFAULT FALSE;
				DECLARE address BIGINT;
				DECLARE lock_cursor CURSOR FOR SELECT addr FROM conn_lock WHERE cluster_id = cluster_belongs;
				DECLARE CONTINUE HANDLER FOR NOT FOUND SET discard_cluster_lock_done = TRUE;
			
				OPEN lock_cursor;
                
				discard_cluster_lock: LOOP
					FETCH lock_cursor INTO address;
					IF discard_cluster_lock_done THEN
						LEAVE discard_cluster_lock;
					END IF;
					START TRANSACTION READ WRITE;
                    DELETE FROM conn_lock WHERE (cluster_id = cluster_belongs AND addr = address);
                    CASE (SELECT lock_stat FROM heap WHERE addr = address)
						WHEN 'READ_LOCK' THEN
                        BEGIN
							IF NOT EXISTS (SELECT lock_id FROM conn_lock WHERE addr = address) THEN
								UPDATE heap SET lock_stat = 'UNLOCKED' WHERE addr = address;
                            END IF;
                        END;
                        WHEN 'WRITE_LOCK' THEN
							UPDATE heap SET lock_stat = 'UNLOCKED' WHERE addr = address;
                        BEGIN
                        END;
                    END CASE;
                    COMMIT;
				END LOOP;
			
				CLOSE lock_cursor;
			END;
		ELSE COMMIT;
		END IF;
    END LOOP;
    
    CLOSE cluster_cursor;
END; //
DELIMITER ;
COMMIT;

START TRANSACTION READ WRITE;
DROP PROCEDURE IF EXISTS gc;
DELIMITER //
CREATE PROCEDURE gc()
BEGIN
	DECLARE gc_done BOOLEAN DEFAULT FALSE;
    DECLARE address BIGINT;
    DECLARE object_type ENUM('DATA', 'STRUCT');
    DECLARE num_reference BIGINT;
    DECLARE heap_cursor CURSOR FOR SELECT addr, obj_type, ref FROM heap;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET gc_done = TRUE;
    
    OPEN heap_cursor;
    
    gc_loop: LOOP
		FETCH heap_cursor INTO address, object_type, num_reference;
        IF gc_done THEN 
			LEAVE gc_loop;
        END IF;
        IF num_reference <= 0 THEN
			CASE object_type
				WHEN 'DATA' THEN
                BEGIN
                    DELETE FROM obj_data WHERE addr = address;
				END;
                WHEN 'STRUCT' THEN
				BEGIN
					DECLARE field_delete_done BOOLEAN DEFAULT FALSE;
                    DECLARE target BIGINT;
					DECLARE field_cursor CURSOR FOR SELECT targ FROM obj_struct WHERE addr = address;
                    DECLARE CONTINUE HANDLER FOR NOT FOUND SET field_delete_done = TRUE;
                    
                    OPEN field_cursor;
                    
                    field_delete_loop: LOOP
						FETCH field_cursor INTO target;
                        IF field_delete_done THEN 
							LEAVE field_delete_loop;
                        END IF;
                        UPDATE heap SET ref = ref - 1 WHERE addr = target;
					END LOOP;
                    
                    CLOSE field_cursor;
                    
                    DELETE FROM obj_struct WHERE addr = address;
				END;
					
			END CASE;
            DELETE FROM heap WHERE addr = address;
        END IF;
    END LOOP;
    
    CLOSE heap_cursor;
END; //
DELIMITER ;
COMMIT;

START TRANSACTION READ WRITE;
DROP PROCEDURE IF EXISTS init_db;
DELIMITER //
CREATE PROCEDURE init_db()
BEGIN
	START TRANSACTION READ WRITE;
	CREATE TABLE IF NOT EXISTS sys_var (var_key VARCHAR(128) NOT NULL, var_value TEXT, PRIMARY KEY(var_key)) ENGINE = InnoDB;
	INSERT INTO sys_var (var_key, var_value) SELECT 'sys_init', 'false' WHERE NOT EXISTS (SELECT var_key FROM sys_var WHERE var_key = 'sys_init');
    IF 'true' != (SELECT var_value FROM sys_var WHERE var_key = 'sys_init') THEN
		CREATE TABLE IF NOT EXISTS heap (addr BIGINT NOT NULL, obj_type ENUM('DATA', 'STRUCT') NOT NULL, ref BIGINT NOT NULL DEFAULT 1, lock_stat ENUM('UNLOCKED', 'WRITE_LOCK', 'READ_LOCK') NOT NULL DEFAULT 'UNLOCKED', PRIMARY KEY(addr));
        CREATE TABLE IF NOT EXISTS obj_struct (addr BIGINT NOT NULL, field VARCHAR(128) NOT NULL, targ BIGINT NOT NULL, PRIMARY KEY(addr, field));
        CREATE TABLE IF NOT EXISTS obj_data (addr BIGINT NOT NULL, data_type TEXT NOT NULL, content LONGBLOB NOT NULL, PRIMARY KEY(addr));
        CREATE TABLE IF NOT EXISTS conn (cluster_id VARBINARY(16) NOT NULL, conn_id BIGINT NOT NULL, PRIMARY KEY(cluster_id, conn_id));
        CREATE TABLE IF NOT EXISTS conn_ref (cluster_id VARBINARY(16) NOT NULL, addr BIGINT NOT NULL, ref INT NOT NULL, PRIMARY KEY(cluster_id, addr));
        CREATE TABLE IF NOT EXISTS conn_lock (addr BIGINT NOT NULL, lock_id VARBINARY(16) NOT NULL, cluster_id VARBINARY(16) NOT NULL, PRIMARY KEY(addr, lock_id, cluster_id));
        
        CREATE EVENT IF NOT EXISTS collect_garbage ON SCHEDULE EVERY 1 HOUR DO CALL gc();
        
		UPDATE sys_var SET var_value = 'true' WHERE var_key = 'sys_init';
    END IF;
    COMMIT;
END; //
DELIMITER ;
COMMIT;



START TRANSACTION READ WRITE;
DROP PROCEDURE IF EXISTS start_up;
DELIMITER //
CREATE PROCEDURE start_up()
BEGIN
	START TRANSACTION READ WRITE;
	IF @@aquario_oos_:dbName:_startup <=> TRUE THEN
		IF LOCATE('CALL :dbName:.on_disconnect();', @@exit_connect) <= 0 THEN
			SET @@exit_connect = CONCAT(@@exit_connect, 'CALL :dbName:.on_disconnect(); ');
        END IF;
        BEGIN
			DECLARE discard_cluster_ref_done BOOLEAN DEFAULT FALSE;
			DECLARE address BIGINT;
			DECLARE num_reference INT;
			DECLARE ref_cursor CURSOR FOR SELECT addr, ref FROM conn_ref;
			DECLARE CONTINUE HANDLER FOR NOT FOUND SET discard_cluster_ref_done = TRUE;
			
			OPEN ref_cursor;
			
			discard_cluster_ref: LOOP
				FETCH ref_cursor INTO address, num_reference;
				IF discard_cluster_ref_done THEN
					LEAVE discard_cluster_ref;
				END IF;
				UPDATE heap SET ref = ref - num_reference WHERE addr = address;
			END LOOP;
		
			CLOSE ref_cursor;
			
			DELETE FROM conn_ref;
        END;
        BEGIN
			DECLARE discard_cluster_lock_done BOOLEAN DEFAULT FALSE;
			DECLARE address BIGINT;
			DECLARE lock_cursor CURSOR FOR SELECT addr FROM conn_lock;
			DECLARE CONTINUE HANDLER FOR NOT FOUND SET discard_cluster_lock_done = TRUE;
		
			OPEN lock_cursor;
                
			discard_cluster_lock: LOOP
				FETCH lock_cursor INTO address;
				IF discard_cluster_lock_done THEN
					LEAVE discard_cluster_lock;
				END IF;
                UPDATE heap SET lock_stat = 'UNLOCKED' WHERE addr = address;
			END LOOP;
			
			CLOSE lock_cursor;
            
            DELETE FROM conn_lock;
        END;
        DELETE FROM conn;
		SET @@aquario_oos_:dbName:_startup = TRUE;
    END IF;
    COMMIT;
END; //
DELIMITER ;
COMMIT;

CALL init_db();
CALL start_up();
INSERT INTO conn (cluster_id, conn_id) VALUES (:connId:, CONNECTION_ID());
