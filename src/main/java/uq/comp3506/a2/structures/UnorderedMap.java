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
    private static final float LOAD_FACTOR = 0.8f;

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
            index = (index + 1) % this.capacity;
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
        
        String keyString = key.toString();
        int hash = 0;
        
        // 使用31作为乘数
        for (int i = 0; i < keyString.length(); i++) {
            hash = 31 * hash + keyString.charAt(i);
        }
        
        // 处理负数：如果是负数就取绝对值
        if (hash < 0) {
            hash = -hash;
        }
        
        int index = hash % this.capacity;
        return index;
    }
    
    private void resize() {
        //双倍扩容
        int oldCapacity = this.capacity;
        this.capacity *= 2;
        Entry<K, V>[] newData = (Entry<K, V>[]) new Entry[this.capacity];
        for (Entry<K, V> element : this.data) {
            if (element != null) {
                int newIndex = bucket_index(element.getKey());
                while (newData[newIndex] != null) {
                    newIndex = (newIndex + 1) % this.capacity;
                }
                newData[newIndex] = element;
            }
        }
        this.data = newData;    
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
            index = (index + 1) % this.capacity;
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
            index = (index + 1) % this.capacity;
            if (index == originalIndex) {
                break;
            }
        }
        return null;
    }
    
    private void rehash(int removedIndex) {
        ArrayList<Entry<K, V>> toReinsert = new ArrayList<>();
        int index = (removedIndex + 1) % this.capacity;
        while (this.data[index] != null) {
            toReinsert.add(this.data[index]);
            this.data[index] = null;
            this.size--;
            index = (index + 1) % this.capacity;
        }
        for (Entry<K, V> entry : toReinsert) {
            put(entry.getKey(), entry.getValue());
        }
    }
}
