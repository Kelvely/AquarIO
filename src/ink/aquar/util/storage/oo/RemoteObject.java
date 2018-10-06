package ink.aquar.util.storage.oo;

import ink.aquar.util.concurrent.callback.CallbackArg0;
import ink.aquar.util.concurrent.callback.CallbackArg1;

/**
 * A reference to a remote object. <br/>
 * <code>RemoteObject</code> should not be created locally, because it is referring to a remote object somewhere.
 * However, you can make the constructor to establish connections to the database, and this is okay,
 * but <code>RemoteObject.createConnection()</code> should make a better readability. <br/>
 * An OOS object can be either struct or data, though an object both can be struct and data object,
 * however, it make the object not easy to understand as much. <br/>
 * <br/>
 * The Java object type of this remote object reference depends on the actual object type in remote,
 * so here should be no need to make a remote query to know what kind of type it is.
 * If it refers to a data object, then this reference should be a <code>RemoteDataObject<code/>, vice versa. <br/>
 * <br/>
 * Notice if you are using GC in remote, please implement <code>finalize()</code> so that you can tell
 * the remote that you are no more referencing this object. What's more is in remote, referencing
 * remotely should be associated with processes, so that it can remove those referencing records if
 * the process is terminated before the program tell the database that it is not reference it any more. <br/>
 * <br/>
 * Let's talk about connection. Connection may fails before referencing records is being removed.
 * So we can make referencing records associated with connections, and the connection close or fails,
 * remote will automatically remove the referencing records. However, most of the time we use connection
 * pools to make connection more reliable. However, when records are associated with connections, a remote
 * object obtained from a connection SHOULD <b>NOT</b> and <b>CANNOT</b> be used in another connection. <br/>
 * However, we need to enhance the connection. We use sub-connections and virtual connections.
 * On remote, here's a list that stores sub-connection belongs to virtual connections, multiple sub-connections
 * can belong to a virtual connection, and referencing records associate with the virtual connections.
 * This can properly solve both problems. <br/>
 * For implementations, connection should be closed when last remote object reference got from the connection
 * has been collected by the garbage collector. Using <code>Connection.finalize()</code> should work properly. <br/>
 * <br/>
 * Planed to make streaming functions so that can store large size data without
 * experiencing <code>HeapOverflowException</code>s. ;) <br/>
 *
 * @author Kelby Iry
 */
public interface RemoteObject extends OosObject {

    /**
     * Check if this object is a struct object. <br/>
     * The Java object type of this remote object reference depends on the actual object type in remote,
     * so here should be no need to make a remote query to know what kind of type it is. <br/>
     * @return If this object is a struct object
     */
    public default boolean isStruct() {
        return this instanceof RemoteStructObject;
    }

    /**
     * Cast this object reference into a struct object reference. <br/>
     * The Java object type of this remote object reference depends on the actual object type in remote,
     * so here should be no need to make a remote query to know what kind of type it is. <br/>
     * @return This object in <code>RemoteStructObject</code> form
     * @throws ClassCastException Thrown when this object actually is not a remote struct object reference
     */
    public default RemoteStructObject asStruct() {
        return (RemoteStructObject) this;
    }

    /**
     * Check if this object is a data object. <br/>
     * The Java object type of this remote object reference depends on the actual object type in remote,
     * so here should be no need to make a remote query to know what kind of type it is. <br/>
     * @return If this object is a struct object
     */
    public default boolean isData() {
        return this instanceof RemoteDataObject;
    }


    /**
     * Cast this object reference into a data object reference. <br/>
     * The Java object type of this remote object reference depends on the actual object type in remote,
     * so here should be no need to make a remote query to know what kind of type it is. <br/>
     * @return This object in <code>RemoteDataObject</code> form
     * @throws ClassCastException Thrown when this object actually is not a remote data object reference
     */
    public default RemoteDataObject asData() {
        return (RemoteDataObject) this;
    }

    /**
     * Retrieve the object from remote. <br/>
     * retrieving a struct object will not retrieve the member objects of the object,
     * but the references of these objects instead. <br/>
     * The Java object type of the returning local object depends on the actual object type in remote. <br/>
     * @param callback What to do when this object is retrieved successfully
     * @param exHandle What to do when here's an exception occurred retrieving the object in remote
     */
    public void retrieve(CallbackArg1<LocalObject> callback, CallbackArg1<? super Exception> exHandle);

    /**
     * Lock the object in remote, and get a statement of locking. <br/>
     * It will wait <b>FOREVER</b> until the object has been locked successfully,
     * invoking <code>LockStatement.unlock()</code>, or disconnection. <br/>
     * Calling <code>LockStatement.unlock</code> returned by this method can manually stop the waiting. <br/>
     * Read documentation of <code>LockStatement</code> carefully before you use lock functions. <br/>
     * @param lockType The type of lock, can be either read lock or write lock
     * @param callback What to do when the object has been locked successfully, the lock statement called is a direct
     *                 reference of the lock statement returned by calling this method
     * @param exHandle What to do when some exception occur while locking the object
     * @return A statement of locking
     * @see LockStatement
     */
    public LockStatement lock(LockType lockType, CallbackArg1<LockStatement> callback, CallbackArg1<? super Exception> exHandle);

    /**
     * Lock the object in remote, and get a statement of locking. <br/>
     * It will wait until the waiting time exceed the maximum waiting time.
     * The waiting can be also stopped when the object has been locked successfully,
     * invoking <code>LockStatement.unlock()</code>, or disconnection. <br/>
     * Calling <code>LockStatement.unlock</code> returned by this method can manually stop the waiting. <br/>
     * Read documentation of <code>LockStatement</code> carefully before you use lock functions. <br/>
     * @param lockType The type of lock, can be either read lock or write lock
     * @param timeoutMillis The maximum time to wait for locking the object in milliseconds
     * @param callback What to do when the object has been locked successfully, the lock statement called is a direct
     *                 reference of the lock statement returned by calling this method
     * @param onTimeout What to do when time is out
     * @param exHandle What to do when some exception occur while locking the object
     * @return A statement of locking
     * @see LockStatement
     */
    public LockStatement lock(LockType lockType, long timeoutMillis, CallbackArg1<LockStatement> callback, CallbackArg0 onTimeout, CallbackArg1<? super Exception> exHandle);

    /**
     * Try to lock an object in remote, and get a statement of locking. <br/>
     * If the object is already been locked, it will not wait for the lock, otherwise it will lock the object. <br/>
     * Check <code>LockStatement.isLocked()</code> in callback to know if the object is locked successfully. <br/>
     * Read documentation of <code>LockStatement</code> carefully before you use lock functions. <br/>
     * @param lockType The type of lock, can be either read lock or write lock
     * @param callback What to do when the object has been locked successfully, the lock statement called is a direct
     *                 reference of the lock statement returned by calling this method
     * @param exHandle What to do when some exception occur while trying to lock the object
     * @return A statement of locking
     * @see LockStatement
     */
    public LockStatement tryLock(LockType lockType, CallbackArg1<LockStatement> callback, CallbackArg1<? super Exception> exHandle);



}
