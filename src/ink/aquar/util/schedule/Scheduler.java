package ink.aquar.util.schedule;

/**
 * As its name, it is used to schedule tasks. <br/>
 * In many circumstances, it is used to avoid stack to overflow when using callback mechanisms. <br/>
 * <br/>
 * Implementations of this interface should consider concurrency issues if needed. <br/>
 * <br/>
 * Implementations assume the tasks will not be removed as it is being scheduled, 
 * because in real life we actually don't consider a task is being removed from our calendar though it is canceled or done,
 * and sometimes developers want to re-schedule a task through a reference after the task is done.
 * However, it is recommended to technically remove the canceled task and done task from the scheduler.
 * Use TaskRef and garbage collector wisely ;) <br/>
 * Every method of <code>Scheduler</code> should support calls from any thread, which meaning it is thread safe. <br/>
 * 
 * @author Kelby Iry
 * @see TaskRef
 */
public interface Scheduler {
    
    /**
     * Schedule a task, which should be executed ASAP. <br/>
     * @param task The task to schedule
     * @return The reference of the task
     * @see TaskRef
     */
    public TaskRef schedule(Runnable task);
    
    /**
     * <i>Burning daylight method, but make things legit.</i> <br/>
     * <br/>
     * Cancel a task, and return this task, according to its reference. <br/>
     * @param taskRef The reference of the task
     * @return The canceled task, null if the task is canceled or done.
     */
    public Runnable cancelTask(TaskRef taskRef);
    
    /**
     * <i>Burning daylight method, but make things legit.</i> <br/>
     * <br/>
     * Check if a task is done. <br/>
     * @param taskRef The reference of the task
     * @return If the task is done
     */
    public boolean isTaskDone(TaskRef taskRef);
    
    /**
     * Check if a task is schedule TO BE EXECUTED. <br/>
     * Thus, canceled and done task will return false. <br/>
     * @param taskRef The reference of the task
     * @return If the task according to the reference is to be executed
     */
    public boolean hasTask(TaskRef taskRef);
    
    /**
     * <i>Burning daylight method, but make things legit.</i> <br/>
     * <br/>
     * Get the task according to its reference. <br/>
     * The task will be returned no matter the task is being done, canceled, or removed from the scheduler. <br/>
     * @param taskRef The reference of the task
     * @return The task, no matter it is canceled or done
     */
    public Runnable getTask(TaskRef taskRef);

}
