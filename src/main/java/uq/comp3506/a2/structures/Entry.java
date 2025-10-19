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
     * 重写equals方法 - 基于内容比较而不是内存地址
     * 这是通过测试的关键！
     */
    @Override
    public boolean equals(Object obj) {
        // 如果是同一个对象，直接返回true
        if (this == obj) {
            return true;
        }
        
        // 如果obj是null或者类型不同，返回false
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        // 类型转换
        Entry<?, ?> other = (Entry<?, ?>) obj;
        
        // 比较key的内容
        boolean keyEquals;
        if (this.key == null) {
            keyEquals = (other.key == null);
        } else {
            keyEquals = this.key.equals(other.key);
        }
        
        // 比较value的内容
        boolean valueEquals;
        if (this.value == null) {
            valueEquals = (other.value == null);
        } else {
            valueEquals = this.value.equals(other.value);
        }
        
        // 只有key和value都相等时，才返回true
        return keyEquals && valueEquals;
    }

    /**
     * 重写hashCode方法 - 必须与equals方法保持一致
     * 当equals返回true时，hashCode必须返回相同的值
     */
    @Override
    public int hashCode() {
        int result = 17;  // 使用质数作为初始值
        
        // 将key的hashCode加入结果
        result = 31 * result + (key != null ? key.hashCode() : 0);
        
        // 将value的hashCode加入结果
        result = 31 * result + (value != null ? value.hashCode() : 0);
        
        return result;
    }
}
