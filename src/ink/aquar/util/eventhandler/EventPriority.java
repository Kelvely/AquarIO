package ink.aquar.util.eventhandler;

/**
 * The event priority. <br/>
 * @author Kelby Iry
 */
public enum EventPriority {
    
    LOWEST(0), LOW(1), NORMAL(2), HIGH(3), HIGHEST(4), MONITOR(5);
    
    public final int slot;
    
    private EventPriority(int slot) {
        this.slot = slot;
    }
    
}
