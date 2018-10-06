package ink.aquar.util.netcomm;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import ink.aquar.util.concurrent.callback.CallbackArg0;
import ink.aquar.util.concurrent.callback.CallbackArg1;
import ink.aquar.util.netio.MessagePoster;
import ink.aquar.util.netio.MessageReceiver;
import ink.aquar.util.schedule.DelayedScheduler;
import ink.aquar.util.schedule.Scheduler;
import ink.aquar.util.schedule.TaskRef;

/**
 * A packet handler is used to serialize packets, routing packets, handling responses,
 * and deliver incoming packets to the corresponding listener. <br/>
 * Creating packet handler requires three schedulers, they could be the same scheduler or not. However, all of the
 * scheduler should run on a same thread, or you should use <code>ConcurrentPacketHandler</code> instead. <br/>
 *
 * @author Kelby Iry
 * @see ConcurrentPacketHandler
 */
public class PacketHandler implements MessageReceiver {

    private static final byte[] BYTES_PACKET_TYPE_ID = Packet.SERIALIZER.getTypeIdentifier().getBytes();
    
    protected final MessagePoster messagePoster;

    protected final Map<String, Map<String, PacketListener>> specifiedListeners = new HashMap<>();
    protected final Map<String, PacketListener> fallbackListeners = new HashMap<>();

    protected final Map<UUID, DeliverOrTimeout> waitList = new ConcurrentHashMap<>();

    protected final DelayedScheduler timingScheduler;
    protected final Scheduler internalScheduler;
    protected final Scheduler interfaceScheduler;

    /**
     * Create a packet handler with a packet handler profile. <br/>
     * All of the scheduler should run on a same thread,
     * or you have to use <code>ConcurrentPacketHandler()</code> instead. <br/>
     * @param profile The packet handler profile
     *
     * @see ConcurrentPacketHandler
     */
    public PacketHandler(Profile profile) {
        this.messagePoster = profile.messagePoster;
        this.timingScheduler = profile.timingScheduler;
        this.internalScheduler = profile.internalScheduler;
        this.interfaceScheduler = profile.interfaceScheduler;
        this.specifiedListeners.putAll(profile.specifiedListeners);
        this.fallbackListeners.putAll(profile.fallbackListeners);
    }


    /**
     * Register the listener of a packet with specified version. <br/>
     * @param name The packet name
     * @param version The version of the packet
     * @param listener The listener ought to be registered
     * @return The previous registered listener
     */
    public PacketListener registerListener(String name, String version, PacketListener listener) {
        Map<String, PacketListener> versions = specifiedListeners.computeIfAbsent(name, k -> new HashMap<>());
        return versions.put(version, listener);
    }

    /**
     * Register the fallback listener of a packet. <br/>
     * @param name The packet name
     * @param listener The fallback listener ought to be registered
     * @return The previous registered listener
     */
    public PacketListener registerFallbackListener(String name, PacketListener listener) {
        return fallbackListeners.put(name, listener);
    }


    /**
     * Unregister a listener of a packet in specified version. <br/>
     * @param name The packet name
     * @param version The version of the packet
     * @return The listener registered before
     */
    public PacketListener unregisterListener(String name, String version) {
        Map<String, PacketListener> versions = specifiedListeners.get(name);
        if(versions == null) return null;
        PacketListener listener = versions.remove(version);
        if(versions.isEmpty()) specifiedListeners.remove(name);
        return listener;
    }

    /**
     * Unregister the fallback listener of a packet. <br/>
     * @param name The packet name
     * @return The listener registered before
     */
    public PacketListener unregisterFallbackListener(String name) {
        return fallbackListeners.remove(name);
    }

    /**
     * Unregister listeners of a packet in all versions, including the fallback. <br/>
     * @param name The packet name
     */
    public void unregisterAllListeners(String name) {
        specifiedListeners.remove(name);
        fallbackListeners.remove(name);
    }


    /**
     * Check if a listener of a packet with specified version is registered. <br/>
     * @param name The packet name
     * @param version The version of the packet
     * @return If listener of the packet with specified version is registered
     */
    public boolean isListenerRegistered(String name, String version) {
        Map<String, PacketListener> versions = specifiedListeners.get(name);
        if(versions == null) return false;
        return versions.containsKey(version);
    }

    /**
     * Check if a fallback listener of a packet is registered. <br/>
     * @param name The packet name
     * @return If fallback listener of the packet is registered
     */
    public boolean isFallbackRegistered(String name) {
        return fallbackListeners.containsKey(name);
    }


    /**
     * Check if a packet is supported. <br/>
     * If here's a fallback listener then the packet will always being supported. <br/>
     * @param name The packet name
     * @return If the packet is supported
     */
    public boolean isPacketSupported(String name) {
        return specifiedListeners.containsKey(name) || fallbackListeners.containsKey(name);
    }


    /**
     * Check if a version of a packet is supported. <br/>
     * If here's a fallback listener then the version will always being supported. <br/>
     * @param name The packet name
     * @param version The version of the packet
     * @return If the version of the packet is supported
     */
    public boolean isVersionSupported(String name, String version) {
        Map<String, PacketListener> versions = specifiedListeners.get(name);
        if(versions == null) return false;
        return versions.containsKey(version) | fallbackListeners.containsKey(name);
    }


    /**
     * Get all specified packet supported by name. <br/>
     * @return Packet names supported
     */
    public Set<String> getAllPacketNames() {
        Set<String> set = new HashSet<>();
        set.addAll(specifiedListeners.keySet());
        set.addAll(fallbackListeners.keySet());
        return set;
    }


    /**
     * Get all specified version supported of a packet name. <br/>
     * @param packetName the packet name
     * @return Versions of packets supported
     */
    public Set<String> getAllVersionSupported(String packetName) {
        Set<String> supportedVersions = new HashSet<>();
        Map<String, PacketListener> versions = specifiedListeners.get(packetName);
        if(versions != null) {
            supportedVersions.addAll(versions.keySet());
        }
        return supportedVersions;
    }


    /**
     * Send a packet without expectation of response. <br/>
     * @param packet The packet
     */
    public void sendPacket(Packet packet) {

        internalScheduler.schedule(() -> {
            Packet.IDAccessor.setId(packet, UUID.randomUUID());
            messagePoster.sendMessage(wrapPacket(Packet.SERIALIZER.serialize(packet)));
        });

    }

    /**
     * Send a packet with an expectation of response and a maximum waiting time for the response. <br/>
     * @param packet The packet
     * @param onRespond What to do when here's a response
     * @param timeoutMillis The maximum waiting time in milliseconds
     * @param onTimeout What to do when waiting time exceeds the maximum waiting time
     */
    public void sendPacket(Packet packet, CallbackArg1<Packet> onRespond, long timeoutMillis, CallbackArg0 onTimeout) {

        internalScheduler.schedule(() -> {
            UUID transactionId = UUID.randomUUID();
            Packet.IDAccessor.setId(packet, transactionId);
            messagePoster.sendMessage(wrapPacket(Packet.SERIALIZER.serialize(packet)));
            TaskRef taskRef = timingScheduler.schedule(() -> {
                waitList.remove(transactionId);
                onTimeout.onCallback();
            }, timeoutMillis);

            waitList.put(transactionId, new DeliverOrTimeout(onRespond, taskRef));
        });

    }


    protected void sendResponse(Packet packet) {
        internalScheduler.schedule(() -> messagePoster.sendMessage(wrapPacket(Packet.SERIALIZER.serialize(packet))));
    }

    protected ByteBuffer wrapPacket(ByteBuffer bytes) {
        bytes = bytes.duplicate();
        ByteBuffer message = ByteBuffer.allocate(4 + BYTES_PACKET_TYPE_ID.length + bytes.remaining());
        message.putInt(BYTES_PACKET_TYPE_ID.length);
        message.put(BYTES_PACKET_TYPE_ID);
        message.put(bytes);
        message.flip();
        return message;
    }

    protected void broadcastPacket(Packet packet) {

        UUID transactionId = Packet.IDAccessor.getId(packet);
        
        DeliverOrTimeout deliverOrTimeout = waitList.remove(transactionId);
        if(deliverOrTimeout != null) {
            if(deliverOrTimeout.onTimeout.cancel() != null) {
                interfaceScheduler.schedule(() -> deliverOrTimeout.onRespond.onCallback(packet));
            }
            return;
        }

        String name = packet.name;
        String version = packet.version;

        Map<String, PacketListener> versions = specifiedListeners.get(name);
        if(versions != null) {
            PacketListener listener = versions.get(version);
            if(listener != null) {
                interfaceScheduler.schedule(() -> {
                    Packet response = listener.onPacket(packet);
                    if(response != null) {
                        Packet.IDAccessor.setId(response, transactionId);
                        sendResponse(response);
                    }
                });

                return;
            }
        }

        PacketListener fallback = fallbackListeners.get(name);
        if(fallback != null) {
            interfaceScheduler.schedule(() -> {
                Packet response = fallback.onPacket(packet);
                if(response != null) {
                    Packet.IDAccessor.setId(response, transactionId);
                    sendResponse(response);
                }
            });
        }

    }

    @Override
    public void onMessage(ByteBuffer message) {
        internalScheduler.schedule(() -> {
            int tempLength = message.getInt();
            byte[] tempBytes = new byte[tempLength];
            message.get(tempBytes);
            if(!Arrays.equals(tempBytes, BYTES_PACKET_TYPE_ID)) return;
            Packet packet;
            try {
                packet = Packet.SERIALIZER.deserialize(message);
            } catch (Exception ex) {
                return;
            }
            broadcastPacket(packet);
        });
    }

    private static final class DeliverOrTimeout {
        public final CallbackArg1<Packet> onRespond;
        public final TaskRef onTimeout;

        public DeliverOrTimeout(CallbackArg1<Packet> onRespond, TaskRef onTimeout) {
            this.onRespond = onRespond;
            this.onTimeout = onTimeout;
        }
    }

    /**
     * Profile is needed for creating the packet handler, You have to set the profile properly. <br/>
     */
    public static final class Profile {

        /** The outgoing message poster */
        public MessagePoster messagePoster;

        /** The scheduler to handle timeout situations */
        public DelayedScheduler timingScheduler;

        /** The internal scheduler, which handles outgoing packets and incoming packet routing */
        public Scheduler internalScheduler;

        /** The incoming scheduler, which used to deliver packet to end response callbacks and listeners */
        public Scheduler interfaceScheduler;

        /** Preset listeners */
        public Map<String, Map<String, PacketListener>> specifiedListeners;

        /** Preset fallback listeners */
        public Map<String, PacketListener> fallbackListeners;
    }

}
