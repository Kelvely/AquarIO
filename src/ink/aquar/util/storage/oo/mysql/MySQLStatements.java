package ink.aquar.util.storage.oo.mysql;

import ink.aquar.util.misc.ResourceLoader;

class MySQLStatements {

    private MySQLStatements() {}

    public static final String INIT_CONN = load("init_conn.sql");

    public static final String TRY_LOCK = load("try_lock.sql");
    public static final String UNLOCK = load("unlock.sql");

    public static final String FINALIZE = load("finalize.sql");

    public static final String SIZE = load("size.sql");

    public static final String RETRIEVE_STRUCT = load("retrieve_struct.sql");
    public static final String RETRIEVE_DATA = load("retrieve_data.sql");

    public static final String GET = load("get.sql"); // Loop-able

    // TODO Revise for local nested struct objects, more complicated composition might required.
    public static final String SET_LOCAL = load("set_local.sql"); // Loop-able
    public static final String SET_REMOTE = load("set_remote.sql"); // Loop-able
    public static final String SET_LOCAL_RETURNED = load("set_local_returned.sql"); // Loop-able
    public static final String SET_REMOTE_RETURNED = load("set_remote_returned.sql"); // Loop-able

    public static final String REMOVE = load("remove.sql"); // Loop-able
    public static final String REMOVE_RETURNED = load("remove_returned.sql"); // Loop-able

    public static final String HAS_FIELD = load("has_field.sql"); // Loop-able

    public static final String RETRIEVE_FIELD = load("retrieve_field.sql"); // Loop-able
    
    private static String load(String file) {
        return ResourceLoader.loadString(MySQLStatements.class.getClassLoader().getResourceAsStream("ink/aquar/util/storage/oo/mysql/stmt/" + file));
    }

}
