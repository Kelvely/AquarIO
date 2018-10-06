package ink.aquar.util.storage.oo;

import ink.aquar.util.concurrent.callback.CallbackArg0;
import ink.aquar.util.concurrent.callback.CallbackArg1;
import ink.aquar.util.misc.Entry;

import java.util.List;

/**
 * A reference to a remote struct object. <br/>
 * A struct object has fields that refers to other objects. <br/>
 * If you want to get all fields inside, use <code>retrieve()</code>, because struct object stores only fields. <br/>
 *
 * @author Kelby Iry
 */
public interface RemoteStructObject extends RemoteObject {

    // Auto newing object ;) new and set empty object with set(..., new LocalObject(), ...);

    /**
     * Get the remote reference of a member object, or a member of a member object,
     * or a member of a member of a member object... <br/>
     *
     * @param path Field path to the desired member object
     * @param callback What to do when the the member object reference is gotten
     * @param exHandle What to do when exception occur while getting the reference of the member object
     */
    public void get(FieldPath path, CallbackArg1<RemoteObject> callback, CallbackArg1<? super Exception> exHandle);

    /**
     * Get remote references of multiple member objects, or multiple member of multiple member objects, or multiple
     * member of multiple member of multiple member objects <br/>
     * For remote object references gotten in callback arguments, the index corresponds to the index of field paths,
     * meaning if the path and the remote object reference has the same index in the array,
     * the remote object reference gotten is belongs to the field in this path. <br/>
     *
     * @param paths An array of paths to the desired member objects
     * @param callback What to do when the member object references are gotten
     * @param exHandle What to do when exception occur while getting the member objects
     */
    public void get(FieldPath[] paths, CallbackArg1<RemoteObject[]> callback, CallbackArg1<? super Exception> exHandle);


    
    /**
     * Set the object a field refers to in remote. <br/>
     * If the object is a local object,
     * then the remote will create an object with what in this object, and make the field refers to this object,
     * otherwise if it is a remote object it will just make the field refers to the corresponding object in remote. <br/>
     *
     * @param path Field path that is desired to refer the object
     * @param object The object that the field should refers
     * @param callback What to do when the field is set to refer the object successfully
     * @param exHandle What to do when exception occur while setting a field to refer the object
     */
    public void set(FieldPath path, OosObject object, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle); // Contains both remote and local operation, to avoid ambiguity when using null.

    /**
     * Set the objects multiple fields refer to in remote. <br/>
     * If the object is a local object,
     * then the remote will create an object with what in this object, and make the field refers to this object,
     * otherwise if it is a remote object it will just make the field refers to the corresponding object in remote. <br/>
     *
     * @param entries An array of entries is desired to update so that each field will refers to each object
     * @param callback What to do when each field is set to refer each object successfully
     * @param exHandle What to do when exception occur while setting fields to refer objects
     */
    public void set(Entry<FieldPath, OosObject>[] entries, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle);


    /**
     * Set the object a field refers to in remote and get the reference previous object that the field refers to. <br/>
     * If the object is a local object,
     * then the remote will create an object with what in this object, and make the field refers to this object,
     * otherwise if it is a remote object it will just make the field refers to the corresponding object in remote. <br/>
     * The object is nullable, and if it is null then the field will refers to null in remote. <br/>
     *
     * @param path Field path that is desired to refer the object
     * @param object The object that the field should refers
     * @param callback What to do when the field is set to refer the object and get the previous object reference
     * @param exHandle What to do when exception occur while setting a field to refer the object and getting the
     *                 previous object reference that the field refers to
     */
    public void set(FieldPath path, OosObject object, CallbackArg1<RemoteObject> callback, CallbackArg1<? super Exception> exHandle); // Contains both remote and local operation, to avoid ambiguity when using null.

    /**
     * Set the objects multiple fields refer to in remote and get each reference of
     * the previous object that each field refers to. <br/>
     * If the object is a local object,
     * then the remote will create an object with what in this object, and make the field refers to this object,
     * otherwise if it is a remote object it will just make the field refers to the corresponding object in remote. <br/>
     * For remote object references gotten in callback arguments, the index corresponds to the index of entries that
     * the field paths are, meaning if the entry of the path and the remote object reference has the same index in
     * the array, the remote object reference gotten is belongs to the field in this path. <br/>
     *
     * @param entries An array of entries that is desired to update so that each field will refers to each object
     * @param callback What to do when each field is set to refer each object and previous objects are gotten
     * @param exHandle What to do when exception occur while setting fields to refer objects and getting the
     *                 previous object references that those fields refers to
     */
    public void set(Entry<FieldPath, OosObject>[] entries, CallbackArg1<RemoteObject[]> callback, CallbackArg1<? super Exception> exHandle);



    //public void set(FieldPath path, StreamLocalObject object, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle);

    //public void set(FieldPath path, StreamLocalObject object, CallbackArg1<RemoteObject> callback, CallbackArg1<? super Exception> exHandle);

    /**
     * Remove a field in remote. <br/>
     * It just removing the field, but not deleting the object that it refers to,
     * nor setting the field refers to null. <br/>
     * The only difference between set a field refer to null and remove the field is using <code>hasField()</code>,
     * setting to null will have the result of true, but removing will be false. <br/>
     *
     * @param path The path of field that is desired to remove
     * @param callback What to do when the field is removed successfully
     * @param exHandle What to do when exception occur while removing the field
     */
    public void remove(FieldPath path, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle);

    /**
     * Remove multiple fields in remote. <br/>
     * It just removing the field, but not deleting the object that it refers to,
     * nor setting the field refers to null. <br/>
     * The only difference between set a field refer to null and remove the field is using <code>hasField()</code>,
     * setting to null will have the result of true, but removing will be false. <br/>
     *
     * @param paths An array of path of fields that is desired to remove
     * @param callback What to do when fields are removed successfully
     * @param exHandle What to do when exception occur while removing fields
     */
    public void remove(FieldPath[] paths, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle);

    /**
     * Remove a field in remote and get the remote object reference of the object that the field refers to. <br/>
     * It just removing the field, but not deleting the object that it refers to,
     * nor setting the field refers to null. <br/>
     * The only difference between set a field refer to null and remove the field is using <code>hasField()</code>,
     * setting to null will have the result of true, but removing will be false. <br/>
     *
     * @param path The path of field that is desired to remove and to get the object reference it refers to
     * @param callback What to do when the field is removed and got the object reference it refers to
     * @param exHandle What to do when exception occur while removing the field and getting the
     *                 object reference it refers to
     */
    public void remove(FieldPath path, CallbackArg1<RemoteObject> callback, CallbackArg1<? super Exception> exHandle);

    /**
     * Remove multiple fields in remote and get each reference of the object that each field refers to. <br/>
     * It just removing the field, but not deleting the object that it refers to,
     * nor setting the field refers to null. <br/>
     * The only difference between set a field refer to null and remove the field is using <code>hasField()</code>,
     * setting to null will have the result of true, but removing will be false. <br/>
     * For remote object references gotten in callback arguments, the index corresponds to the index of field paths,
     * meaning if the path and the remote object reference has the same index in the array,
     * the remote object reference gotten is belongs to the field in this path. <br/>
     *
     * @param paths An array of paths of fields that are desired to remove and to get objects reference they refer to
     * @param callback What to do when fields are removed and got object references they refer to
     * @param exHandle What to do when exception occur while removing fields and getting object
     *                 references that they refer to
     */
    public void remove(FieldPath[] paths, CallbackArg1<RemoteObject[]> callback, CallbackArg1<? super Exception> exHandle);


    
    /**
     * Check if a field exists. <br/>
     * A field refers to null still exists :( <br/>
     * @param path The path of fields that is desired to check its existence
     * @param callback What to do when the result is gotten
     * @param exHandle What to do when exception occurred wile getting if the field exists
     */
    public void hasField(FieldPath path, CallbackArg1<Boolean> callback, CallbackArg1<? super Exception> exHandle);

    /**
     * Check if fields are existed. <br/>
     * A field refers to null still exists :( <br/>
     * For results gotten in callback arguments, the index corresponds to the index of field paths, meaning if the path
     * and the result has the same index in the array, the result gotten is belongs to the field in this path. <br/>
     * @param paths An array of paths of fields that are desired to check their existences
     * @param callback What to do when results are gotten
     * @param exHandle What to do when exception occurred while getting if those fields are existed
     */
    public void hasFields(FieldPath[] paths, CallbackArg1<Boolean[]> callback, CallbackArg1<? super Exception> exHandle);


    
    /**
     * Retrieve the object that a field path refers to from remote. <br/>
     * retrieving a struct object will not retrieve the member objects of the object,
     * but the references of these objects instead. <br/>
     * The Java object type of the returning local object depends on the actual object type in remote. <br/>
     * @param path The path of field that refers to the object that is desired to retrieve
     * @param callback What to do when the object is retrieved successfully
     * @param exHandle What to do when exception occurred while retrieving the object
     */
    public void retrieve(FieldPath path, CallbackArg1<LocalObject> callback, CallbackArg1<? super Exception> exHandle);

    /**
     * Retrieve objects that an array of field paths refer to from remote. <br/>
     * retrieving a struct object will not retrieve the member objects of the object,
     * but the references of these objects instead. <br/>
     * The Java object type of the returning local object depends on the actual object type in remote. <br/>
     * For objects gotten in callback arguments, the index corresponds to the index of field paths, meaning if the path
     * and the object has the same index in the array, the object gotten is belongs to the field in this path. <br/>
     * @param paths An array of paths of fields that are desired to retrieve
     * @param callback What to do when objects are retrieved successfully
     * @param exHandle What to do when exception occur while retrieving objects
     */
    public void retrieve(FieldPath[] paths, CallbackArg1<LocalObject[]> callback, CallbackArg1<? super Exception> exHandle);


    /**
     * Lock the object that a field path refers to in remote, and get a statement of locking. <br/>
     * It will wait <b>FOREVER</b> until the object has been locked successfully,
     * invoking <code>LockStatement.unlock()</code>, or disconnection. <br/>
     * Calling <code>LockStatement.unlock</code> returned by this method can manually stop the waiting. <br/>
     * Read documentation of <code>LockStatement</code> carefully before you use lock functions. <br/>
     * @param path The path of field that refers to the object that is desired to lock
     * @param lockType The type of lock, can be either read lock or write lock
     * @param callback What to do when the object has been locked successfully, the lock statement called is a direct
     *                 reference of the lock statement returned by calling this method
     * @param exHandle What to do when some exception occur while locking the object
     * @return A statement of locking
     * @see LockStatement
     */
    public LockStatement lock(FieldPath path, LockType lockType, CallbackArg1<LockStatement> callback, CallbackArg1<? super Exception> exHandle);
    //public List<Entry<FieldPath, LockStatement>> lock(List<FieldPath> path, LockType lockType, CallbackArg1<List<Entry<FieldPath, LockStatement>>> callback, CallbackArg1<? super Exception> exHandle); // NO, WHO WILL LOCK SUCH MANY OBJECTS AT ONE TIME???


    /**
     * Lock the object that a field path refers to in remote, and get a statement of locking. <br/>
     * It will wait until the waiting time exceed the maximum waiting time.
     * The waiting can be also stopped when the object has been locked successfully,
     * invoking <code>LockStatement.unlock()</code>, or disconnection. <br/>
     * Calling <code>LockStatement.unlock</code> returned by this method can manually stop the waiting. <br/>
     * Read documentation of <code>LockStatement</code> carefully before you use lock functions. <br/>
     * @param path The path of field that refers to the object that is desired to lock
     * @param lockType The type of lock, can be either read lock or write lock
     * @param timeoutMillis The maximum time to wait for locking the object in milliseconds
     * @param callback What to do when the object has been locked successfully, the lock statement called is a direct
     *                 reference of the lock statement returned by calling this method
     * @param onTimeout What to do when time is out
     * @param exHandle What to do when some exception occur while locking the object
     * @return A statement of locking
     * @see LockStatement
     */
    public LockStatement lock(FieldPath path, LockType lockType, long timeoutMillis, CallbackArg1<LockStatement> callback, CallbackArg0 onTimeout, CallbackArg1<? super Exception> exHandle);
    //public List<Entry<FieldPath, LockStatement>> lock(List<FieldPath> path, LockType lockType, long timeoutMillis, CallbackArg1<List<Entry<FieldPath, LockStatement>>> callback, CallbackArg1<? super Exception> exHandle); // YA, NOBODY WILL LOCK SUCH MANY OBJECT AT ONE TIME!!


    /**
     * Try to lock an object  that a field path refers to in remote, and get a statement of locking. <br/>
     * If the object is already been locked, it will not wait for the lock, otherwise it will lock the object. <br/>
     * Check <code>LockStatement.isLocked()</code> in callback to know if the object is locked successfully. <br/>
     * Read documentation of <code>LockStatement</code> carefully before you use lock functions. <br/>
     * @param path The path of field that refers to the object that is desired to try locking
     * @param lockType The type of lock, can be either read lock or write lock
     * @param callback What to do when the object has been locked successfully, the lock statement called is a direct
     *                 reference of the lock statement returned by calling this method
     * @param exHandle What to do when some exception occur while trying to lock the object
     * @return A statement of locking
     * @see LockStatement
     */
    public LockStatement tryLock(FieldPath path, LockType lockType, CallbackArg1<LockStatement> callback, CallbackArg1<? super Exception> exHandle);
    //public List<Entry<FieldPath, LockStatement>> tryLock(List<FieldPath> path, LockType lockType, CallbackArg1<List<Entry<FieldPath, LockStatement>>> callback, CallbackArg1<? super Exception> exHandle); // SO, WE SHOULD REMOVE THESE!

}
