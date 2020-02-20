package bptree;

import java.io.IOException;

/**
 * A {@code StorageManager} manages a storage space.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * 
 * @param <L>
 *            the type of locations of objects in the {@code StorageManager}
 * @param <O>
 *            the type of objects managed by the {@code StorageManager}
 */
public interface StorageManager<L, O> {

	/**
	 * Returns the ID of the specified file.
	 * 
	 * @param fileName
	 *            the name of the file
	 * @return the ID of the specified file
	 */
	int fileID(String fileName);

	/**
	 * Returns the first location in any file.
	 * 
	 * @return the first location in any file.
	 */
	L first();

	/**
	 * Returns the object at the specified location in the specified file.
	 * 
	 * @param fileID
	 *            the ID of the file
	 * @param loc
	 *            the location of the object
	 * @return the object at the specified location in the specified file
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	O get(int fileID, L loc) throws IOException;

	/**
	 * Puts the specified object at the specified location in the specified file.
	 * 
	 * @param fileID
	 *            the ID of the file
	 * @param loc
	 *            the location of the object
	 * @param o
	 *            the object to put
	 * @return the object stored previously at the specified location in the specified file; {@code null} if no such
	 *         object
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	O put(int fileID, L loc, O o) throws IOException;

	/**
	 * Adds the specified object in the specified file.
	 * 
	 * @param fileID
	 *            the ID of the file
	 * @param o
	 *            the object to add
	 * @return the location of the object in the specified file
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	L add(int fileID, O o) throws IOException;

	/**
	 * Removes the specified object at the specified location in the specified file.
	 * 
	 * @param fileID
	 *            the ID of the file
	 * @param loc
	 *            the location of the object
	 * @return the object stored previously at the specified location in the specified file; {@code null} if no such
	 *         object
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	O remove(int fileID, L loc) throws IOException;

}
