package ink.aquar.util.eventhandler;

/**
 * The base class of Event. <br/>
 * Whatever can you do to this class ;) <br/>
 * <br/>
 * Example:
 * <code>
 *     public class CowEatGrassEvent extends Event { <br/>
 * <br/>
 *         public Cow getCow(); <br/>
 * <br/>
 *         public Location getLocation(); <br/>
 * <br/>
 *         // And whatever you want to add <br/>
 * <br/>
 *     } <br/>
 * </code>
 *
 * @author Kelby Iry
 */
public abstract class Event {

    /** The event name will be the class name of the event class. */
    public final String name = this.getClass().getName();

}
