package ink.aquar.util.netio;

import java.nio.ByteBuffer;

/**
 * <code>MessageReceiver</code> is an interface that should be implemented to receive messages from another object. <br/>
 * The sender object will call <code>onMessage(message)</code> method to make the receiver object receive the message. <br/>
 * 
 * @author Kelby Iry
 */
public interface MessageReceiver {
    
    /**
     * What to do when receive a message. <br/>
     * @param message The message
     */
    public void onMessage(ByteBuffer message);

}
