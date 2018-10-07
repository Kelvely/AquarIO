package ink.aquar.util.storage.oo.mysql;

import ink.aquar.util.concurrent.callback.CallbackArg0;
import ink.aquar.util.concurrent.callback.CallbackArg1;
import ink.aquar.util.misc.SchedulerSet;
import ink.aquar.util.storage.oo.LockStatement;
import ink.aquar.util.storage.oo.LockType;
import ink.aquar.util.storage.oo.RemoteObject;

import java.sql.PreparedStatement;

abstract class MyRemoteObject implements RemoteObject {

    protected final Connection connection;
    protected final long address;
    protected final SchedulerSet schedulerSet;

    protected MyRemoteObject(Connection connection, long address, SchedulerSet schedulerSet) {
        this.connection = connection;
        this.address = address;
        this.schedulerSet = schedulerSet;
    }

    @Override
    public LockStatement lock(LockType lockType, CallbackArg1<LockStatement> callback, CallbackArg1<? super Exception> exHandle) {
        MyLockStatement lockStatement = new MyLockStatement(connection, this, lockType, schedulerSet);
        schedulerSet.internalScheduler.schedule(() -> lockStatement.lock(callback, exHandle, null, -1));
        return lockStatement;
    }

    @Override
    public LockStatement lock(LockType lockType, long timeoutMillis, CallbackArg1<LockStatement> callback, CallbackArg0 onTimeout, CallbackArg1<? super Exception> exHandle) {
        MyLockStatement lockStatement = new MyLockStatement(connection, this, lockType, schedulerSet);
        schedulerSet.internalScheduler.schedule(() -> lockStatement.lock(callback, exHandle, onTimeout, System.currentTimeMillis() + timeoutMillis));
        return lockStatement;
    }

    @Override
    public LockStatement tryLock(LockType lockType, CallbackArg1<LockStatement> callback, CallbackArg1<? super Exception> exHandle) {
        MyLockStatement lockStatement = new MyLockStatement(connection, this, lockType, schedulerSet);
        schedulerSet.internalScheduler.schedule(() -> {
            try {
                lockStatement.tryLock();
                schedulerSet.interfaceScheduler.schedule(() -> callback.onCallback(lockStatement));
            } catch (Exception e) {
                schedulerSet.interfaceScheduler.schedule(() -> exHandle.onCallback(e));
            }
        });
        return lockStatement;
    }

    @Override
    protected void finalize() throws Throwable {
        schedulerSet.internalScheduler.schedule(() -> {
            try {
                PreparedStatement statement = connection.prepareStatement(MySQLStatements.FINALIZE);
                statement.setLong(1, address);
                // TODO Add arguments
                statement.executeUpdate();
            } catch (Exception ex) { /* IGNORE */}
        });
        // Remove reference, notice situation if removing reference is after connection close
        super.finalize();
    }
}
