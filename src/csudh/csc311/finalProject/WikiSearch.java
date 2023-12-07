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

	// map from URLs that contain the term(s) to relevance score
	private Map<String, Integer> map;

	/**
	 * Constructor.
	 *
	 * @param map
	 */
	//A WikiSearch object contains a map from URLs to their relevance score.
	public WikiSearch(Map<String, Integer> map) {
		this.map = map;
	}

	/**
	 * Looks up the relevance of a given URL.
	 *
	 * @param url
	 * @return
	 */
	public Integer getRelevance(String url) {
		Integer relevance = map.get(url);
		return relevance==null ? 0: relevance;
	}

	/**
	 * Prints the contents in order of term frequency.
	 *
	 * @param
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
		//creates a hashmap r that will map the relevance of a term on the Wikipedia website
		Map<String, Integer> r = new HashMap<String, Integer>(map);
		for(String term: that.map.keySet()) {
			// relevance score is based on the TF-IDF framework for term relevance
			int relevanceScore = totalRelevance(this.getRelevance(term),that.getRelevance(term));
			//adds the term and its relevance to the hashmap
			r.put(term, relevanceScore);
		}
		//returns the hashmap
		return new WikiSearch(r);
	}

	/**
	 * Computes the intersection of two search results.
	 *
	 * @param that
	 * @return New WikiSearch object.
	 */

	//checks for the intersection of two searches 
	public WikiSearch and(WikiSearch that) {
		//Creates a hashMap intersection 
		Map<String, Integer> intersection = new HashMap<String, Integer>();
		//for each term in the hashmap
		for (String term: map.keySet()) {
			//checks if the map contains the term
			if (that.map.containsKey(term)) {
				//creates integer relevance that takes the total relevance  of the two identical terms
				int relevance = totalRelevance(this.map.get(term), that.map.get(term));
				//adds the term and its relevance to the intersection hasmap
				intersection.put(term, relevance);
			}
		}
		//returns the intersection search results
		return new WikiSearch(intersection);
	}

	/**
	 * Computes the intersection of two search results.
	 *
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch minus(WikiSearch that) {
		//Creates a new hashmap difference
		Map<String, Integer> difference = new HashMap<String, Integer>(map);
		//for each term in the hashmap
		for (String term: that.map.keySet()) {
			//removes the term
			difference.remove(term);
		}
		//returns the hashmap without the term
		return new WikiSearch(difference);
	}

	/**
	 * Computes the relevance of a search with multiple terms.
	 *
	 * @param rel1: relevance score for the first search
	 * @param rel2: relevance score for the second search
	 * @return
	 */

	 
	protected int totalRelevance(Integer rel1, Integer rel2) {
		// simple starting place: relevance is the sum of the term frequencies.
		return rel1 + rel2;
	}

	/**
	 * Sort the results by relevance.
	 *
	 * @return List of entries with URL and relevance.
	 */
	public List<Entry<String, Integer>> sort() {
		List<Entry<String, Integer>> entries = 
				new LinkedList<Entry<String, Integer>>(map.entrySet());
		
		// Comparator object is created to help sort
		Comparator<Entry<String, Integer>> comparator = new Comparator<Entry<String, Integer>>() {
            @Override
			//comparator requires compare method to be implemented
            public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
				//compares the two values 
                return e1.getValue().compareTo(e2.getValue());
            }
        };
        
        // sort the entries and then return them
		Collections.sort(entries, comparator);
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

	public static void main(String[] args) throws IOException {

		// make a JedisIndex
		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis);

		// search for the first term
		String term1 = "java";
		System.out.println("Query: " + term1);
		WikiSearch search1 = search(term1, index);
		search1.print();

		// search for the second term
		String term2 = "programming";
		System.out.println("Query: " + term2);
		WikiSearch search2 = search(term2, index);
		search2.print();

		// compute the intersection of the searches
		System.out.println("Query: " + term1 + " AND " + term2);
		WikiSearch intersection = search1.and(search2);
		intersection.print();
	}
}
