package ucb.cs169.project7;


import java.util.Hashtable;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import android.media.MediaPlayer;


public class RoleClientApplication extends Application {
	private int playerId;
	private String playerName;
	private int maxhealth;
	private String skill1, skill2, skill3;
	private static ServerLinkService CONNECTION;
	private Player[] players;
	private MediaPlayer mp;
	private MediaPlayer buttonSound, swordecho, failattack, grunt;
	private boolean battle;
	private Object battleLock = new Object();
	
	public Object getBattleLock() {
		return battleLock;
	}
	
	public boolean getBattle() {
		Log.v("Role Client", "Get battle alert " + Boolean.toString(battle));
		return battle;
	}
	
	public void setBattle(boolean battle) {		
		Log.v("Role Client", "Set battle alert " + Boolean.toString(battle));
		this.battle = battle;		
	}
	
    @Override
    public void onCreate() {
        super.onCreate();
        playerId = 0;
        
        startLoginMusic();
        buttonSound = MediaPlayer.create(this, R.raw.buttonsound);
        swordecho = MediaPlayer.create(this,R.raw.swordecho);
        failattack = MediaPlayer.create(this,R.raw.failattack);
        grunt = MediaPlayer.create(this,R.raw.grunt);
    }
    
    public void playButtonSound() {
    	//TODO: add prepare??
    	buttonSound.start();
    }
    
    public void playSwordEcho() {
    	swordecho.start();
    }
    
    public void playFailAttack() {
    	failattack.start();
    }
    
    public void playGrunt() {
    	grunt.start();
    }
      
    public void startAlertMusic() {
        mp = MediaPlayer.create(this, R.raw.ff4alert);
        mp.start();
        mp.setLooping(true);
    }
    
    public void startVictoryMusic() {
        mp = MediaPlayer.create(this, R.raw.ff6victo);
        mp.start();
        mp.setLooping(true);
    }
    
    public void startGameOverMusic() {
        mp = MediaPlayer.create(this, R.raw.ff8gameover);
        mp.start();
        mp.setLooping(true);
    }
    
    public void startLoginMusic() {
        mp = MediaPlayer.create(this, R.raw.ff7login);
        mp.start();
        mp.setLooping(true);
    }
    
    public void startWorldMusic() {
        mp = MediaPlayer.create(this, R.raw.ff4world);
        mp.start();
        mp.setLooping(true);
    }
    
    public void startBattleMusic() {
        mp = MediaPlayer.create(this, R.raw.ff7battle);
		mp.start();
        mp.setLooping(true);
    }
    
    public void stopMusic() {
        // DEALLOCATE ALL MEMORY
        if (mp != null) {
            if (mp.isPlaying()) {
            	mp.stop();
            }
            mp.setLooping(false);
            mp.release();
            mp = null;
        }
    }
    
    public void pauseMusic() {
    	if (mp != null) {
    		if (mp.isPlaying()) {
    			mp.pause();
    		}
    	}
    }
    
    public void resumeMusic() {
    	if (mp != null) {
    		if (!mp.isPlaying()) {
    			mp.start();
    		}	
    	}
    }
    
    public int getPlayerId() {
    	return playerId;
    }
    
    public String getPlayerName() {
    	return playerName;
    }
    
    public int getmaxhealth() {
    	return maxhealth;
    }
    
    
    public String getSkill1() {
    	return skill1 != null ? skill1 : "";
    }
        
    public String getSkill2() {
    	return skill2 != null ? skill2 : "";
    }
    
    public String getSkill3() {
    	return skill3 != null ? skill3 : "";
    }

    public ServerLinkService getServerLink() {
    	return CONNECTION;
    }
    
    public String getSavedName() {
		SharedPreferences prefs = getSharedPreferences("RoleClientApplication", Context.MODE_PRIVATE);
		return prefs.getString("name", null);
    }
    
    public String getSavedPassword() {
		SharedPreferences prefs = getSharedPreferences("RoleClientApplication", Context.MODE_PRIVATE);
		return prefs.getString("password", null);
    }

    public void saveLogin(String name, String password) {
		// TODO Auto-generated method stub
		SharedPreferences prefs = getSharedPreferences("RoleClientApplication", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("name", name);
		editor.putString("password", password);
		editor.commit();
	}
    
    public void clearLogin() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = getSharedPreferences("RoleClientApplication", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("name", null);
		editor.putString("password", null);
		editor.commit();
	}

	public boolean hasSavedLogin() {
		SharedPreferences prefs = getSharedPreferences("RoleClientApplication", Context.MODE_PRIVATE);
		String name = prefs.getString("name", null);
		String password = prefs.getString("password", null);
		return (name != null && password != null);
	}
	
	
	public void setPlayerId(int id) {
		playerId = id;
	}
	
	public void setPlayerName(String name) {
		playerName = name;
	}
	
    public void setmaxhealth(int m) {
    	maxhealth = m;
    }
	
    public void setSkills(Object[] skills) {
        for (int i = 0; i < skills.length; i++) {
        	Hashtable <String, Object> skill = (Hashtable <String, Object>) skills[i];
        	Log.v("hash", skill.toString());
        	String skillType = ((skill.containsKey("skill-type")) ? (String) skill.get("skill-type") : null);
        	if (skillType == null) {
        		return;
        	} else if (skillType.equals("primary")) {
        		skill1 = skill.containsKey("skill-name") ? (String) skill.get("skill-name") : null;
        	} else if (skillType.equals("secondary")) {
        		skill2 = skill.containsKey("skill-name") ? (String) skill.get("skill-name") : null;
        	} else if (skillType.equals("accelerometer")) {
        		skill3 = skill.containsKey("skill-name") ? (String) skill.get("skill-name") : null;
        	}
        }
    }   
    
    public void setServerLink(ServerLinkService s) {
    	CONNECTION = s;
    }
    
	public void updateData(Hashtable<String, Object> data) {
		Hashtable<String, Object> updates = (Hashtable<String, Object> ) data.get("updates");				
		Object locations[] = (Object[]) ((Hashtable<String, Object> ) updates.get("locations")).get("character");
		players = new Player[locations.length];
		for (int i = 0; i < locations.length; i++) {
			players[i] = new Player();
			players[i].setId(Integer.parseInt((String) ((Hashtable<String, Object>) locations[i]).get("id")));
			players[i].setName((String)((Hashtable<String, Object>) locations[i]).get("name"));			
		}		
		if (updates.containsKey("battle")) {
			//timer.cancel();
			int opponentId = Integer.parseInt((String) ((Hashtable<String, Object>) updates.get("battle")).get("initiator"));
	        Intent myIntent = new Intent("role.intent.action.BATTLE_ALERT.RECEIVE_INVITE", Uri.parse("id://" + opponentId), this, BattleAlert.class);
	        startActivity(myIntent);        
		}
	}
}