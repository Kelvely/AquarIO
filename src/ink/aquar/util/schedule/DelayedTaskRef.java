package ink.aquar.util.schedule;

/**
 * A reference to a task inside a <code>DelayedScheduler</code>, and the task. <br/>
 * This reference can do something else than a reference. <br/>
 * 
 * @author Kelby Iry
 * @see TaskRef
 * @see Scheduler
 * @see DelayedScheduler 
 */
public interface DelayedTaskRef extends TaskRef {
    
    /**
     * Get the system millisecond time the task has been scheduled. <br/>
     * @return The time the task has been scheduled
     */
    public long getTimeScheduledMillis();
    
    /**
     * Get the system millisecond time the task should be executed. <br/>
     * @return The time the task should be executed
     */
    public long getTimeToExecuteMillis();
    
    /**
     * Get the millisecond unit delay time span, which is the time from the task schedule to the time executing the task. <br/>
     * @return The delay time span
     */
    public default long getTimeDelayedMillis() { return getTimeToExecuteMillis() - getTimeScheduledMillis(); }

    /**
     * Get the time remaining in millisecond unit before executing the task. <br/>
     * @return The time remaining before executing the task
     */
    public default long getTimeRemainingMillis() {
        long timeRemains = getTimeToExecuteMillis() - System.currentTimeMillis();
        return timeRemains > 0 ? timeRemains : 0;
    }

    /**
     * Get the time waited in millisecond unit for executing the task. <br/>
     * @return The time waited for executing the task
     */
    public default long getTimeWaitedMillis() {
        long delay = getTimeDelayedMillis();
        long timeWaited = System.currentTimeMillis() - getTimeScheduledMillis();
        return timeWaited < delay ? timeWaited : delay;
    }

}
