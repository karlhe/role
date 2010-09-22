package ucb.cs169.project7;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.util.Log;
import android.util.Xml;

public class ServerLink {
	public static final int LOC_SEND = 0;
	public static final int LOC_GET = 1;
	public static final int BATTLE_SEND = 2;
	public static final int BATTLE_GET = 3;
	public static final int CHAR_CREATE = 4;
	public static final int CHAR_LOGIN = 5;
	public static final int CHAR_INSPECT = 6;
	public static final int CHAR_CLASS = 7;
	public static final int CHAR_CLASSES = 8;
	
	private ClientComponent callBack;
	private DefaultHttpClient serverClient;
	
	//Maintain the base address list for the various types of requests
	
	//LOCAL TESTING
	/*
	private static final String HOST = "10.0.2.2";
	private static final int PORT = 3000; 
	private static final String[] ADDRESS = {
		"http://10.0.2.2:3000/androids/update/", //0 Address for Location Post
		"http://10.0.2.2:3000/androids/fetch/", //1 Address for PList Get
		"http://10.0.2.2:3000/battles/update/", //2 Address for Battle Post
		"http://10.0.2.2:3000/battles/fetch/", //3 Address for Battle Get
		"http://10.0.2.2:3000/characters/create/", //4 Address for Character Create
		"http://10.0.2.2:3000/character_sessions/create/", //5 Address for Character Login
		"http://10.0.2.2:3000/characters/", //6 Address for Character Inspect
		"http://10.0.2.2:3000/character_classes/", //7 Address for Character Class
		"http://10.0.2.2:3000/character_classes/", //8 Address for Character Classes
		};
		*/
	//EC2 SERVER
	
	private static final String HOST = "ec2-184-73-2-94.compute-1.amazonaws.com";
	private static final int PORT = 80;	
	private static final String[] ADDRESS = {
		"http://ec2-184-73-2-94.compute-1.amazonaws.com:80/androids/update/", //0 Address for Location Post
		"http://ec2-184-73-2-94.compute-1.amazonaws.com/androids/fetch/", //1 Address for PList Get
		"http://ec2-184-73-2-94.compute-1.amazonaws.com:80/battles/update/", //2 Address for Battle Post
		"http://ec2-184-73-2-94.compute-1.amazonaws.com/battles/fetch/", //3 Address for Battle Get
		"http://ec2-184-73-2-94.compute-1.amazonaws.com:80/characters/create/", //4 Address for Character Create
		"http://ec2-184-73-2-94.compute-1.amazonaws.com:80/character_sessions/create/", //5 Address for Character Login
		"http://ec2-184-73-2-94.compute-1.amazonaws.com/characters/", //6 Address for Character Inspect
		"http://ec2-184-73-2-94.compute-1.amazonaws.com/character_classes/", //7 Address for Character Class
		"http://ec2-184-73-2-94.compute-1.amazonaws.com/character_classes/", //8 Address for Character Classes
		};
	
	
	private static final String TAG = "ServerLink";
	
	//
	public ServerLink(ClientComponent callBack) {
		this.callBack = callBack;
		this.serverClient = new DefaultHttpClient();
		Log.v(TAG,"Generated new ServerLink");
	}
	
	public void setCallBack(ClientComponent callBack) {
		this.callBack = callBack;
		Log.v(TAG,"Changed callback");
	}
	
	//A Get Request
	public void sendRequest (int requestType) {
	    RequestAsyncTask task = new RequestAsyncTask(requestType, callBack);
	    Log.v(TAG,"Generated new Async Task");
	    Hashtable<String,Object> data = new Hashtable<String,Object>();
	    data.put("playerId", ((RoleClientApplication) callBack.getApplication()).getPlayerId());
	    Log.v(TAG,"Executing task");
	    task.execute(data);
/*
		HttpResponse response = get(requestType, ((RoleClientApplication) callBack.getApplication()).getPlayerId()); //TODO: Get response and unparse
		Log.v(TAG,"Recieved GET response");
		Hashtable<String, Object> received = parseXML(response, requestType);
		Log.v(TAG,"Parsed GET response");
		threadReturn(received, requestType, myCallback);
		Log.v(TAG,"GET Request completed");
*/
	}
	
	public void sendRequest (int requestType, int playerid) {
        Hashtable<String,Object> data = new Hashtable<String,Object>();
        data.put("playerId", new Integer(playerid));
	    RequestAsyncTask task = new RequestAsyncTask(requestType, callBack);
	    task.execute(data);
/*    
		HttpResponse response = get(requestType, playerid); //TODO: Get response and unparse
		Log.v(TAG,"Recieved GET response");
		Hashtable<String, Object> received = parseXML(response, requestType);
		Log.v(TAG,"Parsed GET response");
		threadReturn(received, requestType);
		Log.v(TAG,"GET Request completed");
*/
	}
	
	//A Post Request
	public void sendRequest (int requestType, Hashtable<String, Object>  data) {
		data.put("id", ((RoleClientApplication) callBack.getApplication()).getPlayerId());
	    RequestAsyncTask task = new RequestAsyncTask(requestType, callBack);
	    task.execute(data);
/*    
		ByteArrayOutputStream out = createXML(data, requestType);
		Log.v(TAG,"POST XML created");
		HttpResponse response = send(out, requestType); //TODO: Get response and unparse
		Log.v(TAG,"Recieved POST response");
		Hashtable<String, Object>  received = parseXML(response, requestType);
		Log.v(TAG,"Parsed POST response");
		threadReturn(received, requestType);
		Log.v(TAG,"POST Request completed");
*/
	}
	
	
	//A Get Request
	public void sendServiceRequest (int requestType) {
		HttpResponse response = get(requestType, ((RoleClientApplication) callBack.getApplication()).getPlayerId()); //TODO: Get response and unparse
		Log.v(TAG,"Recieved GET response");
		Hashtable<String, Object> received = parseXML(response, requestType);
		Log.v(TAG,"Parsed GET response");
		threadReturn(received, requestType);
		Log.v(TAG,"GET Request completed");
	}
	
	public void sendServiceRequest (int requestType, int playerid) {    
		HttpResponse response = get(requestType, playerid); //TODO: Get response and unparse
		Log.v(TAG,"Recieved GET response");
		Hashtable<String, Object> received = parseXML(response, requestType);
		Log.v(TAG,"Parsed GET response");
		threadReturn(received, requestType);
		Log.v(TAG,"GET Request completed");
	}
	
	//A Post Request
	public void sendServiceRequest (int requestType, Hashtable<String, Object>  data) {
		data.put("id", ((RoleClientApplication) callBack.getApplication()).getPlayerId());
		Log.v(TAG,"Setting player ID to " + data.get("id"));
		ByteArrayOutputStream out = createXML(data, requestType);
		Log.v(TAG,"POST XML created");
		HttpResponse response = send(out, requestType); //TODO: Get response and unparse
		Log.v(TAG,"Recieved POST response");
		Hashtable<String, Object>  received = parseXML(response, requestType);
		Log.v(TAG,"Parsed POST response");
		threadReturn(received, requestType);
		Log.v(TAG,"POST Request completed");
	}
	
	
	public void threadReturn (final Hashtable<String, Object> received, final int requestType) {
		
		try {
		  ((Activity) callBack).runOnUiThread(
		          new Runnable() {
		              public void run() {
		            	  if (callBack != null) {
		            		  callBack.receiveData(received, requestType);
		            	  }
		              }
		          }
				  );
		  }
		  catch (ClassCastException e) {
			  Log.v(TAG,"Service call");
        	  if (callBack != null) {
        		  Log.v(TAG,"Called recieve data on service");
        		  callBack.receiveData(received, requestType);
        	  }
		  } catch (Exception e) {
		    StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter(sw);
		    e.printStackTrace(pw);
		    Log.e(getClass().getSimpleName(), sw.getBuffer().toString(), e);
		  }
	}
	
	//Function for creating the XML
    public static ByteArrayOutputStream createXML(Hashtable<String, Object>  data, int requestType) {
    	XmlSerializer xs = Xml.newSerializer();
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	//Build the XML
		switch(requestType) {
		case LOC_SEND:
	    	try {
	    		Log.v(TAG,"LOC SEND XML Creation starting");
				xs.setOutput(out, "UTF-8");
				xs.startDocument(null, null);
				xs.startTag(null, "update");
				xs.startTag(null,"id");
				//xs.text(((Integer) data.get("userid")).toString());
				xs.text(Integer.toString((Integer) data.get("id")));
				//xs.text("1");
				xs.endTag(null, "id");
				//xs.startTag(null,"client");
				//xs.text(((Integer) data.get("client")).toString());
				//xs.endTag(null, "client");
				//xs.startTag(null,"mode");
				//xs.text(((Boolean) data.get("mode")).toString());
				//xs.endTag(null, "mode");
				xs.startTag(null, "location");
				xs.startTag(null, "latitude");
				xs.text(((Double) data.get("latitude")).toString());
				xs.endTag(null, "latitude");
				xs.startTag(null, "longitude");
				xs.text(((Double) data.get("longitude")).toString());
				xs.endTag(null, "longitude");
				xs.endTag(null, "location");
				xs.endTag(null,"update");
				xs.endDocument();
				xs.flush();
				Log.v(TAG, "Finished creating XML");
				return out;
	    	}
			catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case BATTLE_SEND:
	    	try {
	    		Log.v(TAG,"BATTLE SEND XML Creation starting");
				xs.setOutput(out, "UTF-8");
				xs.startDocument(null, null);
				xs.startTag(null, "battle");
				xs.startTag(null,"id");
				//xs.text(((Integer) data.get("player")).toString());
				//xs.text("2");
				xs.text(Integer.toString((Integer) data.get("id")));
				xs.endTag(null, "id");
				xs.startTag(null,"opponent");
				xs.text(((Integer) data.get("opponent")).toString());
				//xs.text("2");
				xs.endTag(null, "opponent");
				xs.startTag(null, "action");
				xs.startTag(null, "type");	
				xs.text((String) data.get("type"));
				xs.endTag(null, "type");
				if (data.containsKey("ability")) {
					xs.startTag(null, "ability");
					xs.text((String) data.get("ability"));
					xs.endTag(null, "ability");
				}
				xs.endTag(null, "action");
				xs.endTag(null,"battle");
				xs.endDocument();
				xs.flush();
				Log.v(TAG, "Finished creating XML");
				return out;
	    	}
			catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CHAR_CREATE:
	    	try {
	    		Log.v(TAG,"CHAR CREATE XML Creation starting");
				xs.setOutput(out, "UTF-8");
				xs.startDocument(null, null);
				xs.startTag(null, "character");
				xs.startTag(null,"name");
				xs.text((String) data.get("name"));
				xs.endTag(null, "name");
				xs.startTag(null,"class");
				xs.text((String) data.get("class"));
				xs.endTag(null, "class");
				xs.startTag(null, "password");
				xs.text((String) data.get("password"));
				xs.endTag(null, "password");
				xs.endTag(null,"character");
				xs.endDocument();
				xs.flush();
				Log.v(TAG, "Finished creating XML");
				return out;
	    	}
			catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CHAR_LOGIN:
	    	try {
	    		Log.v(TAG,"CHAR LOGIN XML Creation starting");
				xs.setOutput(out, "UTF-8");
				xs.startDocument(null, null);
				xs.startTag(null, "character");
				xs.startTag(null,"name");
				xs.text((String) data.get("name"));
				xs.endTag(null, "name");
				xs.startTag(null, "password");
				xs.text((String) data.get("password"));
				xs.endTag(null, "password");
				xs.endTag(null,"character");
				xs.endDocument();
				xs.flush();
				Log.v(TAG, "Finished creating XML");
				return out;
	    	}
			catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			Log.v(TAG,"Not supposed to be here");
			break;
		}
    	

		return null;
	}	
	
    //Method for getting an XML from the server
    synchronized protected HttpResponse get(int requestType, int playerid) {
    	String url_address = ADDRESS[requestType];
    	if (requestType != CHAR_CLASSES && requestType != CHAR_INSPECT) {
    		int userid = ((RoleClientApplication) callBack.getApplication()).getPlayerId();
    		url_address = url_address + new Integer(userid).toString();
    		if (userid == 0) {
    			Log.v("hi","Not supposed to be here");
    			//Log.v(callBack.getClass().getSimpleName(),"Not supposed to be here");
    			//throw new NullPointerException(null);
    			return null;
    		}
    	}
    	if (requestType == CHAR_INSPECT){
    		url_address = url_address + new Integer(playerid).toString();
    	}    	
    	try {
		    HttpClient httpclient = serverClient;  
		    HttpResponse response = httpclient.execute(new HttpGet(url_address));  
		    return response;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
					e.printStackTrace();
		}
		return null;
    }
    
    //Method for sending an XML to the server and receiving one back
    synchronized protected HttpResponse send(ByteArrayOutputStream xml, int requestType) {
    	try {
    		HttpHost host = new HttpHost(HOST, PORT, "http");
			HttpPost post = new HttpPost(ADDRESS[requestType]);
			post.addHeader("Accept", "text/xml");
			post.addHeader("Content-Type", "application/xml");			        
			DefaultHttpClient client = serverClient;
			StringEntity sendEntity = new StringEntity(xml.toString(), "UTF-8");
			sendEntity.setContentType("application/xml");
			post.setEntity(sendEntity);
			Log.v(TAG,"Created header file");
			HttpResponse response = client.execute(host, post);
			Log.v(TAG,"Recieved response");
			return response;
			//TODO: Read the response file, and return it to be parsed
			
			/* Old Sax Parser code
			SAXParserFactory spf = SAXParserFactory.newInstance();
			UpdateHandler handler = new UpdateHandler(myState);
			Log.v(TAG,"Factory stuff");
			SAXParser parser = spf.newSAXParser();
			Log.v(TAG,"Parser");
			XMLReader reader = parser.getXMLReader();
			Log.v(TAG,"Reader");
			reader.setContentHandler(handler);
			Log.v(TAG,"Handle");
			reader.parse(retrieveInputStream(entity));
			Log.v(TAG,"Done");
			//parser.parse(entity.getContent(), handler);
			 
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();*/
		}		catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
					e.printStackTrace();
		}
		return null;
	}
    
	private static Object getText(Element doc, String tag) {
		Node myNode = doc.getElementsByTagName(tag).item(0);
		if (myNode == null) {
			return null;
		}
		else {
			return myNode.getFirstChild().getNodeValue();
		}
	}
	
	private static Object findText(Element doc, String tag) {
		return doc.getElementsByTagName(tag).item(0);
	}
	
	private static boolean setText(Hashtable<String, Object> ht, Node node, String tag) {
		Node myNode = ((Element)node).getElementsByTagName(tag).item(0);
		if (myNode == null) {
			return false;
		} else {
			ht.put(tag, myNode.getFirstChild().getNodeValue());
			return true;
		}
	}
	
	private static HashSet hashFactory() {
		return new HashSet();
	}
    
    public static Hashtable<String, Object>  parseXML(HttpResponse response, int requestType) {
    	if  (response == null) return null;
    	Log.v(TAG,"Starting parse");
    	try {
    		HttpEntity entity = response.getEntity();
    		Log.v(TAG,"Retrieved response");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Log.v(TAG,"Created doc builder factory");
			DocumentBuilder db = dbf.newDocumentBuilder();
			Log.v(TAG,"Created doc builder");
			Document doc = db.parse(retrieveInputStream(entity));
			Log.v(TAG,"Parsed input stream");
			Hashtable<String, Object> data = new Hashtable<String, Object>();
			//doc.getDocumentElement().normalize();
			
	    	//Build the Hashtable from XML
			switch(requestType) {
			case LOC_GET:
				Log.v(TAG,"Location Get Parsing");
				HashSet lg_updates = hashFactory();
				data.put(doc.getDocumentElement().getTagName(), lg_updates.getHash());
				
				lg_updates.getFrom(doc.getDocumentElement(), "health");
				
				HashSet lg_locations = hashFactory();
				lg_updates.set("locations", lg_locations);
				NodeList chars = doc.getElementsByTagName("character");
				Object[] pList = new Object[chars.getLength()];
				
				for (int i = 0; i < chars.getLength(); i++) {
					HashSet plr = hashFactory();
					plr.getFrom(chars.item(i), "id", "name");
					HashSet ploc = hashFactory();
						ploc.getFrom(chars.item(i), "latitude", "longitude");
					plr.set("location", ploc);
					pList[i] = plr.getHash();
				}
				lg_locations.set("character", pList);
				
				Element lg_battle = (Element) doc.getElementsByTagName("battle").item(0);
				if (lg_battle != null) {
					HashSet lg_battles = hashFactory();
					lg_updates.set("battle", lg_battles);
					lg_battles.getFrom(lg_battle, "id", "initiator", "opponent", "status");
				}				
				Log.v(TAG,"Finished location get xml parse");
				return data;
				
			case BATTLE_GET:
				Log.v(TAG,"Battle Get Parsing");
				Hashtable<String, Object> bg_battle = new Hashtable<String, Object>();
				data.put(doc.getDocumentElement().getTagName(), bg_battle);
				bg_battle.put("status", getText(doc.getDocumentElement(), "status"));
				if (((String) bg_battle.get("status")).equals("none")){
					Log.v(TAG,"Finished battle get xml parse");
					return data;
				}
				if (findText(doc.getDocumentElement(),"experience") != null)
				bg_battle.put("experience", getText(doc.getDocumentElement(), "experience"));
				if (findText(doc.getDocumentElement(),"level-up") != null)
					bg_battle.put("level-up", getText(doc.getDocumentElement(), "level-up"));
				if (findText(doc.getDocumentElement(),"new-skill") != null)
					bg_battle.put("new-skill", getText(doc.getDocumentElement(), "new-skill"));
				Hashtable<String, Object> bg_player = new Hashtable<String, Object>();
				bg_battle.put("initiator", getText(doc.getDocumentElement(), "initiator"));
				bg_battle.put("player", bg_player);
				Element bg_player_node = (Element) doc.getElementsByTagName("player").item(0);
				bg_player.put("health", getText(bg_player_node, "health"));
				Hashtable<String, Object> bg_player_actions = new Hashtable<String, Object>();
				if (findText(bg_player_node,"action") != null) {
					bg_player.put("action", bg_player_actions);
					//bg_player_actions.put("type", getText(bg_player_node, "type"));
					bg_player_actions.put("ability", getText(bg_player_node, "ability"));
					bg_player_actions.put("status", getText(bg_player_node, "status"));
					bg_player_actions.put("effect", getText(bg_player_node, "effect"));
					//TODO: bg_player_actions.put("delay", getText(bg_player_node, "delay"));
				}
				
				Hashtable<String, Object> bg_opponent = new Hashtable<String, Object>();
				bg_battle.put("opponent", bg_opponent);
				Element bg_opponent_node = (Element) doc.getElementsByTagName("opponent").item(0);
				bg_opponent.put("id", getText(bg_opponent_node, "id"));
				bg_opponent.put("health", getText(bg_opponent_node, "health"));
				Hashtable<String, Object> bg_opponent_actions = new Hashtable<String, Object>();
				if (findText(bg_opponent_node,"action") != null) {
					bg_opponent.put("action", bg_opponent_actions);
					//bg_opponent_actions.put("type", getText(bg_opponent_node, "type"));
					bg_opponent_actions.put("ability", getText(bg_opponent_node, "ability"));
					bg_opponent_actions.put("status", getText(bg_opponent_node, "status"));
					bg_opponent_actions.put("effect", getText(bg_opponent_node, "effect"));
					//TODO: bg_opponent_actions.put("delay", getText(bg_opponent_node, "delay"));
				}

				Log.v(TAG,"Finished battle get xml parse");
				return data;
			
			/*	
			case CHAR_CREATE:
				Log.v(TAG,"Char create Parsing");
				Hashtable<String, Object> ch_create = new Hashtable<String, Object>();
				data.put(doc.getDocumentElement().getTagName(), ch_create);
				ch_create.put("name", getText(doc.getDocumentElement(), "name"));
				ch_create.put("class", getText(doc.getDocumentElement(), "class"));
				ch_create.put("password", getText(doc.getDocumentElement(), "password"));
				ch_create.put("id", getText(doc.getDocumentElement(), "id"));
				return data;
			*/
				
			case CHAR_CREATE:
			case CHAR_LOGIN:
				Log.v(TAG,"Char create Parsing");
				HashSet ch_create = hashFactory();
				data.put(doc.getDocumentElement().getTagName(), ch_create.getHash());
				ch_create.getFrom(doc.getDocumentElement(), "status", "message", "id");
				return data;
						
			case CHAR_INSPECT:
				Log.v(TAG,"Char inspect Parsing");
				HashSet ch_inspect = hashFactory();
				data.put(doc.getDocumentElement().getTagName(), ch_inspect.getHash());
				ch_inspect.getFrom(doc.getDocumentElement(), "id", "status", "name", "level",
						"class", "health", "max-health", "experience", "tnl");
				
				/*
				HashSet ci_location = hashFactory();
				ch_inspect.set("location", ci_location);
				ci_location.getFrom(doc.getDocumentElement(), "latitude", "longitude");
				*/
				
				HashSet ci_skills = hashFactory();
				ch_inspect.set("skills", ci_skills);
				NodeList ci_skill_list = doc.getElementsByTagName("skill");
				Object[] ci_skill_array = new Object[ci_skill_list.getLength()];
				for (int i = 0; i < ci_skill_list.getLength(); i++) {
					HashSet ci_sk = hashFactory();
					ci_sk.getFrom(ci_skill_list.item(i), "skill-name", "skill-id", "skill-level", "skill-type");
					ci_skill_array[i] = ci_sk.getHash();
				}
				ci_skills.set("skill", ci_skill_array);
				
				return data;
				
			case CHAR_CLASS:
				Log.v(TAG,"Char class Parsing");
				Hashtable<String, Object> cc_view = new Hashtable<String, Object>();
				data.put(doc.getDocumentElement().getTagName(), cc_view);
				cc_view.put("name", getText(doc.getDocumentElement(), "name"));
				cc_view.put("description", getText(doc.getDocumentElement(), "description"));
				
				Hashtable<String, Object> cc_skills = new Hashtable<String, Object>();
				cc_view.put("skills", cc_skills);
				NodeList cc_skill_list = doc.getElementsByTagName("skill");;
				Object[] cc_skill_array = new Object[cc_skill_list.getLength()];
				Hashtable<String, Object> cc_sk;
				for (int i = 0; i < cc_skill_list.getLength(); i++) {
					cc_sk = new Hashtable<String, Object>();
					cc_sk.put("skill-name", getText((Element) cc_skill_list.item(i), "skill-name"));
					cc_sk.put("min-level", getText((Element) cc_skill_list.item(i), "min-level"));
					cc_sk.put("formula", getText((Element) cc_skill_list.item(i), "formula"));
					cc_skill_array[i] = cc_sk;
				}
				cc_skills.put("skill", cc_skill_array);
				return data;
				
			case CHAR_CLASSES:
				Log.v(TAG,"Char classes Parsing");
				Hashtable<String, Object> cc_all = new Hashtable<String, Object>();
				data.put(doc.getDocumentElement().getTagName(), cc_all);
				Element test = doc.getDocumentElement();
				Log.v(TAG,doc.getDocumentElement().getTagName());
				NodeList cc_all_list = doc.getElementsByTagName("character-class");;
				Object[] cc_all_array = new Object[cc_all_list.getLength()];
				
				Element cc_element;
				Hashtable<String, Object> cc_single;
				Hashtable<String, Object> ca_skills;
				NodeList ca_skill_list;
				Object[] ca_skill_array;
				Hashtable<String, Object> ca_sk;
				
				for (int i = 0; i < cc_all_list.getLength(); i++) {
					cc_element = (Element) cc_all_list.item(i);
					cc_single = new Hashtable<String, Object>();
					cc_single.put("name", getText(cc_element, "name"));
					cc_single.put("description", getText(cc_element, "description"));

					ca_skills = new Hashtable<String, Object>();
					cc_single.put("skills", ca_skills);
					ca_skill_list = cc_element.getElementsByTagName("skill");
					ca_skill_array = new Object[ca_skill_list.getLength()];
					for (int j = 0; j < ca_skill_list.getLength(); j++) {
						ca_sk = new Hashtable<String, Object>();
						ca_sk.put("skill-name", getText((Element) ca_skill_list.item(j), "skill-name"));
						ca_sk.put("min-level", getText((Element) ca_skill_list.item(j), "min-level"));
						ca_sk.put("formula", getText((Element) ca_skill_list.item(j), "formula"));
						ca_skill_array[j] = ca_sk;
					}
					ca_skills.put("skill", ca_skill_array);
					cc_all_array[i] = cc_single;
				}
				cc_all.put("character-class", cc_all_array);				
				return data;
				
			default: //Standard Responses
				Log.v(TAG,"Standard response Parsing");
				Hashtable<String, Object> standard = new Hashtable<String, Object>();
				data.put(doc.getDocumentElement().getTagName(), standard);
				standard.put("status", getText(doc.getDocumentElement(), "status"));
				standard.put("message", getText(doc.getDocumentElement(), "message"));
				return data;
			}
			

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    //SAX Parser Version
    /*
    public Hashtable<String, Object>  parseXML(HttpResponse response, int requestType) {
    	try {
			HttpEntity entity = response.getEntity();
			SAXParserFactory spf = SAXParserFactory.newInstance();
			XMLHandler handler = new XMLHandler(new Hashtable<String, Object> ());
			Log.v(TAG,"Factory stuff");
			SAXParser parser = spf.newSAXParser();
			Log.v(TAG,"Parser");
			XMLReader reader = parser.getXMLReader();
			Log.v(TAG,"Reader");
			reader.setContentHandler(handler);
			Log.v(TAG,"Handle");
			reader.parse(retrieveInputStream(entity));
			Log.v(TAG,"Done");
			//parser.parse(entity.getContent(), handler);
	    	return handler.getHash();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
					e.printStackTrace();
		}
		return null;
    }*/
    
    private static InputSource retrieveInputStream(HttpEntity httpEntity) {
        InputSource insrc = null;
        try {
              insrc = new InputSource(httpEntity.getContent());
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return insrc;
    }
    
    public static void printData(Hashtable<String, Object> data) {
    	Enumeration<String> keys = data.keys();
    	while (keys.hasMoreElements()) {
    		String key = keys.nextElement();
    		Object value = data.get(key);
    		if (value.getClass() == data.getClass()) {
    			System.out.println(key.toString() + ": " + data.get(key).toString());
    			printData((Hashtable<String, Object>) value);
    		} else {
    			System.out.println(key.toString() + ": " + data.get(key).toString());
    		}
    	}
    }
}
