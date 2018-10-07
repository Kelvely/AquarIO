package ink.aquar.util.storage.oo.mysql;

import ink.aquar.util.concurrent.callback.CallbackArg1;
import ink.aquar.util.misc.ResourceLoader;
import ink.aquar.util.misc.SchedulerSet;
import ink.aquar.util.storage.oo.CommonLocalDataObject;
import ink.aquar.util.storage.oo.LocalObject;
import ink.aquar.util.storage.oo.RemoteDataObject;

import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class MyRemoteDataObject extends MyRemoteObject implements RemoteDataObject {

    protected MyRemoteDataObject(Connection connection, long address, SchedulerSet schedulerSet) {
        super(connection, address, schedulerSet);
    }

    @Override
    public void retrieve(CallbackArg1<LocalObject> callback, CallbackArg1<? super Exception> exHandle) {
        schedulerSet.internalScheduler.schedule(() -> {
            try {
                PreparedStatement statement = connection.prepareStatement(MySQLStatements.RETRIEVE_DATA);
                statement.setLong(1, address);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                String type = resultSet.getString(1);
                ByteBuffer bytes = ResourceLoader.loadBytes(resultSet.getBlob(2).getBinaryStream());
                schedulerSet.interfaceScheduler.schedule(() -> callback.onCallback(new CommonLocalDataObject() {
                    {
                        this.setTypeIdentifier(type);
                        this.setBytes(bytes);
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
                PreparedStatement statement = connection.prepareStatement(MySQLStatements.SIZE);
                statement.setLong(1, address);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                int size = resultSet.getInt(1);
                schedulerSet.interfaceScheduler.schedule(() -> callback.onCallback(size));
            } catch (Exception e) {
                exHandle.onCallback(e);
            }
        });

    }

}
