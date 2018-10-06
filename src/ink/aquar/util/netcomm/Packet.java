package ink.aquar.util.netcomm;

import ink.aquar.util.io.DeserializationException;
import ink.aquar.util.io.Serializer;
import ink.aquar.util.io.TypeNotMatchException;
import ink.aquar.util.misc.UUIDSerializer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

/**
 * A packet that store an object. <br/>
 * Technically it just store the serialized bytes of the object so that you have to provide a serializer,
 * and the packet has type name and version for packet handler to route to the corresponding listener. <br/>
 *
 * @author Kelby Iry
 */
public class Packet {

    /** The serializer make a packet into bytes */
    public static final Serializer<Packet> SERIALIZER = new PacketSerializer();

    /** The packet name, which is used as a type of packet */
    public final String name;
    /** The version of the packet */
    public final String version;
    private ByteBuffer bytes;
    private UUID transactionId;

    /**
     * Create packet with specified name and version. <br/>
     * @param name The packet name, which is used as a type of packet
     * @param version The version of the packet
     */
    public Packet(String name, String version) {
        this.name = name;
        this.version = version;
    }

    /**
     * Get the object inside the packet. <br/>
     * @param serializer The serializer that matches the type of the object
     * @param <T> The type of the object
     * @return The object
     */
    public <T> T getData(Serializer<T> serializer) {
        return serializer.deserialize(bytes);
    }

    /**
     * Set the object inside the packet. <br/>
     * @param data The object
     * @param serializer The serializer that matches the type of the object
     * @param <T> The type of the object
     */
    public <T> void setData(T data, Serializer<T> serializer) {
        bytes = serializer.serialize(data);
    }

    private static class PacketSerializer implements Serializer<Packet> {

        private static final String TYPE_IDENTIFIER = "AQUAR_IO_NET_COMM_PACKET_P1";

        @Override
        public Packet deserialize(ByteBuffer raw) {
            raw = raw.duplicate();

            int tempLength;
            byte[] tempBytes;

            /*
            // Check type of data
            if(raw.remaining() < 4) throw new TypeNotMatchException("Unknown type while de-serializing packets!");
            tempLength = raw.getInt();
            if(raw.remaining() < tempLength) throw new TypeNotMatchException("Unknown type while de-serializing packets!");
            tempBytes = new byte[tempLength];
            raw.get(tempBytes);
            if(!Arrays.equals(tempBytes, BYTES_TYPE_IDENTIFIER)) throw new TypeNotMatchException(
                    "Can't deserialize a raw packet with type identifier "+ new String(tempBytes)
                    +" using serializer that manages types with type identifier "
                    + TYPE_IDENTIFIER +"!"); */

            // Packet name
            if(raw.remaining() < 4) throw new DeserializationException();
            tempLength = raw.getInt();
            if(raw.remaining() < tempLength) throw new DeserializationException();
            tempBytes = new byte[tempLength];
            raw.get(tempBytes);
            String name = new String(tempBytes);

            // Packet version
            if(raw.remaining() < 4) throw new DeserializationException();
            tempLength = raw.getInt();
            if(raw.remaining() < tempLength) throw new DeserializationException();
            tempBytes = new byte[tempLength];
            raw.get(tempBytes);
            String version = new String(tempBytes);

            // Transaction UUID
            if(raw.remaining() < 16) throw new DeserializationException();
            tempBytes = new byte[16];
            raw.get(tempBytes);
            UUID transactionId = UUIDSerializer.SERIALIZER.deserialize(ByteBuffer.wrap(tempBytes));

            // Content
            if(raw.remaining() < 4) throw new DeserializationException();
            tempLength = raw.getInt();
            if(raw.remaining() < tempLength) throw new DeserializationException();
            tempBytes = new byte[tempLength];
            raw.get(tempBytes);

            Packet packet = new Packet(name, version);
            packet.bytes = ByteBuffer.wrap(tempBytes);
            IDAccessor.setId(packet, transactionId);

            return packet;
        }

        @Override
        public ByteBuffer serialize(Packet data) {
            ByteBuffer bytesOfData = data.bytes;
            byte[] bytesOfName = data.name.getBytes();
            byte[] bytesOfVersion = data.version.getBytes();

            ByteBuffer byteBuffer = ByteBuffer.allocate(
                    // 4 + BYTES_TYPE_IDENTIFIER.length     // Packet type id
                            + 4 + bytesOfName.length     // Packet name
                            + 4 + bytesOfVersion.length  // Packet version
                            + 16 +                       // Transaction UUID
                            4 + bytesOfData.remaining()  // Content
            );
            byteBuffer
                    //.putInt(BYTES_TYPE_IDENTIFIER.length).put(BYTES_TYPE_IDENTIFIER) // Packet type ID
                    .putInt(bytesOfName.length).put(bytesOfName)                     // Packet name
                    .putInt(bytesOfVersion.length).put(bytesOfVersion)               // Packet version
                    .put(UUIDSerializer.SERIALIZER.serialize(data.transactionId))    // Transaction UUID
                    .putInt(bytesOfData.remaining()).put(bytesOfData.duplicate());   // Content
            byteBuffer.flip();
            return byteBuffer;
        }

        @Override
        public String getTypeIdentifier() {
            return TYPE_IDENTIFIER;
        }

    }

    /**
     * An accessor to access the transaction ID of the packet. <br/>
     */
    public static class IDAccessor {

        /**
         * Get the transaction ID <br/>
         * @param packet The packet
         * @return The transaction ID of the packet
         */
        public static UUID getId(Packet packet) {
            return packet.transactionId;
        }

        /**
         * Set the transaction ID <br/>
         * @param id The transaction ID that the packet should set to
         */
        public static void setId(Packet packet, UUID id) {
            packet.transactionId = id;
        }

    }

}
