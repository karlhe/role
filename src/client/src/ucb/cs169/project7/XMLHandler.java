package ucb.cs169.project7;

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler{
	protected Hashtable hash;
	
	private Stack<String> nameStack;
	private StringBuilder contentString;
	

	
	public XMLHandler(Hashtable hash) {
		this.hash = hash;
		nameStack = new Stack<String>();
		contentString = new StringBuilder();
	}

	public Hashtable getHash() {
		return hash;
	}
	
	@Override
	public void startDocument() throws SAXException {
	}
	
	@Override
	public void endDocument() throws SAXException {		
	}
	
	@Override
	public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException {
		String currentName = localName;
		nameStack.push(currentName);
		contentString.setLength(0);
		if (currentName.equals("character")) {
			Player newPlayer = new Player();
			newPlayer.setId(new Integer(attributes.getValue("id")));
			//players.add(newPlayer);
		}
	}
	
    @Override 
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		String currentName = nameStack.pop();
		String str = contentString.toString();
    	if (currentName.equals("health")) {
    		//worldState.setHealth(new Integer(str));
    	} else if (currentName.equals("experience")) {
    		//worldState.setExperience(new Integer(str));
    	} else if (currentName.equals("level")) {
    		//worldState.setLevel(new Integer(str));
    	} else if (currentName.equals("name")) {
    		//players.get(players.size() - 1).setName(str);
    	}
    }
    
    @Override 
    public void characters(char ch[], int start, int length) {
		contentString.append(ch, start, length);
    }
}
