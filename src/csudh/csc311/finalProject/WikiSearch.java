package src.csudh.csc311.finalProject;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import redis.clients.jedis.Jedis;


/**
 * Represents the results of a search query.
 *
 */
public class WikiSearch {

	private Map<String, Integer> map;

	/**
	 * Constructor.
	 *
	 * @param map
	 */
	public WikiSearch(Map<String, Integer> map) {
		this.map = map;
	}

	/**
	 * Looks up the relevance of a given URL.
	 *
	 * @param url
	 * @return the relevance 
	 */
	public Integer getRelevance(String url) {
		Integer relevance = map.get(url); 
		return relevance==null ? 0: relevance; //ternary ifelse returning the relevance 
	}

	/**
	 * Prints the contents in order of term frequency.
	 *
	 * 
	 */
	private  void print() {
		List<Entry<String, Integer>> entries = sort();
		for (Entry<String, Integer> entry: entries) {
			System.out.println(entry);
		}
	}

	/**
	 * Computes the union of two search results.
	 *
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch or(WikiSearch that) {
		
		Map<String, Integer> r = new HashMap<String, Integer>(map); 
		for(String term: that.map.keySet()) {
			int relevanceScore = totalRelevance(this.getRelevance(term),that.getRelevance(term)); // Computes combined relevance
			r.put(term, relevanceScore); //Adds the term and its relevance to the hashmap
		}
		return new WikiSearch(r); // Returns new WikiSearch object
	}

	/**
	 * Computes the intersection of two search results.
	 *
	 * @param that
	 * @return New WikiSearch object.
	 */

	public WikiSearch and(WikiSearch that) {
		Map<String, Integer> intersection = new HashMap<String, Integer>();
		for (String term: map.keySet()) {
			if (that.map.containsKey(term)) { //Checks if the map contains the term
				int relevance = totalRelevance(this.map.get(term), that.map.get(term)); // Computes combined relevance
				intersection.put(term, relevance); 
			}
		}
		return new WikiSearch(intersection);// Returns new WikiSearch object
	}

	/**
	 * Computes the intersection of two search results.
	 *
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch minus(WikiSearch that) {
		Map<String, Integer> difference = new HashMap<String, Integer>(map);
		for (String term: that.map.keySet()) { //Checks each term in HashMap
			difference.remove(term); //Removes the term
		}
		return new WikiSearch(difference);// Returns WikiSearch object
	}

	/**
	 * Computes the relevance of a search with multiple terms.
	 *
	 * @param rel1: relevance score for the first search
	 * @param rel2: relevance score for the second search
	 * @return
	 */


	protected int totalRelevance(Integer rel1, Integer rel2) {
		return rel1 + rel2; // returns sum of term frequencies
	}

	/**
	 * Sort the results by relevance.
	 *
	 * @return List of entries with URL and relevance.
	 */
	public List<Entry<String, Integer>> sort() {
		List<Entry<String, Integer>> entries = 
				new LinkedList<Entry<String, Integer>>(map.entrySet());
		
		Comparator<Entry<String, Integer>> comparator = new Comparator<Entry<String, Integer>>() { //Sorts using Comparator 
            @Override
            public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
                return e1.getValue().compareTo(e2.getValue()); 
            }
        };
        
		Collections.sort(entries, comparator); //Sorts the entries and then returns them
		return entries;
	}


	/**
	 * Performs a search and makes a WikiSearch object.
	 *
	 * @param term
	 * @param index
	 * @return
	 */
	public static WikiSearch search(String term, JedisIndex index) {

		Map<String, Integer> map = index.getCounts(term);
		return new WikiSearch(map);
	}

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis); // makes a JedisIndex object

		String term1 = "java"; 
		System.out.println("Query: " + term1); // Search for the first term
		WikiSearch search1 = search(term1, index);
		search1.print();

		String term2 = "programming";
		System.out.println("Query: " + term2); // Search for the second term
		WikiSearch search2 = search(term2, index);
		search2.print();

		System.out.println("Query: " + term1 + " AND " + term2);
		WikiSearch intersection = search1.and(search2); // Compute the intersection of the searches
		intersection.print();
	}
}
