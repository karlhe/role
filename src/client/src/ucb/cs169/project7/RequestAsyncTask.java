package ucb.cs169.project7;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class RequestAsyncTask extends AsyncTask<Hashtable<String, Object>, Void, Hashtable<String, Object>> {
    private int requestType;
    private int playerId;
    private ClientComponent callBack;
    //private static DefaultHttpClient httpClient;
    private DefaultHttpClient httpClient;
    public boolean send = false;
        
    private static String TAG = "AsyncTask";
    
    
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

    static {
        //httpClient = new DefaultHttpClient();
    }

    public RequestAsyncTask(int requestType, ClientComponent callback) {
        this.requestType = requestType;
        this.callBack = callback;
        httpClient = new DefaultHttpClient();
        Log.v(TAG,"Async Task Created");
    }
    
    @Override
    public Hashtable<String, Object> doInBackground(Hashtable<String, Object>... data) {
    	Log.v(TAG,"Do in background");
		HttpResponse response = getResponse(data[0]);
		Log.v(TAG,"Recieved Response");
		Hashtable<String, Object> received = ServerLink.parseXML(response, requestType);
		Log.v(TAG,"Parsed XML");
		//ServerLink.printData(received);
		return received;
    }
    
    @Override
    public void onPostExecute(Hashtable<String, Object> results) {
    	Log.v(TAG,"Post execute");
        callBack.receiveData(results, requestType);
        Log.v(TAG,"callBack done");
        cancel(true);
    }
    
    protected HttpResponse getResponse(Hashtable<String,Object> data) {
    	String url_address = ADDRESS[requestType];
    	if (requestType == ServerLink.LOC_GET || requestType == ServerLink.BATTLE_GET) {
    		int userid = ((RoleClientApplication) callBack.getApplication()).getPlayerId();
    		url_address = url_address + new Integer(userid).toString();
    		if (userid == 0) {
    			Log.v(TAG + new Integer(requestType).toString(),"Not supposed to be here");
    			//Log.v(callBack.getClass().getSimpleName(),"Not supposed to be here");
    			//throw new NullPointerException(null);
    			return null;
    		}
    	}
    	if (requestType == ServerLink.CHAR_INSPECT){
    		url_address = url_address + ((Integer) data.get("playerId")).toString();
    	}
    	Log.v(TAG,"Address Created");
    	try {
    	    if (requestType == ServerLink.CHAR_INSPECT || requestType == ServerLink.BATTLE_GET
    	    		|| requestType == ServerLink.LOC_GET || requestType == ServerLink.CHAR_CLASS
    	    		|| requestType == ServerLink.CHAR_CLASSES) {
    	    	Log.v(callBack.getClass().getSimpleName(),"Async Get Sent");
	    	    return httpClient.execute(new HttpGet(url_address));  
	    	} else {	    		
    			HttpPost post = new HttpPost(url_address);
    			ByteArrayOutputStream xml = ServerLink.createXML(data, requestType);
    			post.addHeader("Accept", "text/xml");
    			post.addHeader("Content-Type", "application/xml");			        
    			StringEntity sendEntity = new StringEntity(xml.toString(), "UTF-8");
    			sendEntity.setContentType("application/xml");
    			post.setEntity(sendEntity);
    			Log.v(callBack.getClass().getSimpleName(),"Async Post Sent");
    			return httpClient.execute(post);
	    	}
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
}