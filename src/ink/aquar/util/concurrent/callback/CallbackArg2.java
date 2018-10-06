package ink.aquar.util.concurrent.callback;

/**
 * Callback with 2 arguments. <br/>
 * Don't forget to use lambda! <br/>
 * @param <A1> The type of first argument
 * @param <A2> The type of second argument
 */
public interface CallbackArg2<A1, A2> {
    
    /**
     * What to do when calling back. <br/>
     * @param arg1 The first argument
     * @param arg2 The second argument
     */
    public void onCallback(A1 arg1, A2 arg2);

}
