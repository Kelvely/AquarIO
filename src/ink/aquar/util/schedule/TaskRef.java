package ink.aquar.util.schedule;

/**
 * A reference to a task inside a <code>Scheduler</code>, and the task. <br/>
 * This reference can do something else than a reference. <br/>
 * 
 * @author Kelby Iry
 * @see Scheduler
 */
public interface TaskRef {
    
    /**
     * To cancel the task, remove the task, and get the task. <br/>
     * @return The task being canceled, null if the task is done or removed from the scheduler's schedule.
     */
    public Runnable cancel();

    /**
     * Check if the task is already done. <br/>
     * @return If the task is done
     */
    public boolean isDone();
    
    /**
     * Get the task that this reference refers to. <br/>
     * The task will be returned no matter the task is being done, canceled, or removed from the scheduler. <br/>
     * @return The task, no matter no matter it is canceled or done
     */
    public Runnable getTask();

}
