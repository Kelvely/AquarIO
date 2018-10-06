package ink.aquar.util.misc;

import java.io.IOException;

/**
 * A wrapped <code>IOException</code> that is a <code>RuntimeException</code>,
 * make non-throws exception handling possible <br/>
 */
public class WrappedIOException extends RuntimeException {

    /**
     * The wrapped IO Exception <br/>
     */
    public final IOException ioException;

    /**
     * Create a wrapped IO exception with an IO exception. <br/>
     * @param ioException The IO exception
     */
    public WrappedIOException(IOException ioException) {
        this.ioException = ioException;
    }

}
