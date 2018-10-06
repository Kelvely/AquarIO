package ink.aquar.util.io;

/**
 * something went wrong while de-serializing bytes, data is damaged for example. <br/>
 * Though it's a runtime exception, you should catch them when needed. <br/>
 * @author Kelby Iry
 */
public class DeserializationException extends RuntimeException {

    /**
     * Construct a <code>DeserializationException</code> with no parameter. <br/>
     */
    public DeserializationException() {
        super();
    }

    /**
     * Construct a <code>DeserializationException</code> with a message. <br/>
     * @param message The message
     */
    public DeserializationException(String message) {
        super(message);
    }

}
