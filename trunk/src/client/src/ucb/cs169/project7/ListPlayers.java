package ucb.cs169.project7;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ListPlayers extends ListActivity implements ClientComponent {
	private Player players[];
	private ServerLink serverLink;
	private Timer timer;
	private TimerTask task;
	RoleClientApplication application;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        players = new Player[0];
        serverLink = new ServerLink(this);
        ServerLinkService.callBack = this;
        setContentView(R.layout.listplayersview);
        application = (RoleClientApplication) getApplication();
        
        //handle button
        Button back = (Button) findViewById(R.id.listPlayersBackButton);
        back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	application.playButtonSound();
            	Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        
        /*
        timer = new Timer();
        
    	task = new TimerTask() {
    		public void run() {
    			Log.d("timer", "run");
    	    	serverLink.sendRequest(ServerLink.LOC_GET);
    		}
    	};
    	timer.schedule(task, 5000, 5000);
    	*/
    	//serverLink.sendRequest(ServerLink.LOC_GET);
//        setListAdapter(new ArrayAdapter<Player>(this, R.layout.listplayers, players));
    }

    @Override
    public void onStop() {
    	//timer.cancel();
    	super.onStop();
    	
    }
    

	public void receiveData(Hashtable<String, Object> data, int requestType) {
		try {
			if (data == null) {
				//ERROR
				Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
				return;
			}
	    	if (requestType == ServerLink.LOC_GET) {
	    		Log.d(this.getLocalClassName(), "Updating list of players");
	    		updatePlayers(data);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}		
    }
    	
	public void updatePlayers(Hashtable<String, Object> data) {
		Hashtable<String, Object> updates = (Hashtable<String, Object> ) data.get("updates");
		Object locations[] = (Object[]) ((Hashtable<String, Object> ) updates.get("locations")).get("character");
		players = new Player[locations.length];
		for (int i = 0; i < locations.length; i++) {
			players[i] = new Player();
			players[i].setId(Integer.parseInt((String) ((Hashtable<String, Object>) locations[i]).get("id")));
			players[i].setName((String)((Hashtable<String, Object>) locations[i]).get("name"));
		}
		setListAdapter(new ArrayAdapter<Player>(this, R.layout.listplayers, players));		
	}
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        application.playButtonSound();
        
    	Intent myIntent = new Intent(v.getContext(), SelectedPlayerInfo.class);
    	myIntent.putExtra("id", players[position].getId());
    	//timer.cancel();
    	startActivityForResult(myIntent, 0);
        
        /*
        Intent myIntent = new Intent("role.intent.action.BATTLE.SEND_INVITE", Uri.parse("id://" + players[position].getId()), v.getContext(), Battle.class);
    	//timer.cancel();
        startActivityForResult(myIntent, 0);        
        */
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
		  try {
			    {
			      ServerLinkService.callBack = this;
			      Intent svc = new Intent(this, ServerLinkService.class);
			      startService(svc);
			    }
			  }
			  catch (Exception e) {
			    Log.e(this.getLocalClassName(), "Server started already", e);
			  }	
        ServerLinkService.callBack = this;
    }
    
	public void startPoll() {	
	}
    
}


