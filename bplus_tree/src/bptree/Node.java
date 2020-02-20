package bptree;

import bptree.BPlusTree.InvalidDeletionException;

/**
 * The {@code Node} class implements nodes that constitute a B+-tree.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * @param <K>
 *            the type of keys
 * @param <P>
 *            the type of pointers
 */
public abstract class Node<K extends Comparable<K>, P> implements java.io.Serializable {

	/**
	 * An automatically generated serial version UID.
	 */
	private static final long serialVersionUID = -4653032690694061325L;

	/**
	 * The number of keys that this {@code Node} currently maintains.
	 */
	protected int keyCount;

	/**
	 * The keys that this {@code Node} maintains.
	 */
	protected K[] keys;

	/**
	 * The pointers that this {@code Node} maintains.
	 */
	protected P[] pointers;

	/**
	 * Constructs a {@code Node}.
	 * 
	 * @param degree
	 *            the degree of the {@code Node}
	 */
	@SuppressWarnings("unchecked")
	public Node(int degree) {
		keyCount = 0;
		keys = (K[]) new Comparable[degree - 1];
		pointers = (P[]) new Object[degree];
	}

	/**
	 * Returns a string representation of this {@code Node}.
	 */
	public String toString() {
		String s = "(";
		for (int i = 0; i < keys.length; i++)
			s += ((i == 0 ? "" : ", ") + pointers[i] + ", " + keys[i]);
		return s + ", " + pointers[keys.length] + ")";
	}

	/**
	 * Returns the number of keys in this {@code Node}.
	 * 
	 * @return the number of keys in this {@code Node}
	 */
	public int keyCount() {
		return keyCount;
	}

	/**
	 * Returns the key at the specified index.
	 * 
	 * @param i
	 *            the index of the key
	 * @return the key at the specified index
	 */
	public K key(int i) {
		return keys[i];
	}

	/**
	 * Returns the pointer at the specified index.
	 * 
	 * @param i
	 *            the index of the key
	 * @return the pointer at the specified index
	 */
	public P pointer(int i) {
		return pointers[i];
	}

	/**
	 * Appends the specified keys and their pointers of the specified {@code Node} into this {@code Node}.
	 * 
	 * @param node
	 *            a {@code Node}
	 * @param beginIndex
	 *            the beginning index of the keys, inclusive
	 * @param endIndex
	 *            the ending index of the keys, inclusive
	 */
	public void append(Node<K, P> node, int beginIndex, int endIndex) {
		for (int i = 0; i <= endIndex - beginIndex; i++) {
			this.keys[keyCount] = node.keys[i + beginIndex];
			this.pointers[keyCount] = node.pointers[i + beginIndex];
			keyCount++;
		}
	}

	/**
	 * Clears this {@code Node}.
	 */
	public void clear() {
		keyCount = 0;
		for (int i = 0; i < keys.length; i++)
			keys[i] = null;
		for (int i = 0; i < pointers.length; i++)
			pointers[i] = null;
	}

	/**
	 * Determines whether or not this {@code Node} is full and thus cannot contain more keys.
	 * 
	 * @return {@code true} if this {@code Node} is full and thus cannot contain more keys; {@code false} otherwise
	 */
	public boolean isFull() {
		return keyCount >= keys.length;
	}

	/**
	 * Removes the specified key and a relevant pointer from this {@code Node}.
	 * 
	 * @param key
	 *            a key
	 * @throws InvalidDeletionException
	 *             if a key non-existent in this {@code Node} is attempted to be removed from this {@code Node}.
	 */
	public abstract void remove(K key) throws InvalidDeletionException;

	/**
	 * Determines whether or not this {@code Node} is under-utilized and thus some action such as merging or
	 * redistribution is needed.
	 * 
	 * @return {@code true} if this {@code Node} is under-utilized and thus some action such as merging or
	 *         redistribution is needed; {@code false} otherwise
	 */
	public abstract boolean isUnderUtilized();

	/**
	 * Determines whether or not this {@code Node} can be merged with the specified {@code Node}.
	 * 
	 * @param other
	 *            another {@code Node}
	 * @return {@code true} if this {@code Node} can be merged with the specified {@code Node}; {@code false} otherwise
	 */
	public abstract boolean mergeable(Node<K, P> other);

}
