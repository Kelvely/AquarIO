package ink.aquar.util.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TextResourceLoader {

    private TextResourceLoader() {}

    /**
     * Load all bytes from the input stream. <br/>
     * Best performance using ByteArrayOutputStream! <br/>
     * @param inputStream The input stream ought to be read
     * @return The read string from the stream
     * @throws WrappedIOException Thrown when IO exceptions occur when reading the stream
     */
    public static String load(InputStream inputStream) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            // StandardCharsets.UTF_8.name() > JDK 7
            return result.toString("UTF-8");
        } catch (IOException ex) {
            throw new WrappedIOException(ex);
        }
    }

}
