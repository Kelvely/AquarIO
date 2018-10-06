package ink.aquar.util.eventhandler;

/**
 * When registering a listener with a raw type will cause the event bus to throw this exception. <br/>
 *
 * @author Kelby Iry
 */
public class RawTypeListenerException extends RuntimeException {

    private static final long serialVersionUID = -9135719704711161249L;

    /**
     * Create with no parameter. <br/>
     */
    public RawTypeListenerException() {
        super();
    }

    /**
     * Create with a message. <br/>
     * @param message The exception message
     */
    public RawTypeListenerException(String message) {
        super(message);
    }

}
