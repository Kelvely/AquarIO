package ink.aquar.util.storage.oo;

import ink.aquar.util.io.Serializer;

/**
 * A data object in local. <br/>
 * Use <code>RemoteStructObject.set(..., localDataObject, ...)</code>
 * can upload this object and make a field refers to the remote uploaded object. <br/>
 * You need a serializer to get and set data in a local data object. <br/>
 *
 * @author Kelby Iry
 */
public interface LocalDataObject extends LocalObject {

    /**
     * Get the data in this local data object. <br/>
     * @param serializer The data serializer that the type it can handle matches the type of data inside
     * @param <T> The type of the data stored inside
     * @return The data stored inside
     * @throws ink.aquar.util.io.TypeNotMatchException Thrown when the type the serializer can handle doesn't match the data type of this
     * @throws ink.aquar.util.io.DeserializationException Thrown when the serializer can not deserialize the raw data inside
     */
    public <T> T getData(Serializer<T> serializer);

    /**
     * Set the data in this local data object. <br/>
     * @param data The data to set
     * @param serializer The data serializer that the type it can handle matches the type of data to set
     * @param <T> The type of the data to set
     * @throws ink.aquar.util.io.TypeNotMatchException Thrown when the type the serializer can handle doesn't match the data to set
     */
    public <T> void setData(T data, Serializer<T> serializer);

    /**
     * Get the size of the raw data inside, which will be stored inside the database. <br>
     * @return The size of the raw data inside
     */
    public int size();

    /**
     * Create a local data object. <br/>
     * @return A local data object
     */
    public static LocalDataObject create() {
        return new CommonLocalDataObject();
    }

}
