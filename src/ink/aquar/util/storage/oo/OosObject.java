package ink.aquar.util.storage.oo;

/**
 * Base interface of <code>LocalObject</code> and <code>RemoteObject</code>, because you can add both
 * remote objects and local objects into local struct object, and using <code>RemoteStructObject.set(...,null, ...)
 * may cause ambiguity problem, so this interface has been created! <br/>
 *
 * @author Kelby Iry
 */
public interface OosObject {

    /**
     * Check if this OOS object is a remote object reference
     * @return If this OOS object is a remote object reference
     */
    public default boolean isRemote() {
        return this instanceof RemoteObject;
    }

    /**
     * Cast this OOS object into a remote object reference
     * @return This OOS object in remote object reference form
     * @throws ClassCastException Thrown when this OOS object is actually not a remote object reference
     */
    public default RemoteObject asRemote() {
        return (RemoteObject) this;
    }

    /**
     * Check if this OOS object is a local object
     * @return If this OOS object is a local object
     */
    public default boolean isLocal() {
        return this instanceof LocalObject;
    }

    /**
     * Cast this OOS object into a local object reference
     * @return This OOS object in local object form
     * @throws ClassCastException Thrown when this OOS object is actually not a local object
     */
    public default LocalObject asLocal() {
        return (LocalObject) this;
    }





}
