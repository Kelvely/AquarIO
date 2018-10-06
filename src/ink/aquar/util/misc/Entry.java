package ink.aquar.util.misc;

/**
 * Entry is entry, the entry of a table, or a map, or... whatever xDD <br/>
 * @author Kelby Iry
 *
 * @param <K> The type of the key
 * @param <V> The type of the value
 */
public class Entry<K, V> {
    
    public final K key;
    public final V value;

    /**
     * Create an entry with a key and a value.
     * @param key The key of the entry
     * @param value The value of the entry
     */
    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

}
