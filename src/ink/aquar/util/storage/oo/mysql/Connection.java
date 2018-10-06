package ink.aquar.util.storage.oo.mysql;

import ink.aquar.util.concurrent.callback.CallbackArg0;
import ink.aquar.util.concurrent.callback.CallbackArg1;
import ink.aquar.util.misc.SchedulerSet;
import ink.aquar.util.schedule.Scheduler;
import ink.aquar.util.storage.oo.RemoteStructObject;
import ink.aquar.util.storage.oo.conncluster.ConnectionManager;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Connection implements ConnectionManager {

    protected List<java.sql.Connection> sqlConnections = new ArrayList<>();
    protected ReadWriteLock connRWL = new ReentrantReadWriteLock();

    protected final SchedulerSet schedulerSet;

    protected final String url;
    protected final String database;
    protected final String username;
    protected final String password;

    protected final UUID connectionId = UUID.randomUUID();


    public Connection(OosMySQL.Profile profile) {
        schedulerSet = new SchedulerSet(profile.timingScheduler, profile.internalScheduler, profile.interfaceScheduler);

        url = profile.url;
        database = profile.database;
        username = profile.username;
        password = profile.password;
    }

    RemoteStructObject getRootObject() {
        return new MyRemoteStructObject(this, 0, schedulerSet);
    }

    Statement createStatement() throws SQLException {
        Statement statement;
        connRWL.readLock().lock();
        statement = sqlConnections.get(ThreadLocalRandom.current().nextInt(sqlConnections.size())).createStatement();
        connRWL.readLock().unlock();
        return statement;
    }

    PreparedStatement prepareStatement(String sql) throws SQLException {
        PreparedStatement statement;
        connRWL.readLock().lock();
        statement = sqlConnections.get(ThreadLocalRandom.current().nextInt(sqlConnections.size())).prepareStatement(sql);
        connRWL.readLock().unlock();
        return statement;
    }

    UUID getConnectionId() {
        return connectionId;
    }

    @Override
    public void createSubConnections(int amount, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle) {
        if(amount < 0)
            throw new IllegalArgumentException("Use closeSubConnections() instead of negative value in createSubConnections()!");
        if(amount == 0) {
            schedulerSet.interfaceScheduler.schedule(callback::onCallback);
            return;
        }
        schedulerSet.internalScheduler.schedule(() -> {
            createSubConn(amount, callback, exHandle, schedulerSet.internalScheduler);
        });
    }

    void createSubConn(int amount, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle, Scheduler outputScheduler) {
        List<java.sql.Connection> connections = new ArrayList<>(amount);
        try {
            for (int i = 0; i < amount; i++) {
                java.sql.Connection connection = DriverManager.getConnection(url, username, password);
                initConn(connection);
                connections.add(connection);
            }
        } catch (SQLException ex) {
            for (java.sql.Connection connection : connections) {
                try {
                    connection.close();
                } catch (SQLException e) { /* IGNORE */ }
            }
            outputScheduler.schedule(() -> exHandle.onCallback(ex));
            return;
        }
        connRWL.writeLock().lock();
        sqlConnections.addAll(connections);
        connRWL.writeLock().unlock();
        outputScheduler.schedule(callback::onCallback);
    }

    private void initConn(java.sql.Connection conn) throws SQLException {
         // TODO
    }


    @Override
    public void closeSubConnections(int amount, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle) {
        if(amount < 0)
            throw new IllegalArgumentException("Use createSubConnections() instead of negative value in closeSubConnections()!");
        if(amount == 0) {
            schedulerSet.interfaceScheduler.schedule(callback::onCallback);
            return;
        }
        schedulerSet.internalScheduler.schedule(() -> {
            closeSubConn(amount, callback, exHandle, schedulerSet.interfaceScheduler);
        });
    }

    private void closeSubConn(int amount, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle, Scheduler outputScheduler) {
        List<java.sql.Connection> removedConn = new ArrayList<>(amount);
        connRWL.writeLock().lock();
        if(amount >= sqlConnections.size()) {
            outputScheduler.schedule(() -> exHandle.onCallback(
                    new IllegalArgumentException("Not enough amount of connections to close!")
            ));
        }
        for(int i=0; i<amount; i++) {
            removedConn.add(sqlConnections.remove(sqlConnections.size() - 1));
        }


        for (java.sql.Connection conn : removedConn) {
            try {
                conn.close();
            } catch(SQLException ex) { /* IGNORE */ }
        }
        outputScheduler.schedule(callback::onCallback);
    }

    @Override
    public int numOfSubConnections() {
        int num;
        connRWL.readLock().lock();
        num = sqlConnections.size();
        connRWL.readLock().unlock();
        return num;
    }

    @Override
    protected void finalize() throws Throwable {
        List<java.sql.Connection> connections = new ArrayList<>(sqlConnections);
        schedulerSet.internalScheduler.schedule(() -> {
            for (java.sql.Connection conn : connections) {
                try {
                    conn.close();
                } catch(SQLException ex) {
                    // IGNORE
                }
            }
        });
        super.finalize();
    }
}
