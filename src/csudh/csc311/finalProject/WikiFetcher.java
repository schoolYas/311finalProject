package src.csudh.csc311.finalProject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WikiFetcher {
	private long lastRequestTime = -1;
	private long minInterval = 1000;

	/**
	 * Fetches and parses a URL string, returning a list of paragraph elements.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public Elements fetchWikipedia(String url) throws IOException {
		sleepIfNeeded();

		Connection conn = Jsoup.connect(url); // Download and parse the document
		Document doc = conn.get();

		Element content = doc.getElementById("mw-content-text"); // Select the content text and pull out the paragraphs.

		Elements paras = content.select("p");
		return paras;
	}

	/**
	 * Reads the contents of a Wikipedia page from src/resources.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public Elements readWikipedia(String url) throws IOException {
		URL realURL = new URL(url);

		String slash = File.separator; // Assemble the file name
		String filename = "resources" + slash + realURL.getHost() + realURL.getPath();

		InputStream stream = WikiFetcher.class.getClassLoader().getResourceAsStream(filename); // Read the file
		Document doc = Jsoup.parse(stream, "UTF-8", filename);

		Element content = doc.getElementById("mw-content-text"); // Parse the contents of the file
		Elements paras = content.select("p");
		return paras;
	}

	/**
	 * Rate limits by waiting at least the minimum interval between requests.
	 */
	private void sleepIfNeeded() {
		if (lastRequestTime != -1) {
			long currentTime = System.currentTimeMillis();
			long nextRequestTime = lastRequestTime + minInterval;
			if (currentTime < nextRequestTime) {
				try {
					Thread.sleep(nextRequestTime - currentTime);
				} catch (InterruptedException e) {
					System.err.println("Warning: sleep interrupted in fetchWikipedia.");
				}
			}
		}
		lastRequestTime = System.currentTimeMillis();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		WikiFetcher wf = new WikiFetcher();
		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		Elements paragraphs = wf.readWikipedia(url);

		for (Element paragraph: paragraphs) {
			System.out.println(paragraph);
		}
	}
}