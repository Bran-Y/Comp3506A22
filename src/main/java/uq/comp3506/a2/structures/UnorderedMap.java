// @edu:student-assignment

package uq.comp3506.a2.structures;

import java.util.ArrayList;

/**
 * Supplied by the COMP3506/7505 teaching team, Semester 2, 2025.
 * <p>
 * NOTE: You should go and carefully read the documentation provided in the
 * MapInterface.java file - this explains some of the required functionality.
 */
public class UnorderedMap<K, V> implements MapInterface<K, V> {


    /**
     * you will need to put some member variables here to track your
     * data, size, capacity, etc...
     */
    private Entry<K, V>[] data;
    private int size = 0;
    private int capacity = 16;
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f; // 降低负载因子减少冲突

    /**
     * Constructs an empty UnorderedMap
     */
    public UnorderedMap() {
        // Implement me!
        this.data = (Entry<K, V>[]) new Entry[INITIAL_CAPACITY];
        this.capacity = INITIAL_CAPACITY;
        this.size = 0;
    }
    
    /**
     * Constructs an empty UnorderedMap with specified initial capacity
     * @param initialCapacity the initial capacity (will be adjusted to next power of 2)
     */
    public UnorderedMap(int initialCapacity) {
        this.capacity = nextPowerOfTwo(initialCapacity);
        this.data = (Entry<K, V>[]) new Entry[this.capacity];
        this.size = 0;
    }

    /**
     * returns the size of the structure in terms of pairs
     * @return the number of kv pairs stored
     */
    @Override
    public int size() {
        // Implement me!
        return this.size;
    }

    /**
     * helper to indicate if the structure is empty or not
     * @return true if the map contains no key-value pairs, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Clears all elements from the map. That means, after calling clear(),
     * the return of size() should be 0, and the data structure should appear
     * to be "empty".
     */
    @Override
    public void clear() {
        // Implement me!
        this.data = (Entry<K, V>[]) new Entry[INITIAL_CAPACITY];
        this.capacity = INITIAL_CAPACITY;
        this.size = 0;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old value
     * is replaced by the specified value.
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the payload data value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no such key
     */
    @Override
    public V put(K key, V value) {
        // Implement me!
        //1.先检查是否满了当前是否达到了load factor
        double currentLoadFactor = (double) this.size / this.capacity;
        //如果满了，先扩容再rehash
        if (currentLoadFactor >= LOAD_FACTOR) {
            //扩容
            resize();
        }
        //计算index
        int index = bucket_index(key);
        //放置元素
        Entry<K, V> currentSlot = this.data[index];
        V returnValue = null;
        //线性探测
        int originalIndex = index;
        while (currentSlot != null) {
            if (currentSlot.getKey().equals(key)) {
                // 找到了，替换
                returnValue = currentSlot.getValue();
                this.data[index].setValue(value);
                return returnValue;
            }
            //如果当前位置不为空，则继续探测下一个位置
            index = (index + 1) & (this.capacity - 1);
            currentSlot = this.data[index];
            //防止循环
            if (index == originalIndex) {
                break;
            }
        }
        this.data[index] = new Entry<>(key, value);
        this.size++;
        return returnValue;
    }
    
    private int bucket_index(K key) {
        if (key == null) {
            return 0;
        }
        
        // 使用Java内置的hashCode方法，效率更高
        int hash = key.hashCode();
        
        // 使用位运算优化取模操作（当capacity是2的幂时）
        if (isPowerOfTwo(this.capacity)) {
            return hash & (this.capacity - 1);
        } else {
            // 处理负数：使用位运算确保结果为正
            return (hash & Integer.MAX_VALUE) % this.capacity;
        }
    }
    
    private boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }
    
    private void resize() {
        // 保存旧数据
        Entry<K, V>[] oldData = this.data;
        int oldCapacity = this.capacity;
        
        // 确保新容量是2的幂，提高位运算效率
        this.capacity = nextPowerOfTwo(oldCapacity * 2);
        this.data = (Entry<K, V>[]) new Entry[this.capacity];
        
        // 重新插入所有元素
        for (Entry<K, V> element : oldData) {
            if (element != null) {
                // 直接使用位运算计算新位置
                int hash = element.getKey().hashCode();
                int newIndex = hash & (this.capacity - 1);
                
                // 线性探测找到空位
                while (this.data[newIndex] != null) {
                    newIndex = (newIndex + 1) & (this.capacity - 1);
                }
                this.data[newIndex] = element;
            }
        }
    }
    
    private int nextPowerOfTwo(int n) {
        if (n <= 0) return 1;
        if (isPowerOfTwo(n)) return n;
        
        // 找到大于n的最小2的幂
        n--;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        return n + 1;
    }
    /**
     * Looks up the specified key in this map, returning its associated value
     * if such key exists.
     *
     * @param key the key with which the specified value is to be associated
     * @return the value associated with key, or null if there was no such key
     */
    @Override
    public V get(K key) {
        // Implement me!
        int index = bucket_index(key);
        int originalIndex = index;
        //获取当前entry
        Entry<K, V> slot = this.data[index];
        while (slot != null) {
            if (slot.getKey().equals(key)) {
                return slot.getValue();
            }
            index = (index + 1) & (this.capacity - 1);
            slot = this.data[index];
            if (index == originalIndex) {
                break;
            }
        }
        //没找到，返回null
        return null;
    }

    /**
     * Looks up the specified key in this map, and removes the key-value pair
     * if the key exists.
     *
     * @param key the key with which the specified value is to be associated
     * @return the value associated with key, or null if there was no such key
     */
    @Override
    public V remove(K key) {
        // Implement me!
        V currentValue = get(key);
        if (currentValue == null) {
            return null;
        }
        int index = bucket_index(key);
        int originalIndex = index;
        while (this.data[index] != null) {
            if (this.data[index].getKey().equals(key)) {
                this.data[index] = null;
                this.size--;
                rehash(index);
                return currentValue;
            }
            index = (index + 1) & (this.capacity - 1);
            if (index == originalIndex) {
                break;
            }
        }
        return null;
    }
    
    private void rehash(int removedIndex) {
        // 使用更高效的rehash策略：直接移动后续元素
        int index = (removedIndex + 1) & (this.capacity - 1);
        
        while (this.data[index] != null) {
            Entry<K, V> entry = this.data[index];
            int idealIndex = bucket_index(entry.getKey());
            
            // 如果这个元素应该放在当前位置或之前的位置，则移动它
            if (idealIndex <= removedIndex || idealIndex > index) {
                this.data[removedIndex] = entry;
                this.data[index] = null;
                removedIndex = index;
            }
            
            index = (index + 1) & (this.capacity - 1);
        }
    }
}
