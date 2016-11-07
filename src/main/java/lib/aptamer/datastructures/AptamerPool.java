/**
 * 
 */
package lib.aptamer.pool;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import exceptions.InvalidAlphabetException;

/**
 * @author Jan Hoinka
 *
 * API for handling aptamer pools. An aptamer pool is defined
 * as every sequenced aptamer of an HT-SELEX experiment and its
 * respective rounds. 
 * 
 * This API provides efficient and transparent methods for aptamer 
 * storage, retrieval, and manipulation.
 */
public interface AptamerPool extends Iterable<Entry<byte[], Integer>>{


	/**
	 * Adds (i.e. registers) an aptamer in the pool and assigns a unique identifier to it.
	 * This identifier corresponds to an integer which can be used for indexing purposes for
	 * additional properties of the aptamer (e.g. to which selection cycles it belongs, counts, etc)
	 * 
	 * If the aptamer already exists in the pool, it will not be inserted again, but 
	 * the existing identifier will be returned. 
	 * 
	 * It is assumed that the parent class performing the registration takes care of
	 * the alphabet validation, i.e. only A C G and T are allowed in all capital letters
	 * 
	 * @param a The aptamer sequence to be added to the pool
	 * @return A unique integer for that sequence
	 */
	public int registerAptamer(byte[] a);
	
	/**
	 * @see AptamerPool#registerAptamer(byte[] a)
	 */
	public int registerAptamer(String a);
	
	
	/**
	 * Retrieves the unique identifier on an aptamer in the pool. 
	 * @param a The aptamer sequence 
	 * @return an integer >= 0. If the aptamer does not exist in the pool, -1 is returned.
	 */
	public int getIdentifier(byte[] a);
	
	/**
	 * @see AptamerPool#getIdentifier(byte[] a)
	 */
	public int getIdentifier(String a);

	
	
	/**
	 * Checks for the existence of a specific aptamer <code>a</code> in the pool.
	 * @param a The aptamer sequence 
	 * @return
	 */
	public Boolean containsAptamer(byte[] a);
	
	/**
	 * @see AptamerPool#containsAptamer(byte[] a)
	 */	
	public Boolean containsAptamer(String a);	
	
	
	/**
	 * Returns the total number of unique aptamers in the pool
	 * @return
	 */
	public int size();
	
	
	/**
	 * Removes all items from the current pool and resets the bloom filter.
	 * Note this physically deletes all files in the project data folder.
	 */
	public void clear();
}