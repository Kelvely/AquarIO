package ink.aquar.util.misc;

import ink.aquar.util.schedule.DelayedScheduler;
import ink.aquar.util.schedule.Scheduler;

/**
 * A set of scheduler that is probably going to be commonly used. <br/>
 * @author Kelby Iry
 */
public class SchedulerSet {

    public final DelayedScheduler timingScheduler;
    public final Scheduler internalScheduler;
    public final Scheduler interfaceScheduler;

    /**
     * Create a scheduler set with scheduler specified. <br/>
     * @param timingScheduler Scheduler used for timing
     * @param internalScheduler Internal scheduler that deals with IOs, etc
     * @param interfaceScheduler Interface scheduler that schedules the main thread
     */
    public SchedulerSet(DelayedScheduler timingScheduler, Scheduler internalScheduler, Scheduler interfaceScheduler) {
        this.timingScheduler = timingScheduler;
        this.internalScheduler = internalScheduler;
        this.interfaceScheduler = interfaceScheduler;
    }


}
