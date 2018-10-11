package ink.aquar.util.misc.placeholder;

import java.util.Objects;

/**
 *
 */
public class BakedProfile {

    /** The profile that fits MySQL standards, and also SQL standards */
    public static final BakedProfile SQL_PROFILE = new BakedProfile(new Profile() {
        {
            variableStart = "<";
            variableEnd = ">";
            loopStart = "|::";
            preLoopEnd = "::";
            postLoopStart = "::";
            loopEnd = "::|";
        }
    });

    /**
     * The default profile, as set to SQL profile because one of the AquarIO's main function is
     * Object-oriented-ify those relation-mapping databases
     */
    public static final BakedProfile DEFAULT_PROFILE = SQL_PROFILE;

    public final String variableStart;
    public final String variableEnd;
    public final String loopStart;
    public final String preLoopEnd;
    public final String postLoopStart;
    public final String loopEnd;

    /**
     * Bake a profile. <br/>
     * @param profile The profile that is going to be baked
     */
    public BakedProfile(Profile profile) {
        variableStart = Objects.requireNonNull(profile.variableStart);
        variableEnd = Objects.requireNonNull(profile.variableEnd);
        loopStart = Objects.requireNonNull(profile.loopStart);
        preLoopEnd = Objects.requireNonNull(profile.preLoopEnd);
        postLoopStart = Objects.requireNonNull(profile.postLoopStart);
        loopEnd = Objects.requireNonNull(profile.loopEnd);
    }
    
}
