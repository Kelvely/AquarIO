package ink.aquar.util.storage.oo.mysql;

import ink.aquar.util.concurrent.callback.CallbackArg0;
import ink.aquar.util.concurrent.callback.CallbackArg1;
import ink.aquar.util.misc.SchedulerSet;
import ink.aquar.util.storage.oo.LockStatement;
import ink.aquar.util.storage.oo.LockType;
import ink.aquar.util.storage.oo.RemoteObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

class MyLockStatement implements LockStatement {

    protected final Connection connection;
    protected final LockType lockType;
    protected final AtomicLong lockId = new AtomicLong(-1);
    protected final RemoteObject remoteObject;
    protected final SchedulerSet schedulerSet;
    protected final AtomicBoolean isCanceled = new AtomicBoolean(false);
    protected final AtomicBoolean isLocked = new AtomicBoolean(false);

    protected MyLockStatement(Connection connection, RemoteObject remoteObject, LockType lockType, SchedulerSet schedulerSet) {
        this.connection = connection;
        this.remoteObject = remoteObject;
        this.lockType = lockType;
        this.schedulerSet = schedulerSet;
    }

    void lock(CallbackArg1<LockStatement> callback, CallbackArg1<? super Exception> exHandle, CallbackArg0 onTimeout, long timeoutSysMillis) {
        synchronized (this) {
            if (isCanceled.get()) return;
            if (timeoutSysMillis > 0 && timeoutSysMillis > System.currentTimeMillis())
                schedulerSet.interfaceScheduler.schedule(onTimeout::onCallback);
            try {
                if (tryLock()) schedulerSet.interfaceScheduler.schedule(() -> {
                        synchronized (isLocked) {
                            isLocked.set(true);
                            callback.onCallback(this);
                        }
                    });
                else schedulerSet.timingScheduler.schedule(() ->
                        schedulerSet.internalScheduler.schedule(() -> lock(callback, exHandle, onTimeout, timeoutSysMillis)),
                        1); // TODO Make it smart!
            } catch (Exception e) {
                schedulerSet.interfaceScheduler.schedule(() -> exHandle.onCallback(e));
            }
        }

    }

    boolean tryLock() throws SQLException {
        synchronized (this) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(""); // TODO Help me!
            resultSet.next();
            long lockId = resultSet.getLong(1); // TODO need review, whether it should be 0 or 1.
            if (lockId >= 0) {
                this.lockId.set(lockId);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void unlock(CallbackArg1<LockStatement> callback, CallbackArg1<? super Exception> exHandle) {
        schedulerSet.internalScheduler.schedule(() -> {
            synchronized (this) {
                isCanceled.set(true);
                if(!isLocked.get()) return;
                try {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(""); // TODO Help me!
                    schedulerSet.interfaceScheduler.schedule(() -> {
                        synchronized (isLocked) {
                            isLocked.set(false);
                            callback.onCallback(this);
                        }
                    });
                } catch (Exception e) {
                    schedulerSet.interfaceScheduler.schedule(() -> exHandle.onCallback(e));
                }
            }
        });
    }

    @Override
    public boolean isLocked() {
        synchronized (isLocked) {
            return isLocked.get();
        }
    }

    @Override
    public LockType getLockType() {
        return lockType;
    }

}
