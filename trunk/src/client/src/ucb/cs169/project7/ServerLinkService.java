package ucb.cs169.project7;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class ServerLinkService extends Service implements ClientComponent{
	
	public static ClientComponent callBack;
	private DefaultHttpClient serverClient;
	
	private static final String TAG = "ServerLinkService";	
	
	private Timer timer;
	private static final long UPDATE_INTERVAL = 10000;
	private static ServerLink Link;	

	public static void setCallBack(ClientComponent c) {
	  callBack = c;
	}

	@Override
	public IBinder onBind(Intent intent) {
	  return null;
	}

	@Override
	public void onCreate() {
	  super.onCreate();
	  timer = new Timer();
	  Link = new ServerLink(this);
	  startService();

	  Log.v(TAG, "Service started");
	}

	@Override
	public void onDestroy() {
	  super.onDestroy();

	  shutdownService();

	  Log.v(TAG, "Service stopped");
	}

	private void startService() {
	  timer.scheduleAtFixedRate(
	      new TimerTask() {
	        @Override
			public void run() {
	        		getUpdate();
	        }
	      },
	      0,
	      UPDATE_INTERVAL);
	  Log.i(getClass().getSimpleName(), "Timer started!");
	}

	private void getUpdate() {
	  // http post to the service
	  Log.i(getClass().getSimpleName(), "Background process starting.");

	  try {
		  //Link.setCallBack(callBack);
		  RoleClientApplication app = (RoleClientApplication) callBack.getApplication();
		  	if (app.getPlayerId() != 0) {
    			  Link.sendServiceRequest(ServerLink.LOC_GET);
		  	}
    		  /*
    		  if (MODE == BATTLE) {
    			  Link.sendRequest(ServerLink.BATTLE_GET);
    		  }
    		  */		                	       
		  Log.i(callBack.getClass().getSimpleName(), "Background running.");
	  }
	  catch (Exception e) {
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    e.printStackTrace(pw);
	    Log.e(getClass().getSimpleName(), sw.getBuffer().toString(), e);
	  }

	  Log.i(getClass().getSimpleName(), "Background task done");

	}

	private void shutdownService() {
	  if (timer != null) timer.cancel();
	  Log.i(getClass().getSimpleName(), "Timer stopped!");
	}
	
	public void receiveData(final Hashtable<String, Object> data, final int requestType) {
		try {
			Log.i(getClass().getSimpleName(), "Recieved data from server");
			if (data == null) return;
			if (requestType == ServerLink.BATTLE_GET) return; //Ignore a complete battle call.
			final Hashtable<String, Object> updates = (Hashtable<String, Object> ) data.get("updates");
			try {			
				  ((Activity) callBack).runOnUiThread(
				          new Runnable() {
				              public void run() {			            	  
				            	  if (callBack != null) {
				            		  RoleClientApplication app = (RoleClientApplication) callBack.getApplication();
				            		  Log.w(getClass().getSimpleName(), "Callback exists");
				            		  synchronized (app.getBattleLock()) {
				            			if (app.getBattle()) {
				            				Log.w(getClass().getSimpleName(), "Already in battle");
				            				callBack.receiveData(data, requestType);
				            				return;
				            			}
				            			if (updates.containsKey("battle")) {				            				
				            				Log.w(getClass().getSimpleName(), "Found battle");
				            				//timer.cancel();
				            				//callBack.startBattle(updates);
				            				Context c = callBack.getApplication().getApplicationContext();
				            				Log.v("ROLECLIENT", "Starting Battle alert");
				            				Hashtable<String, Object> battle = ((Hashtable<String, Object>) updates.get("battle"));
				            				int opponentId = Integer.parseInt((String) battle.get("opponent"));
				            				if (opponentId == 0) {
				            					System.out.println("Battle rejected");
				            					callBack.receiveData(data, requestType);
				            					return;				            					
				            				}
				            				if (battle.get("status").equals("completed") || battle.get("status").equals("rejected")){
				            					//Ignore completed battles, send a battle get to clear battle.
				            					Link.sendServiceRequest(ServerLink.BATTLE_GET);
				            					return;
				            				}
				            				app.setBattle(true);
				            			    int initiator = Integer.parseInt((String) battle.get("initiator"));
				            			    if (initiator != app.getPlayerId()) {				            			    	
				            			    	Log.v("Battle Alert","Not intiator" + Integer.toString(initiator) + Integer.toString(((RoleClientApplication) callBack.getApplication()).getPlayerId()));
					            				Intent myIntent = new Intent("role.intent.action.BATTLE_ALERT.RECEIVE_INVITE", Uri.parse("id://" + opponentId), (Activity) callBack, BattleAlert.class);
					            			    Intent svc = new Intent((Activity) callBack, ServerLinkService.class);
					            			    stopService(svc);
					            			    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					            			    myIntent.putExtra("initiate", false);
					            			    c.startActivity(myIntent);
					            			    //callBack.receiveData(data, requestType);					            			    
				            			    }
				            			    if (initiator == app.getPlayerId()) {
				            			    	Log.v("Battle Alert","Initiator" + Integer.toString(initiator) + Integer.toString(((RoleClientApplication) callBack.getApplication()).getPlayerId()));
					            				Intent myIntent = new Intent("null", Uri.parse("id://" + opponentId), (Activity) callBack, BattleAlert.class);
					            			    Intent svc = new Intent((Activity) callBack, ServerLinkService.class);
					            			    stopService(svc);
					            			    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					            			    myIntent.putExtra("initiate", true);
					            			    c.startActivity(myIntent);
					            			    //callBack.receiveData(data, requestType);					            			    
				            			    }
				            			}
				            		  }
				            	  }
				            	 //Log.w(getClass().getSimpleName(), "Calling callback");
				          		 callBack.receiveData(data, requestType);
				              }
				          }
						  );
				  }
				  catch (Exception e) {
				    StringWriter sw = new StringWriter();
				    PrintWriter pw = new PrintWriter(sw);
				    e.printStackTrace(pw);
				    Log.e(getClass().getSimpleName(), sw.getBuffer().toString(), e);
				  }		
		}
		catch (Exception e){
			e.printStackTrace();
		}		
	}
	
	public void startPoll() {
	}
	
}
