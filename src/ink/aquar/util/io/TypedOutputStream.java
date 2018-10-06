package ink.aquar.util.io;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

/**
 * ROAR! Not documented yet. <br/>
 * @author Kelby Iry
 */
public interface TypedOutputStream<T> extends Closeable, Flushable {
    
    public void write(T value) throws IOException;

}
