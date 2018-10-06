package ink.aquar.util.storage.oo;

/**
 * An OOS object that is in local. <br/>
 * Use <code>RemoteStructObject.set(..., localDataObject, ...)</code>
 * can upload this object and make a field refers to the remote uploaded object. <br/>
 *
 * @author Kelby Iry
 */
public interface LocalObject extends OosObject {

    /**
     * Check if this local object is a local struct object. <br/>
     * @return If this local object is a local struct object
     */
    public default boolean isStruct() {
        return this instanceof LocalStructObject;
    }

    /**
     * Cast this local object into a local struct object. <br/>
     * @return This local object in local struct object form
     * @throws ClassCastException Thrown when this local object is actually not a local struct object
     */
    public default LocalStructObject asStruct() {
        return (LocalStructObject) this;
    }

    /**
     * Check if this local object is a local data object. <br/>
     * @return If this local object is a local data object
     */
    public default boolean isData() {
        return this instanceof LocalDataObject;
    }

    /**
     * Cast this local object into a local data object. <br/>
     * @return This local object in local data object form
     * @throws ClassCastException Thrown when this local object is actually not a local data object
     */
    public default LocalDataObject asData() {
        return (LocalDataObject) this;
    }

}
