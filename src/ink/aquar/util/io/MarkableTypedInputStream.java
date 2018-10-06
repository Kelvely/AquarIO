package ink.aquar.util.io;

import java.io.IOException;

/**
 * ROAR! Not documented yet. <br/>
 * @author Kelby Iry
 */
public interface MarkableTypedInputStream<T> extends TypedInputStream<T> {
    
    public void mark(int pos);
    
    public void reset() throws IOException;

}
