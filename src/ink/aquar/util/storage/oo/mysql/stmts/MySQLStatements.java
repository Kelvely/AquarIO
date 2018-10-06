package ink.aquar.util.storage.oo.mysql.stmts;

import ink.aquar.util.misc.TextResourceLoader;

class MySQLStatements {

    private MySQLStatements() {}

    public static final String INIT_CONN = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("init_conn.sql")).replace('\n', ' ');

    public static final String TRY_LOCK = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("try_lock.sql")).replace('\n', ' ');
    public static final String UNLOCK = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("unlock.sql")).replace('\n', ' ');

    public static final String FINALIZE = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("finalize.sql")).replace('\n', ' ');

    public static final String SIZE = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("size.sql")).replace('\n', ' ');

    public static final String RETRIEVE_STRUCT = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("retrieve_struct.sql")).replace('\n', ' ');
    public static final String RETRIEVE_DATA = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("retrieve_data.sql")).replace('\n', ' ');

    public static final String GET = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("get.sql")).replace('\n', ' '); // Loop-able

    // TODO Revise for local nested struct objects, more complicated composition might required.
    public static final String SET_LOCAL = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("set_local.sql")).replace('\n', ' '); // Loop-able
    public static final String SET_REMOTE = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("set_remote.sql")).replace('\n', ' '); // Loop-able
    public static final String SET_LOCAL_RETURNED = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("set_local_returned.sql")).replace('\n', ' '); // Loop-able
    public static final String SET_REMOTE_RETURNED = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("set_remote_returned.sql")).replace('\n', ' '); // Loop-able

    public static final String REMOVE = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("remove.sql")).replace('\n', ' '); // Loop-able
    public static final String REMOVE_RETURNED = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("remove_returned.sql")).replace('\n', ' '); // Loop-able

    public static final String HAS_FIELD = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("has_field.sql")).replace('\n', ' '); // Loop-able

    public static final String RETRIEVE_FIELD = TextResourceLoader.load(MySQLStatements.class.getClassLoader().getResourceAsStream("retrieve_field.sql")).replace('\n', ' '); // Loop-able

}
