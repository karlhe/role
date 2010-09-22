package ucb.cs169.project7;

import java.util.Hashtable;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectedPlayerInfo extends Activity implements ClientComponent {	
	TextView opponentName, description; ImageView iv;
	ServerLink serverLink; 
	String opponentname;
	int charId, opponentmaxhealth;
	boolean infoReceived;
	RoleClientApplication application;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectedplayerinfo);
        
        description = (TextView) findViewById(R.id.selectedPlayerInfo);
    	iv = (ImageView) findViewById(R.id.charImage); 
    	application = (RoleClientApplication) getApplication();
    	
    	/*
        //opponentName.setText("Taki");
        String info = "Assassin" + "\n" 
			+ "A specialist in stealth. Difficult to detect." + "\n"
			+ "Level 2"
			+ "Health:\t" + "100" + "\n" 
			+ "Attack:\t" + "150" + "\n" 
			+ "Defense:\t" + "75" + "\n" 
			+ "Stealth:\t" + "150";
        description.setText(info);
        */
    	
        description.setText("Loading...");
        
        Bundle extras = getIntent().getExtras();            
        charId = extras.getInt("id"); //selected player id
        Log.v("SelectPlayerInfo",Integer.toString(charId));
        
        serverLink = new ServerLink(this);
       	//Hashtable<String, Object> data = new Hashtable<String, Object>();
    	//data.put("id", charId); blah blah //TODO fix this 
       	infoReceived = false;
        serverLink.sendRequest(ServerLink.CHAR_INSPECT, charId);
        
        //handle button
        Button back = (Button) findViewById(R.id.selectedPlayerBackButton);
        back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	application.playButtonSound();
            	Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    	
        //handle button
        Button fight = (Button) findViewById(R.id.selectedPlayerFightButton);
        fight.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	application.playButtonSound();
            	RoleClientApplication app = (RoleClientApplication) getApplication();
            	if (!infoReceived) {
            		Toast.makeText(SelectedPlayerInfo.this,"Please wait for player info to load.",Toast.LENGTH_SHORT).show();
            		return;
            	}
            	synchronized (app.getBattleLock()) {
	        		app.setBattle(true);
	        		Intent svc = new Intent(v.getContext(), ServerLinkService.class);
	        		stopService(svc);
	                Intent myIntent = new Intent("role.intent.action.BATTLE.SEND_INVITE", Uri.parse("id://" + charId), v.getContext(), Battle.class);
	                myIntent.putExtra("opponentmaxhealth", opponentmaxhealth);
	                myIntent.putExtra("opponentname", opponentname);
	                startActivityForResult(myIntent, 0);
            	}
            	
            	/*
            	Intent myIntent = new Intent(v.getContext(), Battle.class);
            	//myIntent.putExtra("isChallenger", false); //used to differentiate between who is challenger and who is acceptor
                startActivity(myIntent);	
                setResult(RESULT_OK);
                finish();
                */
            	
            }
        });  
    }
   
    public void updateDescription(Hashtable<String, Object> data) {
    	
    	Hashtable<String, Object> character = (Hashtable<String, Object>) data.get("character");
    	//String status = (String) character.get("status");
    	String name = (String) character.get("name");
    	opponentname = name;
    	String level = (String) character.get("level");
    	String charClass = (String) character.get("class");
    	String health = (String) character.get("health");
    	String maxhealth = (String) character.get("max-health");
    	opponentmaxhealth = Integer.parseInt(maxhealth);
    	//Hashtable<String,Object> location = (Hashtable<String,Object>) character.get("location");
    	//String latitude = (String) location.get("latitude");
    	//String longitude = (String) location.get("longitude");
    	String info = name 
    				//+"\tStatus: " + status 
    				+ "\tLevel: " + level
    				+ "\tClass: " + charClass 
    				+ "\tHealth: " + health
    				+ "/" + maxhealth
    				//+ "\tlatitude: " + latitude 
    				//+ "\tLongitude: " + longitude
    				;
    	
    	description.setText(info);
    	infoReceived = true;
    	
		if (charClass.equals("Assassin")) {
			iv.setImageResource(R.drawable.assassin);
		} 
		else if (charClass.equals("Paladin")) {
			iv.setImageResource(R.drawable.paladin);
		}
		else if (charClass.equals("Warrior")) {
			iv.setImageResource(R.drawable.warrior);
		}
     }
    
	public void receiveData(Hashtable<String, Object> data, int requestType) {
		try {
	    	if (requestType == ServerLink.CHAR_INSPECT) {
	    		updateDescription(data);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}		
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
		ServerLinkService.callBack = this;
		Intent svc = new Intent(this, ServerLinkService.class);
		startService(svc);	
	}
    
}