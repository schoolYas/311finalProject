package src.csudh.csc311.finalProject;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.jsoup.nodes.Node;


/**
 * Performs a depth-first traversal of a jsoup Node.
 */
public class WikiNodeIterable implements Iterable<Node> {

	private Node root;

	/**
	 * Creates an iterable starting with the given Node.
	 *
	 * @param root
	 */
	public WikiNodeIterable(Node root) {
	    this.root = root;
	}

	@Override
	public Iterator<Node> iterator() {
		return new WikiNodeIterator(root);
	}

	/**
	 * Inner class that implements the Iterator.
	 *
	 * @author Yasamean Zaidi-Dozandeh
	 *
	 */
	private class WikiNodeIterator implements Iterator<Node> {

		
		Deque<Node> stack; // the Stack keeps track of the Nodes waiting to be visited

		/**
		 * Initializes the Iterator with the root Node on the stack.
		 *
		 * @param node
		 */
		public WikiNodeIterator(Node node) {
			stack = new ArrayDeque<Node>();
		    stack.push(root);
		}

		@Override
		public boolean hasNext() {
			return !stack.isEmpty(); 
		}

		@Override
		public Node next() {
			if (stack.isEmpty()) { // If the stack is empty, we're done
				throw new NoSuchElementException();
			}

			Node node = stack.pop(); // Otherwise pop the next Node off the stack

			List<Node> nodes = new ArrayList<Node>(node.childNodes()); // Push the children onto the stack in reverse order
			Collections.reverse(nodes);
			for (Node child: nodes) {
				stack.push(child);
			}
			return node;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
