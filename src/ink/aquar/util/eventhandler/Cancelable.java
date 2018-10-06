package ink.aquar.util.eventhandler;

/**
 * An event that is cancelable can implement this method
 * @author Kelby Iry
 */
public interface Cancelable {

    /**
     * Check if the event has been canceled. <br/>
     * @return If the event has been canceled
     */
    public boolean isCanceled();

    /**
     * Set the cancel status. <br/>
     * Implementation is elastic and you can just make <code>setCanceled(false)</code> not to be success.
     * Any usage is up to you, and you write your own event documentation. <br/>
     * @param cancellation The cancel status to set
     * @return If the cancel status is set successfully
     */
    public boolean setCanceled(boolean cancellation);

}
