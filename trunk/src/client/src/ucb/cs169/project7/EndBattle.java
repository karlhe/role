package ucb.cs169.project7;

import java.util.Hashtable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EndBattle extends Activity implements ClientComponent{
	public static final int WIN = 1;
	public static final int LOSE = 2;
	public static final int RUN = 3;
	public static final int REJECT = 4;
	
	ImageView iv; TextView title, levelUp, exp;
	int state, level, experience;
	boolean leveled, pending;	
	RoleClientApplication application;
	ImageButton ok;
	String newSkill;
	ServerLink serverLink; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.endbattle);
        
        title = (TextView) findViewById(R.id.title);
    	iv = (ImageView) findViewById(R.id.image);
    	exp = (TextView) findViewById(R.id.exp);
    	levelUp = (TextView) findViewById(R.id.level);
    	application = (RoleClientApplication) getApplication();    	    
    	serverLink = new ServerLink(this);
 
        Bundle extras = getIntent().getExtras();            
        state = extras.getInt("state",-1);
        experience = extras.getInt("experience");
        leveled = extras.getBoolean("leveled");
        newSkill =  extras.getString("new-skill");
        if (experience != -1) {
        	exp.setText("You receive " + Integer.toString(experience) + " experience.");
        }
        if (leveled) {
        	level = extras.getInt("level");
        	levelUp.setText("You have leveled up! You are now level " + Integer.toString(level) + "!");
        	if (newSkill != null) {
        		levelUp.setText(levelUp.getText() + "\n You have learned " + newSkill + "!");
        	}
        }    
        if (state == WIN) {
        	application.stopMusic();
        	application.startVictoryMusic();
        	title.setText("You have won the battle!!");
        	iv.setImageResource(R.drawable.win);
        } else if (state == LOSE) {
        	application.stopMusic();
        	application.startGameOverMusic();
        	title.setText("You have lost the battle.");
        	iv.setImageResource(R.drawable.lose);
        } else if (state == RUN) {
        	application.stopMusic();
        	application.startVictoryMusic();
        	title.setText("Your opponent ran away!");
        	iv.setImageResource(R.drawable.win);
        } else if (state == REJECT) {
        	application.stopMusic();
        	application.startGameOverMusic();
        	title.setText("Your opponent has rejected the battle.");
        	iv.setImageResource(R.drawable.lose);
        }
    	
        //handle button
        ok = (ImageButton) findViewById(R.id.ok);
        ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	if (pending) {
            		Toast.makeText(EndBattle.this,"Please wait for info to load.",Toast.LENGTH_SHORT).show();
            		return;
            	}
            	application.playButtonSound();
				application.stopMusic();
				application.startWorldMusic();
            	Intent svc = new Intent(((Activity) ServerLinkService.callBack), ServerLinkService.class);
				startService(svc);
            	Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    	
    	pending = true;
    	ok.setClickable(false);
    	ok.setEnabled(false);
    	//ok.setText("Please wait...");
    	serverLink.sendRequest(ServerLink.CHAR_INSPECT);
    }
    
	protected void onPause() {
		super.onPause();
		application.pauseMusic();
	}
	
    protected void onResume() {
    	super.onResume();
    	application.resumeMusic();
    }
    
    
    public void receiveData(Hashtable<String, Object> data, int requestType) {
    	if (requestType == ServerLink.CHAR_INSPECT) {
			Hashtable<String, Object> character = (Hashtable<String, Object>) data.get("character");
			if (Integer.parseInt((String) character.get("id")) == application.getPlayerId()) {
				application.setSkills((Object[]) ((Hashtable <String,Object>)character.get("skills")).get("skill"));
	    		
	    		application.setmaxhealth(Integer.parseInt((String) character.get("max-health")));
	    		application.setPlayerName((String) character.get("name"));
	        	pending = false;
	        	ok.setClickable(true);
	        	ok.setEnabled(true);
	        	//ok.setText("Finish");
			}
    	}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Toast.makeText(this,"Please select an option.",Toast.LENGTH_SHORT).show();
            return true;
        }	

        return super.onKeyDown(keyCode, event);
    }  
    
}