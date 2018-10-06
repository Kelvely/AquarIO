package ink.aquar.util.eventhandler;

/**
 * A listener that could receive a type of event <br/>
 * Lambda is recommended, but a little bit harder: <br/>
 * <code>
 *     Listener<SomeEvent> listener = event -> { <br/>
 *         doSomething();<br/>
 *     };<br/>
 *     eventBus.registerListener(listener);<br/>
 * </code>
 * An this can make Java compiler know the type parameter,
 * and the event bus can know what event this listener ought to listen. <br/>
 * @param <E> The event type to listen
 *
 * @author Kelby Iry
 */
public interface Listener<E extends Event> {

    /**
     * What to do when an event is fired. <br/>
     * @param event The actual event
     */
    public void listen(E event);

}
