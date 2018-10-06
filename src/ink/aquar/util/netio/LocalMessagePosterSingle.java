package ink.aquar.util.netio;

import java.nio.ByteBuffer;
/**
 * Sends messages to the receiver that has been put in it. <br/>
 * It is used for local message forwarding, which could put in use to connect IOs. <br/>
 * 
 * @author Kelby Iry
 * @see MessagePoster
 */
public class LocalMessagePosterSingle implements MessagePoster {
    
    protected MessageReceiver receiver;

    /**
     * Put a receiver in, take the previous receiver out. <br/>
     * @param receiver The receiver to put in
     * @return The previous receiver, null if doesn't have an already-put receiver
     */
    public MessageReceiver setReceiver(MessageReceiver receiver) {
        MessageReceiver previous = this.receiver;
        this.receiver = receiver;
        return previous;
    }

    /**
     * Check if here is a receiver in this <code>MessagePoster</code>. <br/>
     * @return If it has a receiver
     */
    public boolean hasReceiver() {
        return receiver == null;
    }

    /**
     * The message will be sent to the receiver. <br/>
     * <br/>
     * Send a message. <br/>
     * @param message The message
     */
    @Override
    public void sendMessage(ByteBuffer message) {
        if(receiver != null) receiver.onMessage(message);
    }

}
