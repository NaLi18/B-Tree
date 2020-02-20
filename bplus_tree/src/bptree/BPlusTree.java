package bptree;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code BPlusTree} class implements B+-trees. Each {@code BPlusTree} stores its {@code Node}s using a
 * {@code StorageManager}.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * 
 * @param <K>
 *            the type of keys
 * @param <P>
 *            the type of pointers
 */
public class BPlusTree<K extends Comparable<K>, P> {

	/**
	 * An {@code InvalidInsertionException} is thrown when a key already existent in a {@code BPlusTree} is attempted to
	 * be inserted again in the {@code BPlusTree}.
	 * 
	 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
	 *
	 */
	public static class InvalidInsertionException extends Exception {

		public InvalidInsertionException(String msg) {
			super(msg);
		}

		/**
		 * An automatically generated serial version UID.
		 */
		private static final long serialVersionUID = -2281189104087198670L;

	}

	/**
	 * An {@code InvalidDeletionException} is thrown when a key non-existent in a {@code BPlusTree} is attempted to be
	 * deleted from the {@code BPlusTree}.
	 * 
	 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
	 *
	 */
	public static class InvalidDeletionException extends Exception {

		public InvalidDeletionException(String msg) {
			super(msg);
		}

		/**
		 * An automatically generated serial version UID.
		 */
		private static final long serialVersionUID = -2281189104087198670L;

	}

	/**
	 * A {@code NodePointerPair} is a pair containing a {@code Node} and a pointer to that {@code Node}.
	 * 
	 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
	 *
	 */
	public class NodePointerPair {

		/**
		 * A {@code Node}.
		 */
		Node<K, P> node;

		/**
		 * A pointer to a {@code Node}.
		 */
		P pointer;

		/**
		 * Constructs a {@code NodePointerPair}.
		 * 
		 * @param node
		 *            a {@code Node}
		 * @param pointer
		 *            a pointer to that {@code Node}
		 */
		public NodePointerPair(Node<K, P> node, P pointer) {
			this.node = node;
			this.pointer = pointer;
		}

		/**
		 * Returns the pointer of this {@code NodePointerPair}.
		 * 
		 * @return the pointer of this {@code NodePointerPair}
		 */
		public P pointer() {
			return pointer;
		}

		/**
		 * Returns the {@code Node} of this {@code NodePointerPair}.
		 * 
		 * @return the {@code Node} of this {@code NodePointerPair}
		 */
		public Node<K, P> node() {
			return node;
		}

	}

	/**
	 * The maximum number of pointers that each {@code Node} of this {@code BPlusTree} can have.
	 */
	protected int degree;

	/**
	 * The {@code StorageManager} used for this {@code BPlusTree}.
	 */
	protected StorageManager<P, Object> sm;

	/**
	 * The ID of the file used for this {@code BPlusTree}.
	 */
	protected int fileID;

	/**
	 * Constructs a {@code BPlusTree}.
	 * 
	 * @param degree
	 *            the maximum number of pointers that each {@code Node} of this {@code BPlusTree} can have
	 * @param sm
	 *            {@code StorageManager} used for this {@code BPlusTree}
	 * @param fileName
	 *            the name of the file used for this {@code BPlusTree}
	 */
	public BPlusTree(int degree, StorageManager<P, Object> sm, String fileName) {
		this.degree = degree;
		this.sm = sm;
		this.fileID = sm.fileID(fileName);
	}

	/**
	 * Returns the degree of this {@code BPlusTree}.
	 * 
	 * @return the degree of this {@code BPlusTree}
	 */
	public int degree() {
		return degree;
	}

	/**
	 * Returns a {@code NodePointerPair} referencing the root {@code Node}.
	 * 
	 * @return a {@code NodePointerPair} referencing the root {@code Node}; {@code null} if this {@code BPlusTree} is
	 *         empty
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@SuppressWarnings("unchecked")
	public NodePointerPair root() throws IOException {
		Object root = sm.get(fileID, sm.first());
		if (root == null)
			return null;
		return nodePointerPair((P) root);
	}

	/**
	 * Returns the specified child {@code Node} of the specified {@code NonLeafNode}.
	 * 
	 * @param node
	 *            a {@code NonLeafNode}
	 * @param i
	 *            the index of the child {@code Node}
	 * @return the specified child {@code Node} of the specified {@code NonLeafNode}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@SuppressWarnings("unchecked")
	public Node<K, P> child(NonLeafNode<K, P> node, int i) throws IOException {
		P p = node.pointer(i);
		if (p == null)
			return null;
		else
			return (Node<K, P>) sm.get(fileID, p);
	}

	/**
	 * Inserts the specified key and pointer into this {@code BPlusTree}.
	 * 
	 * @param k
	 *            the key to insert
	 * @param p
	 *            the pointer to insert
	 * @throws InvalidInsertionException
	 *             if a key already existent in this {@code BPlusTree} is attempted to be inserted again in the
	 *             {@code BPlusTree}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void insert(K k, P p) throws InvalidInsertionException, IOException {
		NodePointerPair root = root();
		if (root == null) {// if the tree is empty
			LeafNode<K, P> l = new LeafNode<K, P>(degree); // create an empty root node
			l.insert(k, p); // insert the specified key and pointer into leaf node l
			saveAsRoot(l); // save node l as the new root

		} else { // if the tree is not empty
			HashMap<NodePointerPair, NodePointerPair> node2parent = new HashMap<NodePointerPair, NodePointerPair>();
			// to remember the parent of each visited node
			NodePointerPair l = find(k, root, node2parent); // find leaf node l that should contain the specified key
			LeafNode<K, P> l_node = (LeafNode<K, P>) l.node();
			if (l_node.contains(k)) // no duplicate keys are allowed in the tree
				throw new InvalidInsertionException("key: " + k);
			if (!l_node.isFull()) { // if leaf node l has room for the specified key
				l_node.insert(k, p); // insert the specified key and pointer into leaf node l
				save(l); // save node l on storage
			} else { // if leaf node l is full and thus needs to be split
				LeafNode<K, P> t = new LeafNode<K, P>(degree + 1); // create a temporary leaf node t
				t.append(l_node, 0, degree - 2); // copy everything to temporary node t
				t.insert(k, p); // insert the key and pointer into temporary node t
				LeafNode<K, P> lp = new LeafNode<K, P>(degree); // create a new leaf node lp
				lp.setSuccessor(l_node.successor()); // chaining from lp to the next leaf node
				l_node.clear(); // clear leaf node l
				int m = (int) Math.ceil(degree / 2.0); // compute the split point
				l_node.append(t, 0, m - 1); // copy the first half to leaf node l
				lp.append(t, m, degree - 1); // copy the second half to leaf node lp
				NodePointerPair _lp = save(lp); // save node lp on storage and also get a pointer to node lp
				l_node.setSuccessor(_lp.pointer()); // chaining from leaf node l to leaf node lp
				save(l); // save node l on storage
				insertInParent(l, lp.key(0), _lp, root, node2parent); // use lp's first key as the separating key
			}
		}
	}

	/**
	 * Finds the {@code LeafNode} that is a descendant of the specified {@code Node} and must be responsible for the
	 * specified key.
	 * 
	 * @param k
	 *            a search key
	 * @param n
	 *            a {@code NodePointerPair} referencing a {@code Node}
	 * @param node2parent
	 *            a {@code Map} to remember, for each visited {@code Node}, the parent of that {@code Node}
	 * @return a {@code NodePointerPair} referencing the {@code LeafNode} which is a descendant of the specified
	 *         {@code Node} and must be responsible for the specified key
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected NodePointerPair find(K k, NodePointerPair n, Map<NodePointerPair, NodePointerPair> node2parent)
			throws IOException {
		if (n.node() instanceof LeafNode)
			return n;
		else {
			BPlusTree<K, P>.NodePointerPair c = nodePointerPair(((NonLeafNode<K, P>) n.node()).child(k));
			node2parent.put(c, n);
			return find(k, c, node2parent);
		}
	}

	/**
	 * Inserts the specified key into the parent {@code Node} of the specified {@code Nodes}.
	 * 
	 * @param n
	 *            a {@code NodePointerPair} referencing a {@code Node}
	 * @param k
	 *            the key between the {@code Node}s
	 * @param np
	 *            a {@code NodePointerPair} referencing a {@code Node}
	 * @param root
	 *            a {@code NodePointerPair} referencing the root {@code Node}
	 * @param node2parent
	 *            a {@code Map} remembering, for each visited {@code Node}, the parent of that {@code Node}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void insertInParent(NodePointerPair n, K k, NodePointerPair np, NodePointerPair root,
			Map<NodePointerPair, NodePointerPair> node2parent) throws IOException {
		if (n.equals(root)) { // if n is the root of the tree
			NonLeafNode<K, P> r = new NonLeafNode<K, P>(degree, n.pointer(), k, np.pointer());
			saveAsRoot(r); // a new root node r containing n, k, np and save it on storage
			return;
		}
		NodePointerPair p = node2parent.get(n); // find the parent p of n
		NonLeafNode<K, P> p_node = (NonLeafNode<K, P>) p.node();
		if (!p_node.isFull()) { // if parent node p has room for a new entry
			p_node.insertAfter(k, np.pointer(), n.pointer()); // insert k and np right after n
			save(p); // save node p on storage
		} else { // if p is full and thus needs to be split
			NonLeafNode<K, P> t = new NonLeafNode<K, P>(degree + 1); // crate a temporary node
			t.copy(p_node, 0, p_node.keyCount()); // copy everything of p to the temporary node
			t.insertAfter(k, np.pointer(), n.pointer()); // insert k and np after n
			p_node.clear(); // clear p
			NonLeafNode<K, P> pp = new NonLeafNode<K, P>(degree); // create a new node pp
			int m = (int) Math.ceil(degree / 2.0); // compute the split point
			p_node.copy(t, 0, m - 1); // copy the first half to parent node p
			pp.copy(t, m, degree); // copy the second half to new node pp
			NodePointerPair _pp = save(pp); // save node pp
			save(p); // save node p on storage
			insertInParent(p, t.key(m - 1), _pp, root, node2parent); // use the middle key as the separating key
		}
	}

	/**
	 * Saves the specified {@code Node} as the new root {@code Node} on storage.
	 * 
	 * @param n
	 *            a {@code Node}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void saveAsRoot(Node<K, P> n) throws IOException {
		P p = sm.add(fileID, n);
		sm.put(fileID, sm.first(), p);
	}

	/**
	 * Saves the specified {@code Node} on storage.
	 * 
	 * @param n
	 *            a {@code NodePointerPair} referencing a {@code Node}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void save(NodePointerPair n) throws IOException {
		sm.put(fileID, n.pointer(), n.node());
	}

	/**
	 * Saves the specified {@code Node} on storage.
	 * 
	 * @param n
	 *            a {@code Node}
	 * @return a {@code NodePointerPair} referencing the {@code Node} after saving it on storage
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected NodePointerPair save(Node<K, P> n) throws IOException {
		return new NodePointerPair(n, sm.add(fileID, n));
	}

	/**
	 * Removes the specified {@code Node} on storage.
	 * 
	 * @param n
	 *            a {@code NodePointerPair} referencing the {@code Node} to remove
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void remove(NodePointerPair n) throws IOException {
		sm.remove(fileID, n.pointer());
	}

	/**
	 * Returns a {@code NodePointerPair} referencing the specified {@code Node}.
	 * 
	 * @param p
	 *            a pointer to the {@code Node} to reference
	 * @return a {@code NodePointerPair} referencing the specified {@code Node}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected NodePointerPair nodePointerPair(P p) throws IOException {
		@SuppressWarnings("unchecked")
		Node<K, P> n = (Node<K, P>) sm.get(fileID, p);
		if (n == null)
			return null;
		else
			return new NodePointerPair(n, p);
	}

	/**
	 * Removes the specified key and the corresponding pointer from this {@code BPlusTree}.
	 * 
	 * @param k
	 *            the key to delete
	 * @throws InvalidDeletionException
	 *             if a key non-existent in a {@code BPlusTree} is attempted to be deleted from the {@code BPlusTree}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void delete(K k) throws InvalidDeletionException, IOException {
		// please implement the body of this method
	}

}