# System initialization
CREATE DATABASE IF NOT EXISTS aquar_io_oos_schema CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aquar_io_oos_schema;

#Declaration sys_disconn_handle
start transaction read write;
drop procedure if exists sys_disconn_handle;
delimiter //
create procedure sys_disconn_handle() 
BEGIN
	DECLARE discard_conn_done BOOLEAN DEFAULT FALSE;
    DECLARE db VARCHAR(64);
    DECLARE db_cursor CURSOR FOR SELECT db_name from dbs;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET discard_conn_done = TRUE;
    
    open db_cursor;
    
    discard_conn: LOOP
		FETCH db_cursor INTO db;
		IF discard_conn_done THEN
			LEAVE discard_conn;
		END IF;
		BEGIN
			# 1305 error stands for procedure not found. here's no such standardized thing as check if a procedure exists, thus fuck mysql :(
			declare continue handler for 1305 delete from dbs where db_name = db; 
			
            Set @stmt_in_text = concat('call ', db, '.on_disconnect();');
			prepare stmt from @stmt_in_text;
			execute stmt;
            deallocate prepare stmt;
        END;
    END LOOP;
    
    close db_cursor;
END; //
delimiter ;
commit;

# Declaration sys_init
START TRANSACTION read write;
drop procedure if exists sys_init;
delimiter //
create procedure sys_init() 
BEGIN
    create table if not exists dbs (db_name VARCHAR(64) not null, PRImary key(db_name)) ENGINE = InnoDB;
    
    start transaction read write;
    # mysql is putting shits on other language programmers' face cuz it doesn't provide temporary variable that restart when startup :(
    # I'm not sure if the fucking mysql will execute the exit_conenct hook as the database restart after it accidentally, 
    # So I will use the existance of exit_connect hook as a initialized indication. 
    if LOCATE('CALL aquar_io_oos_schema.sys_disconn_handle();', @@exit_connect) <= 0 THEN
		begin
			DECLARE discard_temps_done BOOLEAN DEFAULT FALSE;
			DECLARE db VARCHAR(64);
			DECLARE db_cursor CURSOR FOR SELECT db_name from dbs;
			DECLARE CONTINUE HANDLER FOR NOT FOUND SET discard_temps_done = TRUE;
		
			open db_cursor;
		
			discard_temps: LOOP
				FETCH db_cursor INTO db;
				IF discard_temps_done THEN
					LEAVE discard_temps;
				END IF;
				BEGIN
					declare continue handler for 1305 delete from dbs where db_name = db; 
				
					Set @stmt_in_text = concat('call ', db, '.startup();');
					prepare stmt from @stmt_in_text;
					execute stmt;
					deallocate prepare stmt;
				END;
			END LOOP;
            
            close db_cursor;
		end;
        SET @@exit_connect = concat('CALL aquar_io_oos_schema.sys_disconn_handle(); ', @@exit_connect);
    END IF;
    commit;
END; //
delimiter ;
commit;

CALL sys_init();

# Database selection and initialization
CREATE DATABASE IF NOT EXISTS <dbName> CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE <dbName>;

# Declaration startup
START TRANSACTION READ WRITE;
DROP PROCEDURE IF EXISTS startup;
DELIMITER //
CREATE PROCEDURE startup()
BEGIN
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
END; //
DELIMITER ;
COMMIT;

# Declaration on_disconnect
START TRANSACTION READ WRITE;
DROP PROCEDURE IF EXISTS on_disconnect;
DELIMITER //
CREATE PROCEDURE on_disconnect()
BEGIN
	DECLARE discard_conn_done BOOLEAN DEFAULT FALSE;
	DECLARE cluster_belongs VARBINARY(16);
    DECLARE cluster_cursor CURSOR FOR SELECT cluster_id FROM conn WHERE conn_id = CONNECTION_ID();
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET discard_conn_done = TRUE;
    
    OPEN cluster_cursor;
    
    discard_conn: LOOP
		FETCH cluster_cursor INTO cluster_belongs;
		IF discard_conn_done THEN
			LEAVE discard_conn;
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

# Declaration gc
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

################################## Invocation procedures or functions



##################################

# Declaration init_db
START TRANSACTION READ WRITE;
DROP PROCEDURE IF EXISTS init_db;
DELIMITER //
CREATE PROCEDURE init_db()
BEGIN

	CREATE TABLE IF NOT EXISTS heap (addr BIGINT NOT NULL, obj_type ENUM('DATA', 'STRUCT') NOT NULL, ref BIGINT NOT NULL DEFAULT 1, lock_stat ENUM('UNLOCKED', 'WRITE_LOCK', 'READ_LOCK') NOT NULL DEFAULT 'UNLOCKED', PRIMARY KEY(addr)) ENGINE = InnoDB;
	CREATE TABLE IF NOT EXISTS obj_struct (addr BIGINT NOT NULL, field VARCHAR(128) NOT NULL, targ BIGINT NOT NULL, PRIMARY KEY(addr, field)) ENGINE = InnoDB;
	CREATE TABLE IF NOT EXISTS obj_data (addr BIGINT NOT NULL, data_type TEXT NOT NULL, content LONGBLOB NOT NULL, PRIMARY KEY(addr)) ENGINE = InnoDB;
	CREATE TABLE IF NOT EXISTS conn (cluster_id VARBINARY(16) NOT NULL, conn_id BIGINT NOT NULL, PRIMARY KEY(cluster_id, conn_id)) ENGINE = InnoDB;
	CREATE TABLE IF NOT EXISTS conn_ref (cluster_id VARBINARY(16) NOT NULL, addr BIGINT NOT NULL, ref INT NOT NULL, PRIMARY KEY(cluster_id, addr)) ENGINE = InnoDB;
	CREATE TABLE IF NOT EXISTS conn_lock (addr BIGINT NOT NULL, lock_id BIGINT NOT NULL, cluster_id VARBINARY(16) NOT NULL, PRIMARY KEY(addr, lock_id, cluster_id)) ENGINE = InnoDB;
	
    START transaction read write;
	CREATE EVENT IF NOT EXISTS collect_garbage ON SCHEDULE EVERY 1 HOUR DO CALL gc();
    # Adding the database to the system list means the system is going to handle connection cleanups of this database, as only GC is not going to do the chores properly :P
    # Only running GC doesn't hurt but just see a gc running and database is not used will be annoying xD
    INSERT IGNORE INTO aquar_io_oos_schema.dbs (db_name) VALUES (<dbName>);
    commit;
    
END; //
DELIMITER ;
COMMIT;

CALL init_db();
INSERT INTO conn (cluster_id, conn_id) VALUES (<connId>, CONNECTION_ID());
