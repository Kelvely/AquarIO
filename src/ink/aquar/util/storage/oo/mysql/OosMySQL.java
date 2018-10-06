package ink.aquar.util.storage.oo.mysql;

import ink.aquar.util.concurrent.callback.CallbackArg1;
import ink.aquar.util.concurrent.callback.CallbackArg2;
import ink.aquar.util.schedule.DelayedScheduler;
import ink.aquar.util.schedule.Scheduler;
import ink.aquar.util.storage.oo.RemoteStructObject;
import ink.aquar.util.storage.oo.conncluster.ConnectionManager;

public class OosMySQL {

    public static void connect(Profile profile, CallbackArg2<RemoteStructObject, ConnectionManager> callback, CallbackArg1<? super Exception> exHandle) {
        if(profile.connections <= 0)
            throw new IllegalArgumentException("At least allocate 1 sub-connection to the connection cluster!");
        profile.internalScheduler.schedule(() -> {
            try {
                checkDriverExistence();
            } catch(DriverNotFoundException ex) {
                profile.interfaceScheduler.schedule(() -> exHandle.onCallback(ex));
                return;
            }

            Connection conn = new Connection(profile);
            conn.createSubConn(profile.connections, () -> {
                RemoteStructObject obj = conn.getRootObject();
                profile.interfaceScheduler.schedule(() -> callback.onCallback(obj, conn));
            }, exHandle, profile.internalScheduler);


        });

    }

    public static void connect(Profile profile, CallbackArg1<RemoteStructObject> callback, CallbackArg1<? super Exception> exHandle) {
        connect(profile, (obj, conn) -> callback.onCallback(obj), exHandle);
    }

    public static final class Profile {
        public String url;
        public String username;
        public String password;
        public String database;
        public int connections;

        public DelayedScheduler timingScheduler;
        public Scheduler internalScheduler;
        public Scheduler interfaceScheduler;
    }

    private static void checkDriverExistence() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new DriverNotFoundException();
        }
    }

}
