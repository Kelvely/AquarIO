package ink.aquar.util.storage.oo.mysql;

import ink.aquar.util.concurrent.callback.CallbackArg0;
import ink.aquar.util.concurrent.callback.CallbackArg1;
import ink.aquar.util.misc.Entry;
import ink.aquar.util.misc.SchedulerSet;
import ink.aquar.util.storage.oo.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class MyRemoteStructObject extends MyRemoteObject implements RemoteStructObject {

    private static final RemoteObject[] EMPTY_REMOTE_OBJECT_ARRAY = {};

    protected MyRemoteStructObject(Connection connection, long address, SchedulerSet schedulerSet) {
        super(connection, address, schedulerSet);
    }

    @Override
    public void retrieve(CallbackArg1<LocalObject> callback, CallbackArg1<? super Exception> exHandle) {
        schedulerSet.internalScheduler.schedule(() -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(""); // TODO Help me!
                LocalStructObject obj = LocalStructObject.create();
                while(resultSet.next()) {
                    String fieldName = resultSet.getString(1);
                    long address = resultSet.getLong(2);
                    String type = resultSet.getString(3);
                    RemoteObject remoteObject;
                    switch(type.toUpperCase()) {
                        case "STRUCT":
                            remoteObject = new MyRemoteStructObject(connection, address, schedulerSet);
                            break;

                        case "DATA":
                            remoteObject = new MyRemoteDataObject(connection, address, schedulerSet);
                            break;

                            default:
                                throw new IllegalArgumentException("Type of a remote object can be either STRUCT or DATA!");
                    }
                    obj.setMember(fieldName, remoteObject);
                }
                schedulerSet.interfaceScheduler.schedule(() -> callback.onCallback(obj));
            } catch (Exception e) {
                exHandle.onCallback(e);
            }
        });
    }

    @Override
    public void get(FieldPath path, CallbackArg1<RemoteObject> callback, CallbackArg1<? super Exception> exHandle) {
        schedulerSet.internalScheduler.schedule(() -> {
            StringBuilder query = new StringBuilder();
            query.append("..."); // TODO Help me!
            // Make the statement
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query.toString());
                resultSet.next();
                long address = resultSet.getLong(1);
                if(address < 0) {
                    schedulerSet.interfaceScheduler.schedule(() -> callback.onCallback(null));
                } else {
                    String type = resultSet.getString(2);
                    RemoteObject remoteObject;
                    switch(type.toUpperCase()) {
                        case "STRUCT":
                            remoteObject = new MyRemoteStructObject(connection, address, schedulerSet);
                            break;

                        case "DATA":
                            remoteObject = new MyRemoteDataObject(connection, address, schedulerSet);
                            break;

                        default:
                            throw new IllegalArgumentException("Type of a remote object can be either STRUCT or DATA!");
                    }
                    schedulerSet.interfaceScheduler.schedule(() -> callback.onCallback(remoteObject));
                }
            } catch (Exception e) {
                schedulerSet.interfaceScheduler.schedule(() -> exHandle.onCallback(e));
            }
        });

    }

    @Override
    public void get(FieldPath[] paths, CallbackArg1<RemoteObject[]> callback, CallbackArg1<? super Exception> exHandle) {
        schedulerSet.internalScheduler.schedule(() -> {
            StringBuilder query = new StringBuilder();
            query.append("..."); // TODO Help me!
            // Make the statement
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query.toString());
                List<RemoteObject> list = new ArrayList<>(paths.length);
                while(resultSet.next()) {
                    long address = resultSet.getLong(1);
                    if (address < 0) {
                        list.add(null);
                    } else {
                        String type = resultSet.getString(2);
                        RemoteObject remoteObject;
                        switch (type.toUpperCase()) {
                            case "STRUCT":
                                remoteObject = new MyRemoteStructObject(connection, address, schedulerSet);
                                break;

                            case "DATA":
                                remoteObject = new MyRemoteDataObject(connection, address, schedulerSet);
                                break;

                            default:
                                throw new IllegalArgumentException("Type of a remote object can be either STRUCT or DATA!");
                        }
                        list.add(remoteObject);
                    }
                }
                schedulerSet.interfaceScheduler.schedule(() -> callback.onCallback(list.toArray(EMPTY_REMOTE_OBJECT_ARRAY)));
            } catch (Exception e) {
                schedulerSet.interfaceScheduler.schedule(() -> exHandle.onCallback(e));
            }
        });
    }

    @Override
    public void set(FieldPath path, OosObject object, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle) {
        schedulerSet.internalScheduler.schedule(() -> {
            StringBuilder query = new StringBuilder();
            if(object.isLocal()) {
                query.append("..."); // TODO Help me!
                try {
                    PreparedStatement statement = connection.prepareStatement(query.toString());
                    // TODO fill blobs
                    statement.executeUpdate();
                    schedulerSet.interfaceScheduler.schedule(callback::onCallback);
                } catch (Exception e) {
                    schedulerSet.interfaceScheduler.schedule(() -> exHandle.onCallback(e));
                }
            } else if(object.isRemote()) {
                query.append("..."); // TODO Help me pls!
                try {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(query.toString());
                    schedulerSet.interfaceScheduler.schedule(callback::onCallback);
                } catch (Exception e) {
                    schedulerSet.interfaceScheduler.schedule(() -> exHandle.onCallback(e));
                }
            } else {
                schedulerSet.interfaceScheduler.schedule(() -> exHandle.onCallback(new IllegalArgumentException("Unsupported OOS object type!")));
            }

        });
        // Ya such thing like this and need to be revised and optimized.
    }

    @Override
    public void set(Entry<FieldPath, OosObject>[] entries, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle) {

    }

    @Override
    public void set(FieldPath path, OosObject object, CallbackArg1<RemoteObject> callback, CallbackArg1<? super Exception> exHandle) {

    }

    @Override
    public void set(Entry<FieldPath, OosObject>[] entries, CallbackArg1<RemoteObject[]> callback, CallbackArg1<? super Exception> exHandle) {

    }

    @Override
    public void remove(FieldPath path, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle) {

    }

    @Override
    public void remove(FieldPath[] paths, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle) {

    }

    @Override
    public void remove(FieldPath path, CallbackArg1<RemoteObject> callback, CallbackArg1<? super Exception> exHandle) {

    }

    @Override
    public void remove(FieldPath[] paths, CallbackArg1<RemoteObject[]> callback, CallbackArg1<? super Exception> exHandle) {

    }

    @Override
    public void hasField(FieldPath path, CallbackArg1<Boolean> callback, CallbackArg1<? super Exception> exHandle) {

    }

    @Override
    public void hasFields(FieldPath[] paths, CallbackArg1<Boolean[]> callback, CallbackArg1<? super Exception> exHandle) {

    }

    @Override
    public void retrieve(FieldPath path, CallbackArg1<LocalObject> callback, CallbackArg1<? super Exception> exHandle) {

    }

    @Override
    public void retrieve(FieldPath[] paths, CallbackArg1<LocalObject[]> callback, CallbackArg1<? super Exception> exHandle) {

    }

    @Override
    public LockStatement lock(FieldPath path, LockType lockType, CallbackArg1<LockStatement> callback, CallbackArg1<? super Exception> exHandle) {
        return null;
    }

    @Override
    public LockStatement lock(FieldPath path, LockType lockType, long timeoutMillis, CallbackArg1<LockStatement> callback, CallbackArg0 onTimeout, CallbackArg1<? super Exception> exHandle) {
        return null;
    }

    @Override
    public LockStatement tryLock(FieldPath path, LockType lockType, CallbackArg1<LockStatement> callback, CallbackArg1<? super Exception> exHandle) {
        return null;
    }

}
