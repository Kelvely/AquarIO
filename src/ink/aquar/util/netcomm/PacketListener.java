package ink.aquar.util.netcomm;

/**
 * Simply a listener that can receive packets. <br/>
 *
 * @author Kelby Iry
 */
public interface PacketListener {

    /**
     * What to do when here's an incoming packet for this packet listener. <br/>
     * Don't forget use lambda ;) !!
     * @param packet The incoming packet
     * @return The response, null if not to respond.
     */
    public Packet onPacket(Packet packet);

}
