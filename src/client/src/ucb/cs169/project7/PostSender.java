/*
package ucb.cs169.project7;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public class PostSender {
	private static final String TAG = "PostSender";
	ByteArrayOutputStream out;	
	
    protected void playerListRequest(int id) {
    	XmlSerializer xs = Xml.newSerializer();
    	out = new ByteArrayOutputStream();
    	
    	try {
			xs.setOutput(out, "UTF-8");
			xs.startDocument(null, null);
			xs.startTag(null, "update");
			xs.attribute(null, "version", "1");
			xs.attribute(null, "packetID", "0");
			xs.attribute(null, "characterID", "1");
			xs.startTag(null, "mode");
			xs.text("active");
			xs.endTag(null, "mode");
			xs.startTag(null, "status");
			xs.text("battle");
			xs.endTag(null, "status");
			xs.startTag(null, "battle");
			xs.startTag(null, "opponent");
			xs.text(Integer.toString(1));
			xs.endTag(null, "opponent");
			xs.startTag(null, "initiate");
			xs.text("initiate");
			xs.endTag(null,"initiate");
			xs.endTag(null,"battle");
			xs.endTag(null,"update");
			xs.endDocument();
			xs.flush();
			Log.v(TAG, "Finished creating XML");
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
	}	
		
    protected void send(WorldState myState) {
    	try {
    		HttpHost host = new HttpHost("10.0.2.2", 3000, "http");
			HttpPost post = new HttpPost("http://10.0.2.2:3000/androids/1/fetch");
			post.addHeader("Accept", "text/xml");
			post.addHeader("Content-Type", "application/xml");			        
			DefaultHttpClient client = new DefaultHttpClient();
			StringEntity sendEntity = new StringEntity(out.toString(), "UTF-8");
			sendEntity.setContentType("application/xml");
			post.setEntity(sendEntity);
			Log.v(TAG,"Created header file");
			HttpResponse response = client.execute(host, post);
			Log.v(TAG,"Recieved response");
			HttpEntity entity = response.getEntity();
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
			
			
		}
    
    private InputSource retrieveInputStream(HttpEntity httpEntity) {
        InputSource insrc = null;
        try {
              insrc = new InputSource(httpEntity.getContent());
        } catch (Exception e) {
        }
        return insrc;
    }
}
*/