package ink.aquar.util.misc;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.*;

/**
 * A check list is used to wait for multiple tasks to be done
 * so that you can do something else that requires those tasks to be done. <br/>
 * Assume here's 3 tasks to do: <br/>
 * <code>
 *     Checklist checklist = new Checklist(); <br/>
 *     TaskStatus task1 = checklist.createTask(); <br/>
 *     Runnable task1Runnable = () -> { doSomething(); System.out.println("task1 is done"); task1.done(); } <br/>
 *     TaskStatus task2 = checklist.createTask(); <br/>
 *     Runnable task2Runnable = () -> { doSomethingCool(); System.out.println("task2 is done"); task1.done(); } <br/>
 *     TaskStatus task3 = checklist.createTask(); <br/>
 *     Runnable task3Runnable = () -> { doSomethingAwesome(); System.out.println("task3 is done"); task1.done(); } <br/>
 *     new Thread(task1Runnable).start(); <br/>
 *     new Thread(task2Runnable).start(); <br/>
 *     new Thread(task3Runnable).start(); <br/>
 *     checklist.blockUntilAllDone(); // Block until all task done <br/>
 *     System.out.println("All tasks are done"); <br/>
 * </code>
 * And it wait for all 3 tasks to be done then do something else. <br/>
 * <br/>
 * Don't worry, this checklist is of course thread safe ;) <br/>
 *
 * @author Kelby Iry
 */
public class Checklist {

    protected Set<TaskStatus> tasks = new HashSet<>();
    protected Lock tasksLock = new ReentrantLock();
    protected Condition blocker = tasksLock.newCondition();

    /**
     * Add a task to do and get a task status. <br/>
     * You should invoke <code>TaskStatus.done()</code> after your task is done. <br/>
     * @return A task status, as its task is added to the checklist
     */
    public TaskStatus createTask() {
        TaskStatus task = new TaskStatus(this);
        tasksLock.lock();
        tasks.add(task);
        tasksLock.unlock();
        return task;
    }

    /**
     * Check if all tasks are done. <br/>
     * @return If all tasks are done
     */
    public boolean isTaskAllDone() {
        boolean isDone;
        tasksLock.lock();
        isDone = tasks.isEmpty();
        tasksLock.unlock();
        return isDone;
    }

    /**
     * Block until all tasks are done. <br/>
     * @throws InterruptedException if the current thread is interrupted
     * (and interruption of thread suspension is supported)
     */
    public void blockUntilAllDone() throws InterruptedException {
        tasksLock.lock();
        if(!tasks.isEmpty()) {
            blocker.await();
        }
        tasksLock.unlock();
    }

    /**
     * Check the task to done status. <br/>
     * It will not affect anything if you <code>done()</code> a task that is not created by this checklist. <br/>
     * @param task The task status of the task
     */
    public void done(TaskStatus task) {
        tasksLock.lock();
        tasks.remove(task);
        if(tasks.isEmpty()) blocker.signalAll();
        tasksLock.unlock();
    }

    /**
     * Clear task, meaning set all tasks to done state. <br/>
     * This may be useful when handling timeout exceptions. <br/>
     */
    public void clear() {
        tasksLock.lock();
        tasks.clear();
        blocker.signalAll();
        tasksLock.unlock();
    }

    public static class TaskStatus {

        protected final Checklist checklist;

        protected TaskStatus(Checklist checklist) {
            this.checklist = checklist;
        }

        /**
         * Check the task to done status. <br/>
         */
        public void done() {
            checklist.done(this);
        }

    }

}
