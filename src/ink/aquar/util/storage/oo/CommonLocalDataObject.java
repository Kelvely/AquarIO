package ink.aquar.util.storage.oo;

import ink.aquar.util.io.Serializer;
import ink.aquar.util.io.TypeNotMatchException;

import java.nio.ByteBuffer;

public class CommonLocalDataObject implements LocalDataObject {

    private ByteBuffer bytes;
    private String typeId;

    /**
     * Extend this class to make it accessible. <br/>
     */
    protected CommonLocalDataObject() { }

    @Override
    public <T> T getData(Serializer<T> serializer) {
        if(!getTypeIdentifier().equals(serializer.getTypeIdentifier()))
            throw new TypeNotMatchException("Type not match: " + getTypeIdentifier() + ", " + serializer.getTypeIdentifier());
        return serializer.deserialize(getBytes());
    }

    @Override
    public <T> void setData(T data, Serializer<T> serializer) {
        setTypeIdentifier(serializer.getTypeIdentifier());
        setBytes(serializer.serialize(data));
    }

    @Override
    public int size() {
        return bytes.remaining();
    }

    /**
     * Get the stored bytes. <br/>
     * <br/>
     * Extend this class to make it accessible. <br/>
     * @return The stored bytes
     */
    public ByteBuffer getBytes() {
        return bytes;
    }

    /**
     * Set the bytes stored. <br/>
     * <br/>
     * Extend this class to make it accessible. <br/>
     * @param bytes The bytes that is ought to be stored in the object
     */
    public void setBytes(ByteBuffer bytes) {
        this.bytes = bytes;
    }

    /**
     * Get the type identifier of the specified serializer. <br/>
     * <br/>
     * Extend this class to make it accessible. <br/>
     * @return The type identifier
     */
    public String getTypeIdentifier() {
        return typeId;
    }

    /**
     * Set the type identifier to specified serializer. <br/>
     * <br/>
     * Extend this class to make it accessible. <br/>
     * @param typeIdentifier The type identifier that is ought to be set
     */
    public void setTypeIdentifier(String typeIdentifier) {
        typeId = typeIdentifier;
    }
}
