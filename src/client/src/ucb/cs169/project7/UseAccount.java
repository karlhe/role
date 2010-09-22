package ucb.cs169.project7;

import java.util.Hashtable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UseAccount extends Activity implements ClientComponent {	
	private boolean requestingLogin;
	private ServerLink serverLink;
	private EditText name, password;
	private Button save;
	private TextView feedback;
	RoleClientApplication application;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.useaccount);

        requestingLogin = false;
    	serverLink = new ServerLink(this);

        name = (EditText) findViewById(R.id.useAccountNameText);    
        password = (EditText) findViewById(R.id.useAccountPasswordText);
		save = (Button) findViewById(R.id.useAccountSaveButton);
		feedback = (TextView) findViewById(R.id.useAccountFeedback);
		feedback.setTextColor(Color.RED);
		application = (RoleClientApplication) getApplication();
		
        TextWatcher watcher = new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (name.getText().toString().length() > 0 && password.getText().toString().length() > 0 && !isRequestingLogin()) {
					save.setEnabled(true);
				} else {
					save.setEnabled(false);
				}
				
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}       	
        };
        
        name.addTextChangedListener(watcher);
        password.addTextChangedListener(watcher);
        
    }
    
	public boolean isRequestingLogin() {
		return requestingLogin;
	}
    public void onClickCancelButton(View v) {
    	application.playButtonSound();
    	setResult(Activity.RESULT_CANCELED, null);
    	finish();
    }

    public void onClickSaveButton(View v) {
    	application.playButtonSound();
    	
    	((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
       	save.setEnabled(false);

       	requestingLogin = true;

       	Hashtable<String, Object> data = new Hashtable<String, Object>();
    	data.put("name", name.getText().toString());
    	data.put("password", password.getText().toString());
    	serverLink.sendRequest(ServerLink.CHAR_LOGIN, data);
    	
    	/*
    	data = new Hashtable<String, Object>();
    	Hashtable<String, Object> character = new Hashtable<String, Object>();
    	character.put("status", "success");
    	character.put("id","1");
    	data.put("character", character);
    	receiveData(data, 0);
       	*/
    }

	public void receiveData(Hashtable<String, Object> data, int requestType) {
		try {
	
			requestingLogin = false;
	
			RoleClientApplication application = (RoleClientApplication) getApplication();
			
			Hashtable<String, Object> character = (Hashtable<String, Object>) data.get("character-session");
			
			if (requestType == ServerLink.CHAR_LOGIN) {
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
				character = (Hashtable<String, Object>) data.get("character");
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
			
			if (name.getText().toString().length() > 0 && password.getText().toString().length() > 0)
				save.setEnabled(true);
		}
		catch (Exception e){
			e.printStackTrace();
		}		
		
	}
	
	public void startPoll() {
	}
}