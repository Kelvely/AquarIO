package ink.aquar.util.storage.oo;

import ink.aquar.util.concurrent.callback.CallbackArg1;

/**
 * This is a statement of locking, when you locking or trying to lock, you should get a lock statement. <br/>
 * An object can only be locked by one lock statement, and you <b>HAVE</b> to unlock it by the lock statement
 * that you got when you locking the object, otherwise it can only be unlocked through disconnections(Any kind of,
 * including connection close, power cable cut both on connector and database, etc). <br/>
 * <br/>
 * An object locking state belongs to a lock statement, not a connection, a process,
 * nor a thread, but a lock statement. <br/>
 * <br/>
 * A Lock is read-write lock by default,
 * so if you don't know how to use read-write lock then just use write lock always.
 * If you are <b>TOO</b> curious to know what is or how to use a read-write lock then JFGI! <br/>
 *
 * @author Kelby Iry
 */
public interface LockStatement {

    /**
     * Unlock an object in remote. <br/>
     * If this lock statement doesn't lock the object, it does nothing and will call back. <br/>
     * If this lock statement is waiting for locking the object,
     * calling this method will stop waiting and cancel the locking request. <br/>
     * @param callback What to do when the the lock is unlocked
     * @param exHandle What to do when exception occur during unlocking the lock
     */
    public void unlock(CallbackArg1<LockStatement> callback, CallbackArg1<? super Exception> exHandle);

    /**
     * Check if an object is locked by this lock statement. <br/>
     * This technically is a getter method and the variable behind is local and should be handled locally,
     * meaning when you lock an object and get the response of lock success, set <code>isLocked</code> to true,
     * and when you unlock it and get the response of unlock success or on disconnection, set it to false. <br/>
     * @return If the lock is locked
     */
    public boolean isLocked();

    /**
     * Get the type of the lock, either read lock or write lock. <br/>
     * @return The type of the lock
     */
    public LockType getLockType();

}
