package ink.aquar.util.eventhandler;

/**
 * When listener has thrown an exception, an <code>EventException</code> will be thrown. <br/>
 * This exception contains the exception that would be thrown by the listener. <br/>
 * @author Kelby Iry
 */
public class EventException extends RuntimeException {

    private static final long serialVersionUID = -3735146421591758086L;

    /**
     * The throwable. <br/>
     */
    public final Throwable throwable;

    /**
     * Create an EventException with the throwable thrown by the listener. <br/>
     * @param throwable The throwable thrown by the listener
     */
    public EventException(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Create an EventException with the throwable thrown by the listener and a message. <br/>
     * @param throwable The throwable thrown by the listener
     * @param message The exception message
     */
    public EventException(Throwable throwable, String message) {
        super(message);
        this.throwable = throwable;
    }

}
