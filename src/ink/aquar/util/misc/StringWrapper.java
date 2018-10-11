package ink.aquar.util.misc;

/**
 * This is a wrapper class for <code>java.lang.String</code>.
 * Used to fill variables that is a string and need to be identified from normal texts,
 * telling that this is a variable but not a part of the text. <br/>
 *
 * @author Kelby Iry
 */
public class StringWrapper {

    private final String string;

    public StringWrapper(String string) {
        this.string = string;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StringWrapper) {
            return ((StringWrapper) obj).string.equals(string);
        } else return false;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(string.hashCode());
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
