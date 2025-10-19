// @edu:student-assignment

package uq.comp3506.a2.structures;

/**
 * Supplied by the COMP3506/7505 teaching team, Semester 2, 2025.
 */
public class Entry<K, V> {

    /** The key is used for comparisons (in heaps, maps, etc)*/
    private K key;

    /** The value is the payload data object*/
    private V value;

    /** The basic constructor taking a key and value*/
    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /** Gets the key, who would have thought*/
    public K getKey() {
        return this.key;
    }

    /** Get the value*/
    public V getValue() {
        return this.value;
    }

    /** Set (update) the value*/
    public void setValue(V value) {
        this.value = value;
    }

    // You may add more helper functions here, if you wish
    /**
     * rewrite equals method - compare based on content rather than memory address
     */
    @Override
    public boolean equals(Object obj) {
        // if the same object, return true
        if (this == obj) {
            return true;
        }
        
        // if obj is null or type different, return false
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        // type conversion
        Entry<?, ?> other = (Entry<?, ?>) obj;
        
        // compare key content
        boolean keyEquals;
        if (this.key == null) {
            keyEquals = (other.key == null);
        } else {
            keyEquals = this.key.equals(other.key);
        }
        
        // compare value content
        boolean valueEquals;
        if (this.value == null) {
            valueEquals = (other.value == null);
        } else {
            valueEquals = this.value.equals(other.value);
        }
        
        // only when key and value are equal, return true
        return keyEquals && valueEquals;
    }

    /**
     * rewrite hashCode method - must be consistent with equals method
     * when equals returns true, hashCode must return the same value
     */
    @Override
    public int hashCode() {
        int result = 17;  
        
        // add key's hashCode to result
        result = 31 * result + (key != null ? key.hashCode() : 0);
        
        // add value's hashCode to result
        result = 31 * result + (value != null ? value.hashCode() : 0);
        
        return result;
    }
}
