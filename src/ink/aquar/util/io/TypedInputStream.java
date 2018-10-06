package ink.aquar.util.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * ROAR! Not documented yet. <br/>
 * @author Kelby Iry
 */
public interface TypedInputStream<T> extends Closeable {
    
    public int available() throws IOException;
    
    public boolean hasNext() throws IOException;
    
    public T next() throws IOException;
    
    public void skip() throws IOException;
    
    public void skip(int times) throws IOException;

}
