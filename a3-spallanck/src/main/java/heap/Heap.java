package heap;
/*
 * Author: Sophie Pallanck
 * Date: 5/21/20
 * Purpose: This program implements a min-heap that has been augmented 
 * with a map to make certain operations more efficent */
import java.util.NoSuchElementException;

/** An instance is a min-heap of distinct values of type V with
 *  priorities of type P. Since it's a min-heap, the value
 *  with the smallest priority is at the root of the heap. */
public final class Heap<V, P extends Comparable<P>> {
    /**
     * The contents of c represent a complete binary tree. We use square-bracket
     * shorthand to denote indexing into the AList (which is actually
     * accomplished using its get method. In the complete tree,
     * c[0] is the root; c[2i+1] is the left child of c[i] and c[2i+2] is the
     * right child of i.  If c[i] is not the root, then c[(i-1)/2] (using
     * integer division) is the parent of c[i].
     *
     * Class Invariants:
     *
     *   The tree is complete:
     *     1. `c[0..c.size()-1]` are non-null
     *
     *   The tree satisfies the heap property:
     *     2. if `c[i]` has a parent, then `c[i]`'s parent's priority
     *        is smaller than `c[i]`'s priority
     *
     *   In Phase 3, the following class invariant also must be maintained:
     *     3. The tree cannot contain duplicate *values*; note that dupliate
     *        *priorities* are still allowed.
     *     4. map contains one entry for each element of the heap, so
     *        map.size() == c.size()
     *     5. For each value v in the heap, its map entry contains in the
     *        the index of v in c. Thus: map.get(b[i]) = i.
     */
    protected AList<Entry> c;
    protected HashTable<V, Integer> map;

    /** Constructor: an empty heap with capacity 10. */
    public Heap() {
        c = new AList<Entry>(10);
        map = new HashTable<V, Integer>();
    }

    /** An Entry contains a value and a priority. */
    class Entry {
        public V value;
        public P priority;

        /** An Entry with value v and priority p*/
        Entry(V v, P p) {
            value = v;
            priority = p;
        }

        public String toString() {
            return value.toString();
        }
    }

    /** Add v with priority p to the heap.
     *  The expected time is logarithmic and the worst-case time is linear
     *  in the size of the heap. Precondition: p is not null.
     *  In Phase 3 only:
     *  @throws IllegalArgumentException if v is already in the heap.*/
    public void add(V v, P p) throws IllegalArgumentException {
        if (map.containsKey(v)) { 
            throw new IllegalArgumentException();
        }
        Entry add = new Entry(v, p);
        c.append(add);
        map.put(v, size() - 1);
        if (size() > 1) {
            bubbleUp(size() - 1);
        }
    }

    /** Return the number of values in this heap.
     *  This operation takes constant time. */
    public int size() {
        return c.size();
    }

    /** Swap c[h] and c[k].
     *  precondition: h and k are >= 0 and < c.size() */
    protected void swap(int h, int k) {
        Entry tmp1 = c.get(h);
        V val1 = tmp1.value;
        Entry tmp2 = c.get(k);
        V val2 = tmp2.value;
        map.put(val2, h);
        map.put(val1, k);
        c.put(h, c.get(k));
        c.put(k, tmp1);
    }

    /** Bubble c[k] up in heap to its right place.
     *  Precondition: Priority of every c[i] >= its parent's priority
     *                except perhaps for c[k] */
    protected void bubbleUp(int k) {
        while (k > 0 && (c.get(k).priority.compareTo(c.get(parent(k)).priority) < 0)) {
            swap(k, parent(k));
            k = parent(k);
        }
    }

    /** Takes the index of an Entry, and returns the
     * index of the corresponding left child Entry */
    private int leftChild(int index) {
        return index * 2 + 1;
    }

    /** Takes the index of an Entry and returns
     * the index of the corresponding right child Entry */
    private int rightChild(int index) {
        return index * 2 + 2;
    }

    /** Takes the index of an Entry and returns the
     * index of the corresponding parent Entry */
    private int parent(int index) {
        return (index - 1) / 2;
    }

    /** Takes the index of an Entry and returns if the
     * given index has a corresponding left child or not */
    private boolean hasLeftChild(int index) {
        return leftChild(index) < size();
    }

    /** Takes the index of an Entry and returns if the
     * given index has a corresponding right child or not */
    private boolean hasRightChild(int index) {
        return rightChild(index) < size();
    }

    /** Return the value of this heap with lowest priority. Do not
     *  change the heap. This operation takes constant time.
     *  @throws NoSuchElementException if the heap is empty. */
    public V peek() throws NoSuchElementException {
        if (c.size() == 0) {
            throw new NoSuchElementException();
        }
        return c.get(0).value;
    }

    /** Remove and return the element of this heap with lowest priority.
     *  The expected time is logarithmic and the worst-case time is linear
     *  in the size of the heap.
     *  @throws NoSuchElementException if the heap is empty. */
    public V poll() throws NoSuchElementException {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        V val = peek();
        map.remove(val);
        Entry temp = c.pop();
        V tempVal = temp.value;
        if (size() != 0) { //no need to do anything if heap is now empty
            c.put(0, temp);
            map.put(tempVal, 0);
            bubbleDown(0);
        } 
        return val;
    }

    /** Bubble c[k] down in heap until it finds the right place.
     *  If there is a choice to bubble down to both the left and
     *  right children (because their priorities are equal), choose
     *  the right child.
     *  Precondition: Each c[i]'s priority <= its childrens' priorities
     *                except perhaps for c[k] */
    protected void bubbleDown(int k) {
        while (hasLeftChild(k)) { //if k has any child, it will be left
            int smaller = leftChild(k);
            if (hasRightChild(k) && c.get(leftChild(k)).priority.compareTo(c.get(rightChild(k)).priority) > 0) {
                smaller = rightChild(k); //right child has smaller priority than left
            }
            if (c.get(k).priority.compareTo(c.get(smaller).priority) > 0) { //make sure we don't swap duplicates
                swap(k, smaller);
            } else { //we're in the right spot, end the loop
              break;
            }
            k = smaller; //update our indicies to reflect swapping
        }
    }

    /** Return true if the value v is in the heap, false otherwise.
     *  The average case runtime is O(1).  */
    public boolean contains(V v) {
        return map.containsKey(v);
    }

    /** Change the priority of value v to p.
     *  The expected time is logarithmic and the worst-case time is linear
     *  in the size of the heap.
     *  @throws IllegalArgumentException if v is not in the heap. */
    public void changePriority(V v, P p) throws IllegalArgumentException {
        if (!contains(v)) {
            throw new IllegalArgumentException();
        }
        int index = map.get(v); //get the index of the Entry object efficiently
        c.get(index).priority = p;
        bubbleUp(index);
        bubbleDown(index);
    }

    // Recommended helper method spec:
    /* Return the index of the child of k with smaller priority.
     * if only one child exists, return that child's index
     * Precondition: at least one child exists.*/
    private int smallerChild(int k) {
      throw new UnsupportedOperationException();
    }
}
