package ink.aquar.util.concurrent.callback;

/**
 * Callback with an argument. <br/>
 * Don't forget to use lambda! <br/>
 * @param <A> The type of first argument
 */
public interface CallbackArg1<A> {
    
    /**
     * What to do when calling back. <br/>
     * @param arg The first argument
     */
    public void onCallback(A arg);

}
