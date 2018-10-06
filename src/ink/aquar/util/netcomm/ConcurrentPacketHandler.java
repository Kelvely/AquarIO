package ink.aquar.util.netcomm;

import ink.aquar.util.netio.MessagePoster;
import ink.aquar.util.schedule.DelayedScheduler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This is a thread safe version of packet handler. <br/>
 * Use it if your scheduler is not run on single thread. <br/>
 */
public class ConcurrentPacketHandler extends PacketHandler {

    protected final ReadWriteLock listenersRWL = new ReentrantReadWriteLock();

    public ConcurrentPacketHandler(Profile profile) {
        super(profile);
    }

    @Override
    public PacketListener registerListener(String name, String version, PacketListener listener) {
        PacketListener previous;
        listenersRWL.writeLock().lock();
        previous = super.registerListener(name, version, listener);
        listenersRWL.writeLock().unlock();
        return previous;
    }

    public PacketListener registerFallbackListener(String name, PacketListener listener) {
        PacketListener previous;
        listenersRWL.writeLock().lock();
        previous = super.registerFallbackListener(name, listener);
        listenersRWL.writeLock().unlock();
        return previous;
    }


    public PacketListener unregisterListener(String name, String version) {
        PacketListener previous;
        listenersRWL.writeLock().lock();
        previous = super.unregisterListener(name, version);
        listenersRWL.writeLock().unlock();
        return previous;
    }

    public PacketListener unregisterFallbackListener(String name) {
        PacketListener previous;
        listenersRWL.writeLock().lock();
        previous = super.unregisterFallbackListener(name);
        listenersRWL.writeLock().unlock();
        return previous;
    }

    public void unregisterAllListeners(String name) {
        listenersRWL.writeLock().lock();
        super.unregisterAllListeners(name);
        listenersRWL.writeLock().unlock();
    }


    public boolean isListenerRegistered(String name, String version) {
        boolean result;
        listenersRWL.readLock().lock();
        result = super.isListenerRegistered(name, version);
        listenersRWL.readLock().unlock();
        return result;
    }

    public boolean isFallbackRegistered(String name) {
        boolean result;
        listenersRWL.readLock().lock();
        result = super.isFallbackRegistered(name);
        listenersRWL.readLock().unlock();
        return result;
    }


    public boolean isPacketSupported(String name) {
        boolean result;
        listenersRWL.readLock().lock();
        result = super.isPacketSupported(name);
        listenersRWL.readLock().unlock();
        return result;
    }


    public boolean isVersionSupported(String name, String version) {
        boolean result;
        listenersRWL.readLock().lock();
        result = super.isVersionSupported(name, version);
        listenersRWL.readLock().unlock();
        return result;
    }


    public Set<String> getAllPacketNames() {
        Set<String> result;
        listenersRWL.readLock().lock();
        result = super.getAllPacketNames();
        listenersRWL.readLock().unlock();
        return result;
    }


    public Set<String> getAllVersionSupported(String packetName) {
        Set<String> result;
        listenersRWL.readLock().lock();
        result = super.getAllVersionSupported(packetName);
        listenersRWL.readLock().unlock();
        return result;
    }

    @Override
    protected void broadcastPacket(Packet packet) {
        listenersRWL.readLock().lock();
        super.broadcastPacket(packet);
        listenersRWL.readLock().unlock();
    }
}
