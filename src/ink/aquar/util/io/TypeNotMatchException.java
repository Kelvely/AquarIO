package ink.aquar.util.io;

/**
 * Thrown when the type doesn't match while checking the type identifier. <br/>
 * Though it's a runtime exception, you should catch them when needed. <br/>
 * @author Kelby Iry
 */
public class TypeNotMatchException extends RuntimeException {

    /**
     * Construct a <code>TypeNotMatchException</code> with no parameter. <br/>
     */
    public TypeNotMatchException() {
        super();
    }

    /**
     * Construct a <code>TypeNotMatchException</code> with a message. <br/>
     * @param message The message
     */
    public TypeNotMatchException(String message) {
        super(message);
    }

}
