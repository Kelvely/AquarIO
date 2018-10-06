package ink.aquar.util.concurrent.callback;

/**
 * Callback with 4 arguments. <br/>
 * Don't forget to use lambda! <br/>
 * @param <A1> The type of first argument
 * @param <A2> The type of second argument
 * @param <A3> The type of third argument
 * @param <A4> The type of fourth argument
 * @param <A5> The type of fifth argument
 */
public interface CallbackArg5<A1, A2, A3, A4, A5> {
    
    /**
     * What to do when calling back. <br/>
     * @param arg1 The first argument
     * @param arg2 The second argument
     * @param arg3 The third argument
     * @param arg4 The fourth argument
     * @param arg5 The fifth argument
     */
    public void onCallback(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);

}
