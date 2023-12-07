package src.csudh.csc311.finalProject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import org.jsoup.select.Elements;

/**
 * Encapsulates a map from search term to set of TermCounter.
 */
public class Index {

    private Map<String, Set<TermCounter>> index = new HashMap<String, Set<TermCounter>>();
    /**
     * Adds a TermCounter to the set associated with `term`.
     * @param term
     * @param tc
     */
    public void add(String term, TermCounter tc) {
        Set<TermCounter> set = get(term); // If the term is a new one, it will make a new Set
        if (set == null) {
            set = new HashSet<TermCounter>();
            index.put(term, set);
        }
        set.add(tc); // Allows us to modify an existing Set
    }

    /**
     * Looks up a search term and returns a set of TermCounters.
     * @param term
     * @return
     */
    public Set<TermCounter> get(String term) {
        return index.get(term); //Returns the set
    }

	/**
	 * Prints the contents of the index.
	 */
	public void printIndex() {
		for (String term: keySet()) { // Loops through the search terms
			System.out.println(term);
			
			Set<TermCounter> tcs = get(term); // For each term, print the pages where it appears
			for (TermCounter tc: tcs) {
				Integer count = tc.get(term);
				System.out.println("    " + tc.getLabel() + " " + count);
			}
		}
	}

	/**
	 * Returns the set of terms that have been indexed.
	 * 
	 * @return
	 */
	public Set<String> keySet() {
		return index.keySet();
	}

	/**
	 * Add a page to the index.
	 *
	 * @param url         URL of the page.
	 * @param paragraphs  Collection of elements that should be indexed.
	 */
	public void indexPage(String url, Elements paragraphs) {
		// make a TermCounter and count the terms in the paragraphs
		TermCounter tc = new TermCounter(url);
		tc.processElements(paragraphs);
		
		// for each term in the TermCounter, add the TermCounter to the index
		for (String term: tc.keySet()) {
			add(term, tc);
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		WikiFetcher wf = new WikiFetcher();
		Index indexer = new Index();

		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		Elements paragraphs = wf.fetchWikipedia(url);
		indexer.indexPage(url, paragraphs);
		
		url = "https://en.wikipedia.org/wiki/Programming_language";
		paragraphs = wf.fetchWikipedia(url);
		indexer.indexPage(url, paragraphs);
		
		indexer.printIndex();
	}
}
