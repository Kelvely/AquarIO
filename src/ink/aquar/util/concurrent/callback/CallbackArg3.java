package ink.aquar.util.concurrent.callback;

/**
 * Callback with 3 arguments. <br/>
 * Don't forget to use lambda! <br/>
 * @param <A1> The type of first argument
 * @param <A2> The type of second argument
 * @param <A3> The type of third argument
 */
public interface CallbackArg3<A1, A2, A3> {
    
    /**
     * What to do when calling back. <br/>
     * @param arg1 The first argument
     * @param arg2 The second argument
     * @param arg3 The third argument
     */
    public void onCallback(A1 arg1, A2 arg2, A3 arg3);

}
