package ucb.cs169.project7;

import java.util.Hashtable;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BattleAlert extends Activity implements ClientComponent{
	ImageView iv; String playerName; TextView engageText;
	ServerLink serverLink; 
	int opponentmaxhealth;
	boolean infoReceived, initiate;
	ImageButton fight, run;
	RoleClientApplication application;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battlealert);
        
        application = (RoleClientApplication) getApplication();    
        
        application.stopMusic();
        application.startAlertMusic();
        
        Intent i = getIntent();
   		final int opponentId = Integer.parseInt(i.getData().getHost());
   		initiate = i.getExtras().getBoolean("initiate");
   		
        serverLink = new ServerLink(this);
       	//Hashtable<String, Object> data = new Hashtable<String, Object>();
    	//data.put("id", charId); blah blah //TODO fix this 
       	infoReceived = false;
        serverLink.sendRequest(ServerLink.CHAR_INSPECT, opponentId);
        
    	iv = (ImageView) findViewById(R.id.charImage); 
    	engageText = (TextView) findViewById(R.id.engageText);

    	playerName = "Loading...";    		   
        
        //handle button
        fight = (ImageButton) findViewById(R.id.fight);
        fight.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {            	
                // Perform action on clicks
            	if (!infoReceived) {
            		Toast.makeText(BattleAlert.this,"Please wait for opponent info to load.",Toast.LENGTH_SHORT).show();
            		return;
            	}
            	synchronized (application.getBattleLock()) {
            		application.setBattle(true);
            		application.stopMusic();
            		application.startWorldMusic();
	                Intent myIntent = new Intent("role.intent.action.BATTLE.ACCEPT_INVITE", Uri.parse("id://" + opponentId), v.getContext(), Battle.class);
	                myIntent.putExtra("opponentmaxhealth", opponentmaxhealth);
	                myIntent.putExtra("opponentname", playerName);                
	                startActivity(myIntent);	
	                setResult(RESULT_OK);
	                finish();
            	}
            }
        });    	
    	
        //handle button
        run = (ImageButton) findViewById(R.id.run);
        run.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	if (!infoReceived) {
            		Toast.makeText(BattleAlert.this,"Please wait for opponent info to load.",Toast.LENGTH_SHORT).show();
            		return;
            	}
            	synchronized (application.getBattleLock()) {
	        		application.setBattle(false);
	            	application.stopMusic();
	            	application.startWorldMusic();
	        		rejectBattle(opponentId);
	            	
	            	Intent intent = new Intent();
	                setResult(RESULT_OK, intent);
	        		Intent svc = new Intent(((Activity) ServerLinkService.callBack), ServerLinkService.class);
	        		startService(svc);	
	                finish();
            	}
            }
        });
    	
        //handle button
        /*
        Button autoEngage = (Button) findViewById(R.id.autoengage);
        autoEngage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	//TODO: auto-engage mode
            	Toast.makeText(BattleAlert.this,"This feature has not been implemented.",Toast.LENGTH_SHORT).show();
            }
        });
        */
    }
    
    private void rejectBattle(int opponentId){
    	Hashtable<String, Object> data = new Hashtable<String, Object>();
    	data.put("opponent", opponentId);
    	data.put("type", "reject");
    	serverLink.sendRequest(ServerLink.BATTLE_SEND, data);
    }
    
    public void receiveData(Hashtable<String,Object> data, int requestType) {
    	try {
			if (requestType == ServerLink.CHAR_INSPECT) {
				Hashtable<String, Object> character = (Hashtable<String, Object>) data.get("character");
	        	String maxhealth = (String) character.get("max-health");
	        	opponentmaxhealth = Integer.parseInt(maxhealth);
	        	String name = (String) character.get("name");
	        	String charClass = (String) character.get("class");    	
	        	String level = (String) character.get("level");
	        	String health = (String) character.get("health");   	
	        	playerName = name;
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
	    		if (initiate) {
	    			engageText.setText("You have challenged " + playerName + "!");
	    			run.setClickable(false);
	    			run.setEnabled(false);
	    		}
	    		else {
	    			String info = playerName + " has engaged you!" + "\n"
	    			+ playerName + "'s stats:\n"
    				+ "\tLevel: " + level
    				+ "\tClass: " + charClass 
    				+ "\tHealth: " + health
    				+ "/" + maxhealth
    				;
	    			
	    			engageText.setText(info);
	    		}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}		
    }
    
	protected void onPause() {
		super.onPause();
		application.pauseMusic();
	}
	
    protected void onResume() {
    	super.onResume();
    	application.resumeMusic();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Toast.makeText(BattleAlert.this,"Please select an option.",Toast.LENGTH_SHORT).show();
            return true;
        }	

        return super.onKeyDown(keyCode, event);
    }    
    
    public void startPoll(){};
    
}