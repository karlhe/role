package ucb.cs169.project7;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LoginScreen extends Activity{
	RoleClientApplication application;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.login);            
        
        application = (RoleClientApplication) getApplication();
        
        Button createAccount = (Button) findViewById(R.id.loginCreateAccountButton);
        createAccount.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	playButtonSound();
            	if (LoginScreen.checkInternet(v.getContext())) {
	            	Intent myIntent = new Intent(v.getContext(), CreateAccount.class);
	            	startActivityForResult(myIntent, 0);
            	}
            	else {
            		Toast.makeText(LoginScreen.this, "You are not connected to the internet.", Toast.LENGTH_SHORT).show();
            	}
            }
        }); 
        
        Button useAccount = (Button) findViewById(R.id.loginUseAccountButton);
        useAccount.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	playButtonSound();
            	if (LoginScreen.checkInternet(v.getContext())) {
	            	Intent myIntent = new Intent(v.getContext(), UseAccount.class);
	            	startActivityForResult(myIntent, 0);
            	}
            	else {
            		Toast.makeText(LoginScreen.this, "You are not connected to the internet.", Toast.LENGTH_SHORT).show();
            	}
            }
        }); 
        
        Log.v("LoginScreen", "onCreate");
        
    }
    
    public void playButtonSound() {
    	application.playButtonSound();
    }
    
    @Override
	public void onPause() {
    	super.onPause();
    	//application.pauseMusic();
    }
    
    @Override
	public void onStop() {
    	super.onStop();
    	//application.pauseMusic();
    }
    
    @Override
	public void onResume() {
    	super.onResume();
    	application.resumeMusic();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (data != null) {
        	Bundle extras = data.getExtras();
        	if (extras != null) {
        		if (extras.containsKey("isLoggedIn")) {
        			System.out.println("Redirect to home");
        			setResult(Activity.RESULT_OK, data);
        			finish();        			
        		}
        		else {
        			System.out.println("Empty data back");
        		}
        	}
    	}
    }    
    
    public static boolean checkInternet(Context c) {
    	ConnectivityManager connect = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connect.getNetworkInfo(0).isConnectedOrConnecting() || connect.getNetworkInfo(1).isConnectedOrConnecting() ) { 
        	return true;                        	  
        } 
        return false;
    }
}
