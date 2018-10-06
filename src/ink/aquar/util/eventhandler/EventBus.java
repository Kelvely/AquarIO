package ink.aquar.util.eventhandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * A bus to fire event and pass the event to all listeners subscribed. <br/>
 * Listening to a super class cannot make the listener listen its subclasses. <br/>
 * @author Kelby Iry
 */
public class EventBus {
    
    protected final Map<Class<? extends Event>, List<Listener<? extends Event>>[]> listeners = new HashMap<>();

    /**
     * Register an event listener with the default priority, normal. <br/>
     *  Multiple same listeners register is allowed, as the listener will receive the same event multiple times. <br/>
     * @param listener The listener to register
     * @param <E> The type of the event
     * @throws RawTypeListenerException If you passed a Listener&lt?&gt
     */
    public <E extends Event> void registerListener(Listener<E> listener) {
        registerListener(listener, EventPriority.NORMAL);
    }

    /**
     * Register an event listener with a specific priority. <br/>
     * Multiple same listeners register is allowed, as the listener will receive the same event multiple times. <br/>
     * @param listener The listener to register
     * @param priority The priority specification
     * @param <E> The type of the event
     * @throws RawTypeListenerException If you passed a Listener&lt?&gt
     */
    public <E extends Event> void registerListener(Listener<E> listener, EventPriority priority) {
        Class<E> eventClass = getListenerEventClass(listener);
        if (eventClass == null) throw new RawTypeListenerException();

        List<Listener<? extends Event>>[] priorityQueue = listeners.get(eventClass);
        
        if (priorityQueue == null) {
            priorityQueue = createPriorityQueue();
            listeners.put(eventClass, priorityQueue);
        }

        priorityQueue[priority.slot].add(listener);

    }

    /**
     * Unregister an event listener. <br/>
     * If the listener is registered multiple times, all of the listener will be removed. <br/>
     * @param listener The listener to unregister
     * @param <E> The type of the event
     * @throws RawTypeListenerException If you passed a Listener&lt?&gt
     */
    public <E extends Event> void unregisterListener(Listener<E> listener) {
        Class<E> eventClass = getListenerEventClass(listener);
        if (eventClass == null) throw new RawTypeListenerException();

        List<Listener<? extends Event>>[] priorityQueue = listeners.get(eventClass);
        
        if (priorityQueue != null) {
            
            for (List<Listener<? extends Event>> listenerQueue : priorityQueue) {

                listenerQueue.remove(listener);
                
            }
            
        }
    }

    /**
     * Fire an event, and all listener of this event in this event bus will receive the event. <br/>
     * Listeners will receive the event by its priority,
     * same priority listener considered receiving event with a random order. <br/>
     * @param event The event to fire
     * @param <E> The type of the event
     * @exception EventException If here's exception occurred in the listener
     */
    public <E extends Event> void fireEvent(E event) {

        List<Listener<? extends Event>>[] priorityQueue = listeners.get(event);
        
        if(priorityQueue != null) {
            for(int i=5; i>=0; i--) {
                List<Listener<? extends Event>> listenerQueue = priorityQueue[i];
                for (Listener<? extends Event> rawListener : listenerQueue) {
                    @SuppressWarnings("unchecked")
                    Listener<E> listener = (Listener<E>) rawListener;
                    try {
                        listener.listen(event);
                    } catch (Throwable ex) {
                        if(ex instanceof EventException){
                            EventException targetEx = (EventException) ex;
                            String targetMessage = targetEx.throwable.getMessage();
                            throw new EventException(targetEx.throwable, targetEx.throwable.getClass().getName() + 
                                    " occured on passing " + event.name + " to " + listener.getClass().getName() + 
                                    ":" + (targetMessage != null ? targetMessage : ""));
                        } else {
                            String targetMessage = ex.getMessage();
                            throw new EventException(ex, ex.getClass().getName() + " occured on passing " + 
                            event.name + " to " + listener.getClass().getName() + ":" + 
                            (targetMessage != null ? targetMessage : ""));
                        }
                        
                    }
                }
            }
        }
        
    }

    @SuppressWarnings("unchecked")
    private static List<Listener<? extends Event>>[] createPriorityQueue() {
        List<?>[] queue = new ArrayList<?>[6];
        
        queue[EventPriority.LOWEST.slot] = new ArrayList<Listener<? extends Event>>();
        queue[EventPriority.LOW.slot] = new ArrayList<Listener<? extends Event>>();
        queue[EventPriority.NORMAL.slot] = new ArrayList<Listener<? extends Event>>();
        queue[EventPriority.HIGH.slot] = new ArrayList<Listener<? extends Event>>();
        queue[EventPriority.HIGHEST.slot] = new ArrayList<Listener<? extends Event>>();
        queue[EventPriority.MONITOR.slot] = new ArrayList<Listener<? extends Event>>();

        return (List<Listener<? extends Event>>[]) queue;
    }
    
    @SuppressWarnings("unchecked")
    private static <E extends Event> Class<E> getListenerEventClass(Listener<E> listener) {
        Set<Type> types = getAllGenericInterfaces(listener.getClass());
        for (Type type : types) {
            if(type instanceof ParameterizedType) {
                if(((ParameterizedType) type).getRawType() == Listener.class) {
                    return (Class<E>) ((ParameterizedType) type).getActualTypeArguments()[0];
                }
            }
        }
        return null;
    }
    
    private static Set<Type> getAllGenericInterfaces(Class<?> clazz) {
        Set<Type> genericTypes = new HashSet<>();
        
        Class<?> superclass = clazz.getSuperclass();
        if(superclass != null) genericTypes.addAll(getAllGenericInterfaces(superclass));
        
        Class<?>[] interfaces = clazz.getInterfaces();
        if(interfaces.length > 0) {
            for (Class<?> iClass : interfaces) {
                genericTypes.addAll(getAllGenericInterfaces(iClass));
            }
        }
        
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        genericTypes.addAll(Arrays.asList(genericInterfaces));
        
        return genericTypes;
    }

}
