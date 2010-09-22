package ucb.cs169.project7;

import java.util.Hashtable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CharStatus extends Activity implements ClientComponent {	
	TextView description; ImageView iv;
	ServerLink serverLink;
	RoleClientApplication application;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charstatus);
        
        description = (TextView) findViewById(R.id.charStatusInfo);
    	iv = (ImageView) findViewById(R.id.charStatusImage); 
    	
    	application = (RoleClientApplication) getApplication();
    	
        /*
        String info = "Assassin" + "\n" 
			+ "A specialist in stealth. Difficult to detect." + "\n"
			+ "Level 2\n"
			+ "Health:\t" + "100" + "\n" 
			+ "Attack:\t" + "150" + "\n" 
			+ "Defense:\t" + "75" + "\n" 
			+ "Stealth:\t" + "150";
        description.setText(info);
        */
        
        description.setText("Loading...");
       
        int charId = application.getPlayerId();
        if (charId == 0) {
        	charId = 1;
        }
        serverLink = new ServerLink(this);
       	Hashtable<String, Object> data = new Hashtable<String, Object>();
    	//data.put("id", charId); blah blah //TODO fix this 
        serverLink.sendRequest(ServerLink.CHAR_INSPECT, charId);
        
        
        //handle button
        Button back = (Button) findViewById(R.id.charStatusBackButton);
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
        Button skills = (Button) findViewById(R.id.charStatusSkillsButton);
        skills.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	application.playButtonSound();
            	Toast.makeText(CharStatus.this,"Skill tree has not yet been implemented",Toast.LENGTH_LONG).show();
            }
        });  
    }
    
    public void updateDescription(Hashtable<String, Object> data) {
    	
    	Hashtable<String, Object> character = (Hashtable<String, Object>) data.get("character");
    	//String status = (String) character.get("status");
    	String name = (String) character.get("name");
    	String level = (String) character.get("level");
    	String charClass = (String) character.get("class");
    	String health = (String) character.get("health");
    	String maxhealth = (String) character.get("max-health");
    	//Hashtable<String,Object> location = (Hashtable<String,Object>) character.get("location");
    	//String latitude = (String) location.get("latitude");
    	//String longitude = (String) location.get("longitude");
    	String info = name 
    		//+"\tStatus: " + status 
    				+ "\tLevel: " + level
    				+ "\tClass: " + charClass + "\tHealth: " + health
    				+ "\tHealth: " + health + "/" + maxhealth;
    				//+ "\tlatitude: " + latitude + "\tLongitude: " + longitude;
    	
    	description.setText(info);    	
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
    
	public void startPoll() {
		ServerLinkService.callBack = this;
		Intent svc = new Intent(this, ServerLinkService.class);
		startService(svc);	
	}
}