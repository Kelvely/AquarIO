package ink.aquar.util.schedule;

/**
 * It is a version of <code>Scheduler</code> that can schedule tasks that should be executed after a while. <br/>
 * 
 * @see Scheduler
 * @author Kelby Iry
 */
public interface DelayedScheduler extends Scheduler {
    
    /**
     * Schedule a task that should be executed after a while. <br/>
     * @param task The task to schedule
     * @param delayMillis How long to wait before executing the task
     * @return The reference of the task
     * @see DelayedTaskRef
     */
    public TaskRef schedule(Runnable task, long delayMillis);

}
