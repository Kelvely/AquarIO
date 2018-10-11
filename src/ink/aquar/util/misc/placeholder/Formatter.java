package ink.aquar.util.misc.placeholder;

import java.util.List;

/**
 *
 */
public class Formatter extends Section {

    /**
     * Yes, you can inherit this class and make your own formatter! <br/>
     */
    protected Formatter() { }

    /**
     *
     * @param variableName
     * @param object
     * @param loopOccurrence
     * @return
     */
    public Formatter fill(String variableName, Object object, int... loopOccurrence) {
        setVariable(variableName, loopOccurrence, loopOccurrence, 0);
        return this;
    }

    /**
     *
     * @return
     */
    public Formatter createAsTemplate() {
        Formatter formatter = new Formatter();
        cloneTemplateInto(formatter);
        return formatter;
    }

    /**
     *
     * @param text
     * @param profile
     * @return
     */
    public static Formatter parse(String text, BakedProfile profile) {
        Formatter formatter = new Formatter();
        Section.parse(formatter, text, profile);
        return formatter;
    }

    /**
     *
     * @param text
     * @return
     */
    public static Formatter parse(String text) {
        return parse(text, BakedProfile.DEFAULT_PROFILE);
    }

    /**
     *
     * @return
     */
    @Override
    public List<Object> dump() {
        // Needed to write some docs :P
        return super.dump();
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(Object obj : dump()) {
            stringBuilder.append(obj);
        }
        return stringBuilder.toString();
    }

}
