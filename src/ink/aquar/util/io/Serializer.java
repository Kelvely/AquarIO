package ink.aquar.util.io;

import java.nio.ByteBuffer;

/**
 * Serializers aim to turn objects into bytes, which in order to store in your disk, or transfer on network, etc. <br/>
 * How could it work? Define it your own or use easy serializers provided! <br/>
 *
 * @param <T> The type that the serializer handles
 *
 * @author Kelby Iry
 */
public interface Serializer<T> {

    /**
     * De-serialize from raw bytes to an object. <br/>
     * check it outside this method is optional but recommended. <br/>
     * It will assume that the position byte buffer provided is the start point of reading, and implementation
     * should not change the position of provided byte buffer, please use <code>ByteBuffer.duplicate()</code>
     * to create another byte buffer with same bytes and position with the previous one. <br/>
     * @param raw The raw bytes, the position of the byte buffer will assumed as the position start to read
     * @return The de-serialized object from raw bytes provided
     * @throws DeserializationException Thrown when something went wrong while de-serializing bytes,
     * data is damaged for example
     * @throws TypeNotMatchException Thrown when the type doesn't match while checking the type identifier
     */
    public T deserialize(ByteBuffer raw);

    /**
     * Serialize from an object to bytes. <br/>
     * check it outside this method is optional but recommended. <br/>
     * The returned byte buffer should have a position in 0. <br/>
     * @param data The raw bytes, the position of the byte buffer will assumed as the position start to read
     * @return The serialized bytes from the object provided
     */
    public ByteBuffer serialize(T data);

    /**
     * Get a type identifier that can check the type outside to avoid weird things to happen. <br/>
     * You should check type identifier outside instead of in the de-serialization process. <br/>
     * @return The type identifier
     */
    public String getTypeIdentifier();


}
