package ink.aquar.util.misc;

import ink.aquar.util.io.DeserializationException;
import ink.aquar.util.io.Serializer;
import ink.aquar.util.io.TypeNotMatchException;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

/**
 * This is an implementation of serializer that can handle UUID serialization and de-serialization. <br/>
 *
 * @author Kelby Iry
 */
public class UUIDSerializer implements Serializer<UUID> {

    /** Don't new an UUID serializer, just get it from here ;) */
    public static final Serializer<UUID> SERIALIZER = new UUIDSerializer();

    protected static final String TYPE_ID = "AQUAR_MISC_UUID";
    protected static final byte[] BYTES_TYPE_ID = TYPE_ID.getBytes();

    @Override
    public UUID deserialize(ByteBuffer raw) {
        raw = raw.duplicate();

        int tempLength;
        byte[] tempBytes;

        /*if(raw.remaining() < 4) throw new TypeNotMatchException("Unknown type while de-serializing packets!");
        tempLength = raw.getInt();
        if(raw.remaining() < tempLength) throw new TypeNotMatchException("Unknown type while de-serializing packets!");
        tempBytes = new byte[tempLength];
        raw.get(tempBytes);
        if(!Arrays.equals(tempBytes, BYTES_TYPE_ID)) throw new TypeNotMatchException(
                "Can't deserialize a raw packet with type identifier "+ new String(tempBytes)
                        +" using serializer that manages types with type identifier "
                        + TYPE_ID +"!"); */

        if(raw.remaining() < 16) throw new DeserializationException();
        long msb = raw.getLong();
        long lsb = raw.getLong();
        return new UUID(msb, lsb);
    }

    @Override
    public ByteBuffer serialize(UUID data) {
        /*ByteBuffer byteBuffer = ByteBuffer.allocate(4 + BYTES_TYPE_ID.length + 16);
        byteBuffer.putInt(BYTES_TYPE_ID.length);
        byteBuffer.put(BYTES_TYPE_ID); */
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);

        byteBuffer.putLong(data.getMostSignificantBits());
        byteBuffer.putLong(data.getLeastSignificantBits());
        byteBuffer.flip();
        return byteBuffer;
    }

    @Override
    public String getTypeIdentifier() {
        return TYPE_ID;
    }

}
