package ink.aquar.util.eventhandler;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A thread safe version of event bus,
 * preventing registering listener and firing event at same time causes thread safe issue. <br/>
 * @author Kelby Iry
 */
public class ConcurrentEventBus extends EventBus {

    protected final ReadWriteLock rwl = new ReentrantReadWriteLock();

    @Override
    public <E extends Event> void registerListener(Listener<E> listener, EventPriority priority) {
        rwl.writeLock().lock();
        super.registerListener(listener, priority);
        rwl.writeLock().unlock();
    }

    @Override
    public <E extends Event> void unregisterListener(Listener<E> listener) {
        rwl.writeLock().lock();
        super.unregisterListener(listener);
        rwl.writeLock().unlock();
    }

    @Override
    public <E extends Event> void fireEvent(E event) {
        rwl.readLock().lock();
        super.fireEvent(event);
        rwl.readLock().unlock();
    }
}
