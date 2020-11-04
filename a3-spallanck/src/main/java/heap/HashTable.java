//Author: Sophie Pallanck
//Date: 5/21/20
/*Purpose: This program implements a hash table implementation
of a map ADT with chaining to handle collisions */
package heap;

/** A hash table modeled after java.util.Map. It uses chaining for collision
 * resolution and grows its underlying storage by a factor of 2 when the load
 * factor exceeds 0.8. */
public class HashTable<K,V> {

    protected Pair[] buckets; // array of list nodes that store K,V pairs
    protected int size; // how many items currently in the map


    /** class Pair stores a key-value pair and a next pointer for chaining
     * multiple values together in the same bucket, linked-list style*/
    public class Pair {
        protected K key;
        protected V value;
        protected Pair next;

        /** constructor: sets key and value */
        public Pair(K k, V v) {
            key = k;
            value = v;
            next = null;
        }

        /** constructor: sets key, value, and next */
        public Pair(K k, V v, Pair nxt) {
            key = k;
            value = v;
            next = nxt;
        }

        /** returns (k, v) String representation of the pair */
        public String toString() {
            return "(" + key + ", " + value + ")";
        }
    }

    /** constructor: initialize with default capacity 17 */
    public HashTable() {
        this(17);
    }

    /** constructor: initialize the given capacity */
    public HashTable(int capacity) {
        buckets = createBucketArray(capacity);
    }

    /** Return the size of the map (the number of key-value mappings in the
     * table) */
    public int getSize() {
        return size;
    }

    /** Return the current capacity of the table (the size of the buckets
     * array) */
    public int getCapacity() {
        return buckets.length;
    }

    /** Return the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     * Runtime: average case O(1); worst case O(size) */
    public V get(K key) {
        int bucket = hash(key); //find the correct bucket to look in
        Pair curr = buckets[bucket];
        while (curr != null) {
            if (curr.key.equals(key)) { //we've found a mapping, return the value
                return curr.value;
            }
            curr = curr.next;
        }
        return null; //we didn't find this key
    }

    /** Takes a key and hashes it to find the bucket it 
     * corresponds to and returns the bucket index */
    private int hash(K key) {
        return Math.abs(key.hashCode()) % getCapacity();
    }

    /** Associate the specified value with the specified key in this map. If
     * the map previously contained a mapping for the key, the old value is
     * replaced. Return the previous value associated with key, or null if
     * there was no mapping for key. If the load factor exceeds 0.8 after this
     * insertion, grow the array by a factor of two and rehash.
     * Runtime: average case O(1); worst case O(size^2 + a.length)*/
    public V put(K key, V val) {
        int bucket = hash(key);
        Pair curr = buckets[bucket]; //get the list at the correct bucket
        while (curr != null && !curr.key.equals(key)) {
            curr = curr.next;    
        }
        if (curr == null) { 
            size++;
            growIfNeeded();
            bucket = hash(key); //rehash just in case of HashTable growth
            Pair newPair = new Pair(key, val);
            newPair.next = buckets[bucket]; //point newPair to head of list
            buckets[bucket] = newPair; //set head to include newPair
            return null;
        } else { //key was in map, reset the value
            V temp = curr.value;
            curr.value = val;
            return temp;
        }
    }

    /** Return true if this map contains a mapping for the specified key.
     *  Runtime: average case O(1); worst case O(size) */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /** Remove the mapping for the specified key from this map if present.
     *  Return the previous value associated with key, or null if there was no
     *  mapping for key.
     *  Runtime: average case O(1); worst case O(size)*/
    public V remove(K key) {
        if (get(key) == null) { //key wasn't in the hashMap
            return null;
        }
        int bucket = hash(key);
        if (buckets[bucket].key.equals(key)) { //key is first thing in this spot
            V val = buckets[bucket].value; 
            buckets[bucket] = buckets[bucket].next; //remove value
            size--;
            return val;
        }
        Pair prev =  buckets[bucket]; 
        Pair curr = prev.next;
        while (!curr.key.equals(key)) { //the key will be in the list
            curr = curr.next;
            prev = curr;
        }
        V val = curr.value;
        prev.next = curr.next; //reset previous to skip current
        size--;
        return val;
    }

    // suggested helper method:
    /* check the load factor; if it exceeds 0.8, double the array size
     * (capacity) and rehash values from the old array to the new array */
    private void growIfNeeded() {
        double loadFactor = (1.0) * getSize() / getCapacity();
        if (loadFactor > 0.8) {
            Pair[] bigger = createBucketArray(getCapacity() * 2);
            for (int i = 0; i < buckets.length; i++) {
                Pair curr = buckets[i];
                while (curr != null) {
                    Pair next = curr.next; //save a reference to next Pair in old list
                    int hash = (Math.abs(curr.key.hashCode())) % bigger.length; 
                    curr.next = bigger[hash]; //add curr to front
                    bigger[hash] = curr; //set bucket to new front
                    curr = next;
                }
            }
            buckets = bigger;
        }
    }

    /* useful method for debugging - prints a representation of the current
     * state of the hash table by traversing each bucket and printing the
     * key-value pairs in linked-list representation */
    protected void dump() {
        System.out.println("Table size: " + getSize() + " capacity: " +
                getCapacity());
        for (int i = 0; i < buckets.length; i++) {
            System.out.print(i + ": --");
            Pair node = buckets[i];
            while (node != null) {
                System.out.print(">" + node + "--");
                node = node.next;

            }
            System.out.println("|");
        }
    }

    /*  Create and return a bucket array with the specified size, initializing
     *  each element of the bucket array to be an empty LinkedList of Pairs.
     *  The casting and warning suppression is necessary because generics and
     *  arrays don't play well together.*/
    @SuppressWarnings("unchecked")
    protected Pair[] createBucketArray(int size) {
        return (Pair[]) new HashTable<?,?>.Pair[size];
    }
}
