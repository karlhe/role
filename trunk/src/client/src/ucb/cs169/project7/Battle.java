package ucb.cs169.project7;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class Battle extends Activity implements ClientComponent, BattleAccelerometerListener {	
	
	private int opponentId;
	private int playerHealth, opponentHealth, opponentMaxHealth;
	private Timer timer;
	private TimerTask task;
	private TextView playerNameText, opponentNameText, playerHealthText, opponentHealthText, playerActionText, opponentActionText;
	private ProgressBar playerHealthBar, opponentHealthBar;
	private ServerLink serverLink;
	private boolean pending, linked, poll, ended;
	RoleClientApplication application;
	
	private Button button1, button2;
	
	private final String TAG = "Battle Screen";
	
	//Accelerometer variables **
	private float lastX, lastY, lastZ, storedX, storedY, storedZ;
	private ImageButton action;
	private Time storedTime;
	private static Context CONTEXT;
	// ** 
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	serverLink = new ServerLink(this);
    	timer = new Timer();
    	Log.v(TAG, "Battle screen start");
    	ended = false;

        setContentView(R.layout.battle);
        CONTEXT = this;
        
        playerNameText = (TextView) findViewById(R.id.playerNameText);
        opponentNameText = (TextView) findViewById(R.id.opponentNameText);
    	opponentHealthBar = (ProgressBar) findViewById(R.id.opponentHealthBar);
    	opponentHealthText = (TextView) findViewById(R.id.opponentHealthText);
    	opponentActionText = (TextView) findViewById(R.id.opponentActionText);
    	playerHealthBar = (ProgressBar) findViewById(R.id.playerHealthBar);
    	playerHealthText = (TextView) findViewById(R.id.playerHealthText);
    	playerActionText = (TextView) findViewById(R.id.playerActionText);
    	
    	application = (RoleClientApplication) getApplication();

    	Bundle extras = getIntent().getExtras();
    	
    	//Music
    	application.stopMusic();
    	application.startBattleMusic();
    	Log.v(TAG, "Music started");
    	
    	//Gestures
    	action = (ImageButton) findViewById(R.id.playerActionButton3);
        storedTime = new Time();
        action.setOnTouchListener(new OnTouchListener() {
        	public boolean onTouch(View v, MotionEvent m) {
        		application.playButtonSound();
        		if (m.getAction() == MotionEvent.ACTION_DOWN) {
        			turnAccelerometerOn();
        			storedTime.setToNow();
        			storedX = lastX;
            		storedY = lastY;
            		storedZ = lastZ;
            		//action.setTextColor(Color.RED);	            		
        		} else if (m.getAction() == MotionEvent.ACTION_UP) {
            		//button being released
        			turnAccelerometerOff();
        			Time now = new Time();
            		now.setToNow();
            		if (now.second - storedTime.second > 2) {
            			Toast.makeText(Battle.this,"Too slow!",Toast.LENGTH_SHORT).show();
            			application.playFailAttack();
            		} else if (lastX - storedX > 0 && lastX - storedX < 14 && lastY - storedY < 3 && lastY - storedY > -15 && lastZ - storedZ > -12 && lastZ - storedZ < 2) {
            	    	
            			Hashtable<String, Object> data = new Hashtable<String, Object>();
            	    	data.put("opponent", opponentId);
            	    	data.put("type", "fight");
            	    	RoleClientApplication application = (RoleClientApplication) getApplication();
            	    	data.put("ability", application.getSkill3());
            	    	serverLink.sendRequest(ServerLink.BATTLE_SEND, data);
            			
            	    	Toast.makeText(Battle.this,"Stabbed!!",Toast.LENGTH_SHORT).show();
            		} else if (lastX - storedX < 5 && lastX - storedX > -5 && storedY - lastY > 7 && lastZ - storedZ > 9) {
            			
            	    	Hashtable<String, Object> data = new Hashtable<String, Object>();
            	    	data.put("opponent", opponentId);
            	    	data.put("type", "fight");
            	    	RoleClientApplication application = (RoleClientApplication) getApplication();
            	    	data.put("ability", application.getSkill3());
            	    	serverLink.sendRequest(ServerLink.BATTLE_SEND, data);
            			
            			Toast.makeText(Battle.this,"Slapped down!!",Toast.LENGTH_SHORT).show();
            		} else if (lastX - storedX < 5 && lastX - storedX > -5 && lastY-storedY > 0 && lastY-storedY < 13 && lastZ - storedZ > -21 && lastZ - storedZ < -1 ) {
            			
            	    	Hashtable<String, Object> data = new Hashtable<String, Object>();
            	    	data.put("opponent", opponentId);
            	    	data.put("type", "fight");
            	    	RoleClientApplication application = (RoleClientApplication) getApplication();
            	    	data.put("ability", application.getSkill3());
            	    	serverLink.sendRequest(ServerLink.BATTLE_SEND, data);
            			
            			Toast.makeText(Battle.this,"Slapped up!!",Toast.LENGTH_SHORT).show();
            		} else {
            			Toast.makeText(Battle.this,"Attack move failed!",Toast.LENGTH_SHORT).show(); 
            			application.playFailAttack();
            		}
            		//action.setTextColor(Color.BLACK);
            		
        		}
        		return true;
        	}
        });
        Log.v(TAG, "Accelerometer started");        

    	button1 = (Button) findViewById(R.id.playerActionButton1);
    	button1.setText(application.getSkill1());
    	button2 = (Button) findViewById(R.id.playerActionButton2);
    	if (!application.getSkill2().equals("")) {
    		button2.setVisibility(View.VISIBLE);
    		button2.setText(application.getSkill2());
    	}
    	playerNameText.setText(application.getPlayerName());
    	opponentNameText.setText(extras.getString("opponentname"));
    	
    	playerHealth = application.getmaxhealth();
    	setPlayerHealth(playerHealth, application.getmaxhealth());        	    	
    	
    	opponentMaxHealth = extras.getInt("opponentmaxhealth");
    	opponentHealth = opponentMaxHealth;
    	setOpponentHealth(opponentHealth, opponentMaxHealth);
        
    	Log.v(TAG, "Preparing to connect to server");
    	
    	pending = true;
    	linked = false;
    	disableActions();
    	Toast.makeText(this, "Waiting for server response...", Toast.LENGTH_LONG).show();
    	Intent i = getIntent();
    	if (i.getAction().equals("role.intent.action.BATTLE.SEND_INVITE")) {
    		opponentId = Integer.parseInt(i.getData().getHost());
    		initiateBattle();
    	} else if (i.getAction().equals("role.intent.action.BATTLE.ACCEPT_INVITE")) {
    		opponentId = Integer.parseInt(i.getData().getHost());
    		acceptInitiateBattle();
    	}
    	

    }

    @Override
    public void onStop() {    	
    	timer.cancel();
    	timer.purge();
    	super.onStop();
    	
    }
    
    public void setPlayerHealth(int currentHealth, int maxHealth) {
    	/*
    	if (currentHealth <= 0) {
			Toast.makeText(this, "You lost the battle!", Toast.LENGTH_SHORT).show();
			timer.cancel();
	        setResult(RESULT_OK);
	        finish();
    	}*/
    	playerHealthText.setText(Integer.toString(currentHealth));
    	playerHealthBar.setProgress(100 * currentHealth / maxHealth);
    	
    }
    
    public void setOpponentHealth(int currentHealth, int maxHealth) {
    	/*
    	if (currentHealth <= 0) {
			Toast.makeText(this, "You won the battle!", Toast.LENGTH_SHORT).show();
			timer.cancel();
	        setResult(RESULT_OK);
	        finish();
    	}*/
    	opponentHealthText.setText(Integer.toString(currentHealth));
    	opponentHealthBar.setProgress(100 * currentHealth / maxHealth);
    }
    
    public int getOpponentCurrentHealth() {
    	return Integer.parseInt(opponentHealthText.getText().toString());
    }
    
    public static Context getContext() {
		return CONTEXT;
	}
    
    
    /**
     * onShake callback
     */
	/*
    public void onShake(float force) {
		Toast.makeText(this, "Phone shaked : " + force, 1000).show();
	}
    */
    
	/**
	 * onAccelerationChanged callback
	 */
	public void onAccelerationChanged(float x, float y, float z) {
		lastX = x;
		lastY = y;
		lastZ = z;		
	}
	
	protected void onPause() {
		super.onPause();
		if (!pending && !ended) {
			timer.cancel();
			timer.purge();
		}
		application.pauseMusic();
    	turnAccelerometerOff();
	}
	
    protected void onResume() {
    	super.onResume();
    	if (!pending) {
			timer = new Timer();
	    	task = new TimerTask() {
	    		public void run() {
	    			if (!poll) {
	    			Log.v(this.getClass().getSimpleName(),"Battle polling");
	    	    	serverLink.sendRequest(ServerLink.BATTLE_GET);
	    			}
	    		}
	    	};
	    	timer.schedule(task, 1000, 3000);
	    	Toast.makeText(this, "Linked to server...", Toast.LENGTH_LONG).show();
	    	linked = true;
	    	Log.v(TAG, "Battle poll started");
    	}
    	application.resumeMusic();
		//turnAccelerometerOn();  
    }
    
    protected void onDestroy() {
    	super.onDestroy();
    	turnAccelerometerOff();
    }
    
    public void turnAccelerometerOn() {
    	//Log.v("Battle Thread", Long.toString(currentThread().getId()));
    	if (BattleAccelerometerManager.isSupported() && !BattleAccelerometerManager.isListening()) {
    		BattleAccelerometerManager.startListening(this);
    	}
    }
    
    public void turnAccelerometerOff() {
    	cooldown();
    	if (BattleAccelerometerManager.isListening()) {
    		BattleAccelerometerManager.stopListening();
    	}  
    }
	
    public void resetAccelerationValues() {
    	lastX = 0;
    	lastY = 0;
    	lastZ = 0;
    	storedX = 0;
    	storedY = 0;
    	storedZ = 0;
    }
    
    public void onClickButton1(View v) {
    	application.playButtonSound();
    	cooldown();
    	
    	/* comment out for test 
    	task.cancel();
    	timer.purge();
    	*/
    	Hashtable<String, Object> data = new Hashtable<String, Object>();
    	data.put("opponent", opponentId);
    	data.put("type", "fight");
    	RoleClientApplication application = (RoleClientApplication) getApplication();
    	data.put("ability", application.getSkill1());
    	serverLink.sendRequest(ServerLink.BATTLE_SEND, data);
    	//receiveData(mockData(playerHealth, opponentHealth - new Double(Math.random() * 10).intValue() - 1), ServerLink.BATTLE_SEND);
    	/* comment out for test 
    	task = new TimerTask() {
    		public void run() {
    	    	// serverLink.sendRequest(ServerLink.BATTLE_GET);
    	    	receiveData(mockData(playerHealth - new Double(Math.random() * 10).intValue() - 1, opponentHealth), ServerLink.BATTLE_SEND);
    		}
    	};
    	timer.schedule(task, 1000, 1000);
    	*/
    }
    
    public void onClickButton2(View v) {
    	application.playButtonSound();
    	cooldown();
    	//task.cancel();
    	//timer.purge();
    	Hashtable<String, Object> data = new Hashtable<String, Object>();
    	data.put("opponent", opponentId);
    	data.put("type", "fight");
    	data.put("ability", application.getSkill2());
    	serverLink.sendRequest(ServerLink.BATTLE_SEND, data);
    	/*
    	task = new TimerTask() {
    		public void run() {
    	    	serverLink.sendRequest(ServerLink.BATTLE_GET);
    		}
    	};
    	timer.schedule(task, 5000, 5000);
    	*/
    }
/*    
    public void onClickButton3(View v) {
    	application.playButtonSound();
    	task.cancel();
    	//timer.purge();
    	Hashtable<String, Object> data = new Hashtable<String, Object>();
    	data.put("opponent", opponentId);
    	data.put("type", "fight");
    	data.put("ability", 3);
    	serverLink.sendRequest(ServerLink.BATTLE_SEND, data);
    }
*/
    public void onClickButton4(View v) {
    	application.playButtonSound();
    	RoleClientApplication app = (RoleClientApplication) getApplication();
		synchronized (app.getBattleLock()){
			app.setBattle(false);
			ended = true;
	    	timer.cancel();
	    	timer.purge();
			Toast.makeText(this, "You run away.", Toast.LENGTH_LONG).show();
	    	Hashtable<String, Object> data = new Hashtable<String, Object>();
	    	data.put("opponent", opponentId);
	    	data.put("type", "run");
	    	serverLink.sendServiceRequest(ServerLink.BATTLE_SEND, data);
	    	//Music
	    	RoleClientApplication application = (RoleClientApplication) getApplication();
			application.stopMusic();
			application.startWorldMusic();
	    	
			
	        setResult(RESULT_OK);
	        finish();
		}
    }
/*
    public void onClickUpdate(View v) {
    	RoleClientApplication application = (RoleClientApplication) getApplication();
    	serverLink.sendRequest(ServerLink.BATTLE_GET);
    }   
*/  
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Toast.makeText(this,"Please select an option.",Toast.LENGTH_SHORT).show();
            return true;
        }	

        return super.onKeyDown(keyCode, event);
    }
	
    public void cooldown() {
    	if (ended) {
    		return;
    	}
    	disableActions();
    	final Battle battle = this;
		timer.schedule(new TimerTask() {
    		public void run() {
    			((Activity) battle).runOnUiThread(
				          new Runnable() {
				              public void run() {
				    			Log.d(TAG,"Timed CD");
				    			enableActions();
				    			Log.d(TAG,"Actions enabled.");
				              }
				          }
			          );
    		}
    	}, 2000);
    }
    
    public void enableActions() {
    	button1.setEnabled(true);
    	button1.setClickable(true);
    	button2.setEnabled(true);
    	button2.setClickable(true);
    	action.setEnabled(true);
    	action.setClickable(true);
    	Log.d(TAG,"Enabled buttons");
    } 
    
    public void disableActions() {
    	button1.setEnabled(false);
    	button1.setClickable(false);
    	button2.setEnabled(false);
    	button2.setClickable(false);
    	action.setEnabled(false);
    	action.setClickable(false);
    	Log.d(TAG,"Disabled buttons");
    } 
    
    public void initiateBattle() {
    	Hashtable<String, Object> data = new Hashtable<String, Object>();
    	data.put("opponent", opponentId);
    	data.put("type", "initiate");
    	serverLink.sendRequest(ServerLink.BATTLE_SEND, data);
    	//receiveData(mockData(100, 100), ServerLink.BATTLE_SEND);
    }

    public void acceptInitiateBattle() {
    	Hashtable<String, Object> data = new Hashtable<String, Object>();
    	data.put("opponent", opponentId);
    	data.put("type", "accept");
    	serverLink.sendRequest(ServerLink.BATTLE_SEND, data);
    	//receiveData(mockData(100, 100), ServerLink.BATTLE_SEND);
    }
    
	public void receiveData(Hashtable<String, Object> data, int requestType) {
		RoleClientApplication app = (RoleClientApplication) getApplication();
		try {
			if (ended) {
				Log.v("Battle", "Battle ended already");
				return;
			}
			if (data == null) {
				//ERROR
				//Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
				return;
			}
			Hashtable<String, Object> battle;
			if (requestType == ServerLink.BATTLE_GET) {
				poll = false;
				//ServerLink.printData(data);
				battle = (Hashtable<String, Object>) data.get("battle");
				if (((String) battle.get("status")).equals("none")){
					Log.v(this.getClass().getSimpleName(),"Ignoring blank battle");
					return;
				}
				if (battle.containsKey("player")) {
					Hashtable<String, Object> player = (Hashtable<String, Object>) battle.get("player");
					playerHealth = Integer.parseInt((String) player.get("health"));
					RoleClientApplication application = (RoleClientApplication) getApplication();
					setPlayerHealth(playerHealth, application.getmaxhealth());
					if (player.containsKey("action")) {
						Hashtable<String, Object> paction = (Hashtable<String, Object>) player.get("action");
						playerActionText.setText("You attack with " + (String) paction.get("ability") + " for " + (String) paction.get("effect"));		
						application.playSwordEcho();
					}
					else {
						//playerActionText.setText("");
					}
				}
				if (battle.containsKey("opponent")) {
					Hashtable<String, Object> opponent = (Hashtable<String, Object>) battle.get("opponent");
					opponentHealth = Integer.parseInt((String) opponent.get("health"));
					setOpponentHealth(opponentHealth, opponentMaxHealth);
					if (opponent.containsKey("action")) {
						Hashtable<String, Object> oaction = (Hashtable<String, Object>) opponent.get("action");
						opponentActionText.setText("You are attacked with " + (String) oaction.get("ability") + " for " + (String) oaction.get("effect"));
						((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(300);
						application.playGrunt();
					}
					else {
						//opponentActionText.setText("");
					}
					if (Integer.parseInt((String) (opponent.get("id"))) != 0 && opponentId != Integer.parseInt((String) (opponent.get("id")))) {
						Toast.makeText(this, "Your are currently in another battle.", Toast.LENGTH_SHORT).show();
						endBattle(data, 0);
					}
				}				
				if (((String) battle.get("status")).equals("lost")){
					Toast.makeText(this, "You lost the battle!", Toast.LENGTH_SHORT).show();
					endBattle(battle, EndBattle.LOSE);
				} else if (((String) battle.get("status")).equals("won")){
					if (battle.containsKey("opponent") && Integer.parseInt((String) (((Hashtable<String, Object>) battle.get("opponent")).get("id"))) == 0){
						Toast.makeText(this, "Your opponent ran away!", Toast.LENGTH_SHORT).show();					
						endBattle(battle, EndBattle.RUN);
					} else {
						Toast.makeText(this, "You won the battle!", Toast.LENGTH_SHORT).show();
						endBattle(battle, EndBattle.WIN);
					}
				} else if(((String) battle.get("status")).equals("run")){
					Toast.makeText(this, "Your opponent ran away!", Toast.LENGTH_SHORT).show();					
					endBattle(battle, EndBattle.RUN);
				} else if(((String) battle.get("status")).equals("rejected")){
					Toast.makeText(this, "Your opponent rejected the battle!", Toast.LENGTH_SHORT).show();					
					endBattle(battle, EndBattle.REJECT);
				} else if (((String) battle.get("status")).equals("pending") && pending){
					if (Integer.parseInt((String) battle.get("initiator")) != app.getPlayerId()) {
						Toast.makeText(this, "Your opponent has engaged you!", Toast.LENGTH_SHORT).show();
						endBattle(battle, 0);
					} else {						
						Toast.makeText(this, "The opponent has not yet accepted the battle!", Toast.LENGTH_SHORT).show();
					}
				} else if (((String) battle.get("status")).equals("active") && pending){
					Toast.makeText(this, "The battle has begun!", Toast.LENGTH_LONG).show();
					pending = false;
					enableActions();
				}
				
				
			}
			if (requestType == ServerLink.BATTLE_SEND) {
				battle = (Hashtable<String, Object>) data.get("battle");
				if (((String) battle.get("status")).equals("error") 
						&& ((String) battle.get("message")).equals("Initiation has failed.  You are in battle.")) {
					Toast.makeText(this, "Your are currently in another battle.", Toast.LENGTH_LONG).show();
					endBattle(battle, 0);
				}
				else if (((String) battle.get("status")).equals("error") 
						&& ((String) battle.get("message")).equals("Initiation has failed. Opponent is in battle.")) {
					Toast.makeText(this, "Your opponent is currently in another battle.", Toast.LENGTH_LONG).show();
					endBattle(battle, 0);
				}
				
				if (!linked) {
					Log.v(TAG, "Received first response");
			    	task = new TimerTask() {
			    		public void run() {
			    			if (!poll) {
			    			Log.v(this.getClass().getSimpleName(),"Battle polling");
			    	    	serverLink.sendRequest(ServerLink.BATTLE_GET);
			    			}
			    		}
			    	};
			    	timer.schedule(task, 1000, 3000);
			    	Toast.makeText(this, "Linked to server...", Toast.LENGTH_LONG).show();
			    	linked = true;
			    	Log.v(TAG, "Battle poll started");
				}
		    	
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}		
    }

	public Hashtable<String, Object> mockData(int health1, int health2) {
		Hashtable<String, Object> data = new Hashtable<String, Object>();
    	Hashtable<String, Object> battle = new Hashtable<String, Object>();
    	data.put("battle", battle);
    	Hashtable<String, Object> player =  new Hashtable<String, Object>();
    	Hashtable<String, Object> opponent =  new Hashtable<String, Object>();
    	player.put("health", Integer.toString(health1));
    	opponent.put("health", Integer.toString(health2));
    	battle.put("player", player);
    	battle.put("opponent", opponent);
		return data;
	}
	
	public void startPoll() {
		//Ignore
	}
	
	private void endBattle(Hashtable<String, Object> battle, int state) {
		RoleClientApplication app = (RoleClientApplication) getApplication();
		synchronized (app.getBattleLock()){
			app.setBattle(false);
			ended = true;
			//Music
			app.stopMusic();
			app.startWorldMusic();
			
			timer.cancel();
			timer.purge();
			if (state ==0){
				Log.d(TAG,"Exiting due to special state");
				Intent svc = new Intent(((Activity) ServerLinkService.callBack), ServerLinkService.class);
				startService(svc);
		        setResult(RESULT_OK);
		        finish();
			} else {
				Log.d(TAG,"Exiting with state" + Integer.toString(state));
	            Intent myIntent = new Intent(this, EndBattle.class);
	            myIntent.putExtra("state", state);
	            if (battle.containsKey("experience")) {
	            	myIntent.putExtra("experience", Integer.parseInt((String) battle.get("experience")));
	            }
	            if (battle.containsKey("level-up")) {
	            	myIntent.putExtra("leveled", true );
		            myIntent.putExtra("level", Integer.parseInt((String) battle.get("level-up")));
		            if (battle.containsKey("new-skill")) {
		            	myIntent.putExtra("new-skill", (String) battle.get("new-skill"));
		            }
	            } else {
	            	myIntent.putExtra("leveled", false );
		            myIntent.putExtra("level", 0);
	            }	            
	            startActivity(myIntent);
		        setResult(RESULT_OK);
		        finish();
			}
		}

	}
}