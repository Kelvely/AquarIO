package ink.aquar.util.netio;

import java.nio.ByteBuffer;

/**
 * <code>MessagePoster</code> is a message output solution. <br/>
 * When someone(an object, technically) want to send something to another, it uses <code>MessagePoster.sendMessage(message)</code>, 
 * which the another object has a <code>MessagePoster</code> or is a <code>MesasgePoster</code> for the sender object. <br/>
 * 
 * @author Kelby Iry
 */
public interface MessagePoster {
    
    /**
     * Send a message. <br/>
     * @param message The message
     */
    public void sendMessage(ByteBuffer message);

}
