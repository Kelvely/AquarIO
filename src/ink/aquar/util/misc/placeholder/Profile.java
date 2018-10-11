package ink.aquar.util.misc.placeholder;

public class Profile {

    public String variableStart;
    public String variableEnd;
    public String loopStart;
    public String preLoopEnd;
    public String postLoopStart;
    public String loopEnd;

    public BakedProfile bake() {
        return new BakedProfile(this);
    }

}
