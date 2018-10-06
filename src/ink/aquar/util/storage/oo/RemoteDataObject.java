package ink.aquar.util.storage.oo;

import ink.aquar.util.concurrent.callback.CallbackArg1;

/**
 * A reference to a remote data object. <br/>
 * A data object is used to store data, as it has its type identifier and the serialized data inside.
 * However, it is just a reference, so to get the data inside, use <code>retrieve()</code>. You can also just get
 * the type identifier or get the size of the data instead of retrieve the whole object to local. <br/>
 *
 * @author Kelby Iry
 */
public interface RemoteDataObject extends RemoteObject {

    /**
     * Get the size of the data from remote. <br/>
     * @param callback What to do when the size of the data has been gotten
     * @param exHandle What to do when exception occurs while getting the size of the data
     */
    public void size(CallbackArg1<Integer> callback, CallbackArg1<? super Exception> exHandle);

}
