package ucb.cs169.project7;

import java.util.Hashtable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class HashSet {
	protected Hashtable<String, Object> ht;
	public HashSet() {
		ht = new Hashtable<String, Object>();
	}
	public boolean getFrom(Node node, String ... tags) {
		for (int i=0; i<tags.length; i++) {
			getFrom(node, tags[i]);
		}
		return true;
	}
	public boolean getFrom(Node node, String tag) {
		Node myNode = ((Element)node).getElementsByTagName(tag).item(0);
		if (myNode == null) {
			return false;
		} else {
			set(tag, myNode.getFirstChild().getNodeValue());
			return true;
		}
	}
	public boolean set(String tag, HashSet hashset) {
		ht.put(tag, hashset.getHash());
		return true;
	}
	public boolean set(String tag, Object value) {
		ht.put(tag, value);
		return true;
	}
	public boolean set(String tag, Object [] values) {
		ht.put(tag, values);
		return true;
	}
	public Hashtable<String, Object> getHash() {
		return ht;
	}
}