package ink.aquar.util.storage.oo.mysql;

import ink.aquar.util.concurrent.callback.CallbackArg1;
import ink.aquar.util.misc.SchedulerSet;
import ink.aquar.util.storage.oo.CommonLocalDataObject;
import ink.aquar.util.storage.oo.LocalObject;
import ink.aquar.util.storage.oo.RemoteDataObject;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.Statement;

class MyRemoteDataObject extends MyRemoteObject implements RemoteDataObject {

    protected MyRemoteDataObject(Connection connection, long address, SchedulerSet schedulerSet) {
        super(connection, address, schedulerSet);
    }

    @Override
    public void retrieve(CallbackArg1<LocalObject> callback, CallbackArg1<? super Exception> exHandle) {
        schedulerSet.internalScheduler.schedule(() -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(""); // TODO Help me!
                resultSet.next();
                String type = resultSet.getString(1);
                byte[] bytes = resultSet.getBytes(2);
                schedulerSet.interfaceScheduler.schedule(() -> callback.onCallback(new CommonLocalDataObject() {
                    {
                        this.setTypeIdentifier(type);
                        this.setBytes(ByteBuffer.wrap(bytes));
                    }
                }));
            } catch (Exception e) {
                exHandle.onCallback(e);
            }
        });

    }

    @Override
    public void size(CallbackArg1<Integer> callback, CallbackArg1<? super Exception> exHandle) {
        schedulerSet.internalScheduler.schedule(() -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(""); // TODO Help me!
                resultSet.next();
                int size = resultSet.getInt(1);
                schedulerSet.interfaceScheduler.schedule(() -> callback.onCallback(size));
            } catch (Exception e) {
                exHandle.onCallback(e);
            }
        });

    }

}
