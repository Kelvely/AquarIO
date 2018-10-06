package ink.aquar.util.storage.oo.conncluster;

import ink.aquar.util.concurrent.callback.CallbackArg0;
import ink.aquar.util.concurrent.callback.CallbackArg1;

/**
 * An interface to manage connection cluster. <br/>
 *
 * @author Kelby Iry
 */
public interface ConnectionManager {

    /**
     * Add specified number of sub-connections to connection cluster. <br/>
     * @param amount The number of sub-connections that is desired to be added to the connection cluster
     * @param callback What to do when the sub-connections are added
     * @param exHandle What to do when exception occur during adding sub-connections
     * @throws IllegalArgumentException Thrown when amount is less than 0
     */
    public void createSubConnections(int amount, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle);

    /**
     * Close specified number of sub-connections from the connection cluster. <br/>
     * @param amount The number of sub-connections that is desired to be closed from the connection cluster
     * @param callback What to do when sub-connections are closed
     * @param exHandle What to do when exception occur during adding sub-connections
     * @throws IllegalArgumentException Thrown when amount is less than 0
     * @throws IllegalStateException Thrown when amount is greater than or equal to the amount of established sub-connections
     */
    public void closeSubConnections(int amount, CallbackArg0 callback, CallbackArg1<? super Exception> exHandle);

    /**
     * Get the number of established sub-connections. <br/>
     * @return The number of established sub-connections
     */
    public int numOfSubConnections();

}
