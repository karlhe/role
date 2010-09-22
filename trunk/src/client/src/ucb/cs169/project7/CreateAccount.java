package ucb.cs169.project7;

import java.util.Hashtable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateAccount extends Activity implements ClientComponent {	
	Button save, cancel; EditText name, password; 
	private boolean isClassSelected; 
	private String charSelectedName;
	private ServerLink serverLink;
	private TextView feedback;
	RoleClientApplication application;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v("CreateAccount","Create Account made");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount);
        
        isClassSelected = false; //keeps track of whether the player already selected a class
    	serverLink = new ServerLink(this);
    	
        name = (EditText) findViewById(R.id.createAccountNameText);    
        password = (EditText) findViewById(R.id.createAccountPasswordText);
        feedback = (TextView) findViewById(R.id.createAccountFeedback);
        feedback.setTextColor(Color.RED);
        
    	application = (RoleClientApplication) getApplication();
    	
        Button selectChar = (Button) findViewById(R.id.createAccountSelectChar);
        selectChar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	application.playButtonSound();
            	Intent myIntent = new Intent(v.getContext(), CharacterSelection.class);
            	startActivityForResult(myIntent, 0);
            }
        });
        
        //handle save button
        save = (Button) findViewById(R.id.createAccountSaveButton);
        save.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	application.playButtonSound();
            	String nameText = name.getText().toString();
                String passwordText = password.getText().toString();
                
                if (nameText.length() == 0) {
                	String msg = "Please enter a login name.";
                	Toast.makeText(CreateAccount.this,msg,Toast.LENGTH_LONG).show();
                	feedback.setText(msg);
                } else if (passwordText.length() == 0) {
                	String msg = "Please enter a password.";
                	Toast.makeText(CreateAccount.this,msg,Toast.LENGTH_LONG).show();
                	feedback.setText(msg);
                } else if (!isClassSelected) {
                	String msg = "Please select a character class.";
                	Toast.makeText(CreateAccount.this,"Please select a character class.",Toast.LENGTH_LONG).show();
                	feedback.setText(msg);
                } else {
                	//send server the entered name/password/char
                   	Hashtable<String, Object> data = new Hashtable<String, Object>();
                	data.put("name", nameText);
                	data.put("password", passwordText);
                	data.put("class", charSelectedName);
                	serverLink.sendRequest(ServerLink.CHAR_CREATE, data);
                }
            }
        });
        
        //handle cancel button
        cancel = (Button) findViewById(R.id.charSelectionCancelButton);
        cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	application.playButtonSound();
            	Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (data != null) {
        	Bundle extras = data.getExtras();
        	if (extras != null) {
            	if (extras.containsKey("characterSelected")) {
            		charSelectedName = extras.getString("characterSelected");
            		isClassSelected = true;
            	}
  
            	
        	}
    	}
    }
    
	public void receiveData(Hashtable<String, Object> data, int requestType) {
		try {
	    	/*
			//if server OKs,
	    	boolean server = true;
	    	if (server) {
	        	// server says name/password is ok
	    		
	    		//use charSelectedName  (name of class selected)
	    		
	    		Intent intent = new Intent();
	            setResult(RESULT_OK, intent);
	            finish();
	    	} else {
	    		// server said something is wrong with name/password	
	    		//re-enter data
	    	}
	    	*/
			RoleClientApplication application = (RoleClientApplication) getApplication();
	    	if (requestType == ServerLink.CHAR_CREATE) {
	    		Hashtable<String, Object> character = (Hashtable<String, Object>) data.get("character");
	    		String status = (String) character.get("status");
	    		if (status.equals("success")) {
	    			application.saveLogin(name.getText().toString(), password.getText().toString());
	    			application.setPlayerId(Integer.parseInt((String) character.get("id")));
	    			Toast.makeText(this, (String) character.get("message"), Toast.LENGTH_SHORT).show(); 
	    			int charId = application.getPlayerId();
	    			serverLink.sendServiceRequest(ServerLink.CHAR_INSPECT, charId);    	        
	    			
	    		} else if (status.equals("error")) {
					String message = (String) character.get("message");
					Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
					feedback.setText(message);
	    		} //else wtf!?
	    	}
	    	if (requestType == ServerLink.CHAR_INSPECT) {
	    		Hashtable<String, Object> character = (Hashtable<String, Object>) data.get("character");
	    		Object skillList[] = (Object[]) ((Hashtable<String, Object> ) character.get("skills")).get("skill");
	    		String[] skills = new String[skillList.length];
	    		for (int i = 0; i < skillList.length; i++) {
	    			skills[i] = (String) ((Hashtable<String, Object>) skillList[i]).get("skill-name");
	    		}
	    		application.setSkills(skillList);
	    		application.setmaxhealth(Integer.parseInt((String) character.get("max-health")));
	    		application.setPlayerName((String) character.get("name"));
	    		
	    		Intent intent = new Intent();
	            intent.putExtra("isLoggedIn", true);
				setResult(Activity.RESULT_OK, intent);
				finish();
	    	}		
		}
		catch (Exception e){
			e.printStackTrace();
		}		
    }
	
	public void startPoll() {
	}

}