package ink.aquar.util.storage.oo;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The path to a field in a remote object. <br/>
 * If the field name is empty then it is equivalent to <code>this</code> in Java.
 * Thus, a.field is equivalent to a..field, and will be actually removed from the field name queue to avoid returning
 * the false depth, which <code>.bob..pet.name.</code> will be parsed into <code>bob.pet.name</code> as well. <br/>
 *
 * @author Kelby Iry
 */
public class FieldPath implements Iterable<String> {

    private static final String[] EMPTY_STRING_ARRAY = { };

    /** READ ONLY */
    protected final String[] fieldNames;

    /**
     * Create a path to a field with a path expression. <br/>
     * @param path the path expression, like <code>gary.ability.1.name</code>
     */
    public FieldPath(String path) {
        this(path.split("."));
    }

    /**
     * Create a path to field with an array of field names. <br/>
     * The next element of the array is the field of the previous one,
     * as in <code>String[] fieldNames = {"gary", "ability", "1", "name"}</code>,
     * <code>ability</code> is a field of <code>gary</code>. <br/>
     * @param fieldNames An array of field names in member's-member order.
     */
    public FieldPath(String... fieldNames) {
        ArrayList<String> listOfFieldNames = new ArrayList<>(fieldNames.length);
        for (String fieldName : fieldNames) {
            if (!fieldName.isEmpty()) listOfFieldNames.add(fieldName);
        }
        this.fieldNames = listOfFieldNames.toArray(EMPTY_STRING_ARRAY);
    }

    /**
     * Get the depth of this path, as <code>cat.name</code> has depth of 2. <br/>
     * Depth of 0 means <code>this</code> in Java. Just notice if you are doing some implementations like
     * <code>get(new FieldPath({ }))</code>, it should return the object itself. <br/>
     * @return The depth of this path
     */
    public int depth() {
        return fieldNames.length;
    }

    /**
     * The next element of the array is the field of the previous one.
     * @return An iterator to access each field of this in member's-member order
     */
    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private int pointer = 0;

            @Override
            public boolean hasNext() {
                return pointer < fieldNames.length;
            }

            @Override
            public String next() {
                return fieldNames[pointer++];
            }
        };
    }
}
