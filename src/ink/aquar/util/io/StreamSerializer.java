package ink.aquar.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The stream based version of <code>Serializer</code>. <br/>
 * This is prepared for large amount of data transfer after serialization. 
 * Using standard serializer to serialize large amount of data will cause a large, even exceeded use of memory. <br/>
 * <br/>
 * Examples of usage: <br/>
 * <li>Direct transfer of data: <code>StreamSerializer&lt;File&gt;</code>, 
 * doesn't make sense but required in AquarIO OO-Bucket uploading and downloading files directly. <br/> </li>
 * <li>Stream format converter: <code>StreamSerializer&lt;World&gt;</code>, which can serialize game worlds into bytes. <br/> </li>
 * <br/>
 * The object type often not directly contains the data directly, 
 * but refers to a reliable and external resource location, <code>File</code> for example. <br/>
 * 
 * @author Kelby Iry
 *
 * @param <T> The object type this serializer can serialize and de-serialize
 */
public interface StreamSerializer<T, I extends TypedInputStream<T>, O extends TypedOutputStream<T>> {
    
    /**
     * Create a stream of serialization. <br/>
     * @param data The source of data that you want to serialize
     * @return The stream where you retrieve your serialized data
     */
    public InputStream createSerializeStream(I data);
    
    /**
     * Create a stream of de-serialization. <br/>
     * @param raw The stream of bytes that you want to de-serialize
     * @return The stream where you retrieve your de-serialized data
     */
    public I createDeserializeStream(InputStream raw);
    
    /**
     * Serialize all data from a <code>TypedInputStream</code> and output to an <code>OutputStream</code>.
     * @param output The <code>OutputStream</code> to output serialized bytes
     * @param input The <code>TypedInputStream</code> as the source of data
     * @throws IOException
     */
    public default void serialize(OutputStream output, I input) throws IOException {
        InputStream intermediate = createSerializeStream(input);
        while(intermediate.available() > 0) {
            byte[] bytesToWrite = new byte[intermediate.available()];
            intermediate.read(bytesToWrite);
            output.write(bytesToWrite);
        }
    }
    
    /**
     * De-serialize all bytes from an <code>InputStream</code> and output to a <code>TypedOutputStream</code>.
     * @param output The <code>TypedOutputStream</code> to output de-serialized data
     * @param input The stream of bytes
     * @throws IOException
     */
    public default void deserialize(O output, InputStream input) throws IOException {
        I intermediate = createDeserializeStream(input);
        while(intermediate.hasNext()) {
            output.write(intermediate.next());
        }
    }

}
