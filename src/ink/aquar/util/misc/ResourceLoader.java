package ink.aquar.util.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Simply load all UTF-8 bytes into a string, using the best performance algorithm. <br/>
 * See reference from a StackOverflow <a href="https://stackoverflow.com/a/35446009/9685251">answer</a>.
 *
 * @author Kelby Iry
 */
public class ResourceLoader {

    private ResourceLoader() {}

    /**
     * Load all bytes from the input stream. <br/>
     * Best performance using ByteArrayOutputStream! <br/>
     * @param inputStream The input stream ought to be read
     * @return The read string from the stream
     * @throws WrappedIOException Thrown when IO exceptions occur when reading the stream
     */
    public static String loadString(InputStream inputStream) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString();
        } catch (IOException ex) {
            throw new WrappedIOException(ex);
        }
    }

    /**
     * Load all bytes from the input stream. <br/>
     * Best performance using ByteArrayOutputStream! <br/>
     * @param inputStream The input stream ought to be read
     * @return The read bytes from the stream
     * @throws WrappedIOException Thrown when IO exceptions occur when reading the stream
     */
    public static ByteBuffer loadBytes(InputStream inputStream) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return ByteBuffer.wrap(result.toByteArray());
        } catch (IOException ex) {
            throw new WrappedIOException(ex);
        }
    }

}
