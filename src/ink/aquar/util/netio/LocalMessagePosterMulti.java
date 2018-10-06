package ink.aquar.util.netio;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Sends messages to those receivers that are registered in it. <br/>
 * It is used for local message forwarding, which could put in use to connect IOs.<br/>
 * 
 * @author Kelby Iry
 * @see MessagePoster
 */
public class LocalMessagePosterMulti implements MessagePoster {
    
    protected final Map<String, MessageReceiver> channels = new HashMap<>();

    /**
     * Register a receiver with a channel name. <br/>
     * @param channel The channel name
     * @param receiver The receiver to register
     * @return The previous receiver that use this channel name, null if no receivers already registered using this channel name
     */
    public MessageReceiver register(String channel, MessageReceiver receiver) {
        if(receiver == null) return channels.remove(channel);
        else return channels.put(channel, receiver);
    }

    /**
     * Remove a receiver corresponding to its channel name. <br/>
     * @param channel The channel name
     * @return The removed receiver, null if no receiver registered with this channel name
     */
    public MessageReceiver removeReceiver(String channel) {
        return channels.remove(channel);
    }
    
    /**
     * Check if a channel is being used by a receiver. <br/>
     * @param channel The channel name
     * @return If the channel is being used by a receiver
     */
    public boolean isChannelRegistered(String channel) {
        return channels.containsKey(channel);
    }

    /**
     * All receivers registered will receive the message. <br/>
     * <br/>
     * Send a message. <br/>
     * @param message The message
     */
    @Override
    public void sendMessage(ByteBuffer message) {
        for(MessageReceiver receiver : channels.values()) {
            receiver.onMessage(message);
        }
    }

}
