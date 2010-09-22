package ucb.cs169.project7;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class RoleClient extends MapActivity implements ClientComponent {
	private RoleClientApplication application;
	private boolean isRequestingLogin, isLoggedIn, isRequestingRegister, isViewingSelectedPlayer;
	private String charSelectedName;
	private ServerLink serverLink;
	private Drawable PlayerDrawable;
    private MapView mapView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private PlayersOverlay players;
    private ArrayList<Player> playersList;
    private int selectedPlayerId;
	private String selectedPlayerName;
	private int selectedPlayerMaxHealth;
	private	Hashtable<String,Object> class1, class2, class3;

    protected double latitude, longitude;
	protected Object[] charClasses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.worldmap);
		application = (RoleClientApplication) getApplication();
    	serverLink = new ServerLink(this);
    	mapView = (MapView) findViewById(R.id.worldMapMapView);

    	PlayerDrawable = getResources().getDrawable(R.drawable.emo_im_wtf);
    	PlayerDrawable.setBounds(0, 0, PlayerDrawable.getIntrinsicWidth(), PlayerDrawable.getIntrinsicHeight());
		playersList = new ArrayList<Player>();

		PlayersOverlay.setActivity(this);

		mapView.setBuiltInZoomControls(true);

        MyLocationOverlay player = new MyLocationOverlay(this, mapView);
        player.enableMyLocation();
        mapView.getOverlays().add(player);

        final MapController mapController = mapView.getController();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria providerCriteria = new Criteria();
        providerCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
            	latitude = location.getLatitude();
            	longitude = location.getLongitude();
                mapController.animateTo(new GeoPoint((int) (latitude * 1000000), (int) (longitude * 1000000)));
                if (isLoggedIn) {
                	Hashtable<String, Object> data = new Hashtable<String, Object>();
                	data.put("latitude", latitude);
                	data.put("longitude", longitude);
                	Log.d("Location manager","Sending location update to server");
      		  		if (application.getPlayerId() != 0) {
      		  			serverLink.sendServiceRequest(ServerLink.LOC_SEND, data);
      		  		}
                }
            }

            public void onProviderDisabled(String provider) {
                Toast.makeText(RoleClient.this, "GPS location updates have been disabled.", Toast.LENGTH_SHORT).show();
            }

            public void onProviderEnabled(String provider) {
                Toast.makeText(RoleClient.this, "GPS location updates have been enabled.", Toast.LENGTH_SHORT).show();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                case LocationProvider.AVAILABLE:
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    break;
                default:
                    break;
                }
            }
        };

        locationManager.requestLocationUpdates(locationManager.getBestProvider(providerCriteria, true), 60000, 15, locationListener);
        		
        
        mapController.animateTo(new GeoPoint(37875581, -122258922));
        mapController.setZoom(21);

        if (application.getPlayerId() == 0) {
        	isLoggedIn = false;
            findViewById(R.id.worldMap).setVisibility(View.INVISIBLE);
            View view = View.inflate(this, R.layout.login, null);
            addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
            if (application.hasSavedLogin()) {
            	setProgressBarIndeterminateVisibility(true);
            	Button button = (Button) findViewById(R.id.loginCreateAccountButton);
               	button.setEnabled(false);
            	button = (Button) findViewById(R.id.loginUseAccountButton);
               	button.setEnabled(false);
            	button = (Button) findViewById(R.id.loginHelp);
               	button.setEnabled(false);

               	isRequestingLogin = true;
               	Hashtable<String, Object> data = new Hashtable<String, Object>();
            	data.put("name", application.getSavedName());
            	data.put("password", application.getSavedPassword());
            	serverLink.sendRequest(ServerLink.CHAR_LOGIN, data);
            	Toast.makeText(RoleClient.this,"Auto-logging in...",Toast.LENGTH_SHORT).show();
            }
        } else {
        	isLoggedIn = true;
            ServerLinkService.callBack = this;
            Intent svc = new Intent(this, ServerLinkService.class);
            startService(svc);
        }        
    }
    
    @Override
	public void onPause() {
    	super.onPause();
    	application.pauseMusic();
    	locationManager.removeUpdates(locationListener);
    	if (isLoggedIn) {
    		Intent svc = new Intent(this, ServerLinkService.class);
    		stopService(svc);
    	}

    }

    @Override
	public void onResume() {
    	super.onResume();
    	application.resumeMusic();
        Criteria providerCriteria = new Criteria();
        providerCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationManager.requestLocationUpdates(locationManager.getBestProvider(providerCriteria, true), 60000, 15, locationListener);
    	ServerLinkService.callBack = this;
    	if (isLoggedIn) {
    		ServerLinkService.callBack = this;
    		Intent svc = new Intent(this, ServerLinkService.class);
    		startService(svc);
    	}
    }

    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK)
    		//TODO: Override this default behavior
    		finish();
    	return true;
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    public void playButtonSound() {
    	application.playButtonSound();
    }
/*    
    public void clickWorldMapButton(View v) {
    	playButtonSound();
	   	startActivityForResult(new Intent(v.getContext(), WorldMap.class), 0);
    }
*/
    public void clickWorldMapListPlayersButton(View v) {
	    playButtonSound();
        findViewById(R.id.worldMap).setVisibility(View.INVISIBLE);
        View view = View.inflate(this, R.layout.listplayersview, null);
        addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
        ListView list = (ListView) findViewById(R.id.listPlayersList);
        final ArrayAdapter<Player> adapter =new ArrayAdapter<Player>(this, R.layout.listplayers, playersList); 
        list.setAdapter(adapter);
		list.setOnItemClickListener( new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				viewSelectedPlayer(adapter.getItem(position).getId());
			}
		});
    }

    public void clickWorldMapMyStatusButton(View v) {
    	playButtonSound();
        findViewById(R.id.worldMap).setVisibility(View.INVISIBLE);
        View view = View.inflate(this, R.layout.charstatus, null);
        addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
        int charId = application.getPlayerId();
		((ImageButton) findViewById(R.id.charStatusSkillsButton)).setEnabled(false);
    	setProgressBarIndeterminateVisibility(true);
        serverLink.sendRequest(ServerLink.CHAR_INSPECT, charId);
    }
    
    public void clickWorldMapTesterButton(View v) {;
    	playButtonSound();
    	Intent myIntent = new Intent(v.getContext(), Accelerometer.class);
    	startActivityForResult(myIntent, 0);
    }
        
    public void clickWorldMapLogoutButton(View v) {
    	playButtonSound();
       	isLoggedIn = false;
       	application.setPlayerId(0);
       	application.clearLogin();
       	Intent svc = new Intent(getBaseContext(), ServerLinkService.class);
   	    stopService(svc);
   		Toast.makeText(RoleClient.this,"You have logged out.",Toast.LENGTH_SHORT).show();
        findViewById(R.id.worldMap).setVisibility(View.INVISIBLE);
        View view = View.inflate(this, R.layout.login, null);
        addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
        application.stopMusic();
        application.startLoginMusic();
    }

    public void clickCreateAccountButton(View v) {
    	playButtonSound();
    	if (checkInternet()) {
            findViewById(R.id.login).setVisibility(View.INVISIBLE);
            View view = View.inflate(this, R.layout.createaccount, null);
            addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
            final EditText name = (EditText) findViewById(R.id.createAccountNameText);    
            final EditText password = (EditText) findViewById(R.id.createAccountPasswordText);
    		final Button save = (Button) findViewById(R.id.createAccountSaveButton);
    		final TextView feedback = (TextView) findViewById(R.id.createAccountFeedback);
    		feedback.setTextColor(Color.RED);
            TextWatcher watcher = new TextWatcher() {
    			public void afterTextChanged(Editable s) {
    				if (name.getText().toString().length() > 0 && password.getText().toString().length() > 0 && !isRequestingRegister && charSelectedName != null) {
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
    	else {
    		Toast.makeText(this, "You are not connected to the internet.", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void clickHelp(View v) {
    	playButtonSound();
        findViewById(R.id.login).setVisibility(View.INVISIBLE);
        View view = View.inflate(this, R.layout.tutorial, null);
        addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
    }
    
    public void clickTutorialBackButton(View v) {
    	playButtonSound();
        findViewById(R.id.login).setVisibility(View.VISIBLE);
    	View view = findViewById(R.id.tutorial);
    	((ViewGroup) view.getParent()).removeView(view);
    }
    
    public void clickUseAccountButton(View v) {
    	playButtonSound();
    	if (checkInternet()) {
            findViewById(R.id.login).setVisibility(View.INVISIBLE);
            View view = View.inflate(this, R.layout.useaccount, null);
            addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
            final EditText name = (EditText) findViewById(R.id.useAccountNameText);    
            final EditText password = (EditText) findViewById(R.id.useAccountPasswordText);
    		final Button save = (Button) findViewById(R.id.useAccountSaveButton);
    		final TextView feedback = (TextView) findViewById(R.id.useAccountFeedback);
    		feedback.setTextColor(Color.RED);
            TextWatcher watcher = new TextWatcher() {
    			public void afterTextChanged(Editable s) {
    				if (name.getText().toString().length() > 0 && password.getText().toString().length() > 0 && !isRequestingLogin) {
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
    	else {
    		Toast.makeText(this, "You are not connected to the internet.", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void clickCreateAccountSelectCharButton(View v) {
    	playButtonSound();
        findViewById(R.id.createAccount).setVisibility(View.INVISIBLE);
        View view = View.inflate(this, R.layout.characterselection, null);
        addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
    	setProgressBarIndeterminateVisibility(true);
    	((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        serverLink.sendServiceRequest(ServerLink.CHAR_CLASSES);
    }

    public void clickCreateAccountSaveButton(View v) {
    	playButtonSound();
    	setProgressBarIndeterminateVisibility(true);
    	((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        final EditText name = (EditText) findViewById(R.id.createAccountNameText);    
        final EditText password = (EditText) findViewById(R.id.createAccountPasswordText);
		final Button save = (Button) findViewById(R.id.createAccountSaveButton);
		final TextView feedback = (TextView) findViewById(R.id.createAccountFeedback);

		feedback.setText(null);
       	save.setEnabled(false);

       	isRequestingRegister = true;

    	Hashtable<String, Object> data = new Hashtable<String, Object>();
    	data.put("name", name.getText().toString());
    	data.put("password", password.getText().toString());
    	data.put("class", charSelectedName);
    	serverLink.sendRequest(ServerLink.CHAR_CREATE, data);
    }

    public void clickCreateAccountCancelButton(View v) {
    	playButtonSound();
    	((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        findViewById(R.id.login).setVisibility(View.VISIBLE);
    	View view = findViewById(R.id.createAccount);
    	((ViewGroup) view.getParent()).removeView(view);
    }

    public void clickUseAccountSaveButton(View v) {
    	playButtonSound();
    	
    	setProgressBarIndeterminateVisibility(true);
    	((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        final EditText name = (EditText) findViewById(R.id.useAccountNameText);    
        final EditText password = (EditText) findViewById(R.id.useAccountPasswordText);
		final Button save = (Button) findViewById(R.id.useAccountSaveButton);
		final TextView feedback = (TextView) findViewById(R.id.useAccountFeedback);

		feedback.setText(null);
       	save.setEnabled(false);

       	isRequestingLogin = true;
       	Hashtable<String, Object> data = new Hashtable<String, Object>();
    	data.put("name", name.getText().toString());
    	data.put("password", password.getText().toString());
    	serverLink.sendRequest(ServerLink.CHAR_LOGIN, data);
    }

    public void clickUseAccountCancelButton(View v) {
    	playButtonSound();
    	charSelectedName = null;
    	((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        findViewById(R.id.login).setVisibility(View.VISIBLE);
    	View view = findViewById(R.id.useAccount);
    	((ViewGroup) view.getParent()).removeView(view);
    }

    public void clickCharSelectionCharButton1(View v) {
    	application.playButtonSound();
		Hashtable<String, Object>class1 = (Hashtable<String,Object>)charClasses[0];
		TextView description = (TextView) findViewById(R.id.charSelectionDescription);
    	description.setText(getCharInfo(class1));
    	charSelectedName = (String) class1.get("name");
    	setCharImage(charSelectedName);
    	((Button) findViewById(R.id.charSelectionConfirmButton)).setEnabled(true);
    }

    public void clickCharSelectionCharButton2(View v) {
    	application.playButtonSound();
		Hashtable<String, Object>class2 = (Hashtable<String,Object>)charClasses[1];
		TextView description = (TextView) findViewById(R.id.charSelectionDescription);
    	description.setText(getCharInfo(class2));
    	charSelectedName = (String) class2.get("name");
    	setCharImage(charSelectedName);
    	((Button) findViewById(R.id.charSelectionConfirmButton)).setEnabled(true);
    }

    public void clickCharSelectionCharButton3(View v) {
    	application.playButtonSound();
		Hashtable<String, Object>class3 = (Hashtable<String,Object>)charClasses[2];
		TextView description = (TextView) findViewById(R.id.charSelectionDescription);
    	description.setText(getCharInfo(class3));
    	charSelectedName = (String) class3.get("name");
    	setCharImage(charSelectedName);
    	((Button) findViewById(R.id.charSelectionConfirmButton)).setEnabled(true);
    }

    public void clickCharSelectionConfirmButton(View v) {
    	playButtonSound();
    	View view = findViewById(R.id.charSelection);
    	((ViewGroup) view.getParent()).removeView(view);
        findViewById(R.id.createAccount).setVisibility(View.VISIBLE);
        final EditText name = (EditText) findViewById(R.id.createAccountNameText);    
        final EditText password = (EditText) findViewById(R.id.createAccountPasswordText);
		final Button save = (Button) findViewById(R.id.createAccountSaveButton);
		if (name.getText().toString().length() > 0 && password.getText().toString().length() > 0 && !isRequestingRegister && charSelectedName != null)
			save.setEnabled(true);
    }
        
    public void clickCharSelectionCancelButton(View v) {
    	playButtonSound();
    	charSelectedName = null;
    	View view = findViewById(R.id.charSelection);
    	((ViewGroup) view.getParent()).removeView(view);
        findViewById(R.id.createAccount).setVisibility(View.VISIBLE);
		final Button save = (Button) findViewById(R.id.createAccountSaveButton);
		save.setEnabled(false);
    }
    
    public void clickListPlayersBackButton(View v) {
    	playButtonSound();
    	View view = findViewById(R.id.listPlayers);
    	((ViewGroup) view.getParent()).removeView(view);
        findViewById(R.id.worldMap).setVisibility(View.VISIBLE);
    	
    }
    
    public void clickSelectedPlayerFightButton(View v) {
    	playButtonSound();
    	isViewingSelectedPlayer = false;
    	View view = findViewById(R.id.selectedPlayer);
    	((ViewGroup) view.getParent()).removeView(view);
        view = findViewById(R.id.listPlayers);
        if (view != null) {
        	view.setVisibility(View.VISIBLE);
        	((ViewGroup) view.getParent()).removeView(view);
        }
		mapView.setBuiltInZoomControls(true);
        findViewById(R.id.worldMap).setVisibility(View.VISIBLE);
    	synchronized (application.getBattleLock()) {
    		application.setBattle(true);
//    		Intent svc = new Intent(v.getContext(), ServerLinkService.class);
//    		stopService(svc);
            Intent myIntent = new Intent("role.intent.action.BATTLE.SEND_INVITE", Uri.parse("id://" + selectedPlayerId), v.getContext(), Battle.class);
            myIntent.putExtra("opponentmaxhealth", selectedPlayerMaxHealth);
            myIntent.putExtra("opponentname", selectedPlayerName);
            startActivityForResult(myIntent, 0);
    	}
    }

    public void clickSelectedPlayerBackButton(View v) {
    	playButtonSound();
    	isViewingSelectedPlayer = false;
    	View view = findViewById(R.id.selectedPlayer);
    	((ViewGroup) view.getParent()).removeView(view);
        view = findViewById(R.id.listPlayers);
        if (view == null)
        	view = findViewById(R.id.worldMap);
        view.setVisibility(View.VISIBLE);
    }
    
 
    
    public void clickCharStatusSkillsButton(View v) {
    	playButtonSound();
        findViewById(R.id.charStatus).setVisibility(View.INVISIBLE);
        View view = View.inflate(this, R.layout.skillslist, null);
        addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
    	setProgressBarIndeterminateVisibility(true);
        serverLink.sendServiceRequest(ServerLink.CHAR_CLASSES);
        String skill1, skill2, skill3;
        skill1 = application.getSkill1();
        skill2 = application.getSkill2();
        skill3 = application.getSkill3();
        TextView text;
        text = (TextView) findViewById(R.id.skillsListDescription1);
        text.setText("Skill Name: " + skill1);
        if (skill2 != null) {
        	text = (TextView) findViewById(R.id.skillsListDescription2);
        	text.setText("Skill Name: " + skill2);
        }
        text = (TextView) findViewById(R.id.skillsListDescription3);
        text.setText("Skill Name: " + skill3);
    }

    public void clickCharStatusBackButton(View v) {
    	playButtonSound();
    	View view = findViewById(R.id.charStatus);
    	((ViewGroup) view.getParent()).removeView(view);
        findViewById(R.id.worldMap).setVisibility(View.VISIBLE);
    }

    public void clickSkillsListBackButton(View v) {
    	playButtonSound();
    	View view = findViewById(R.id.skillsList);
    	((ViewGroup) view.getParent()).removeView(view);
        findViewById(R.id.charStatus).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	//application.stopMusic();
    	//application.startWorldMusic();
    	if (data != null) {
        	Bundle extras = data.getExtras();
        	if (extras != null) {
        		if (extras.containsKey("isLoggedIn")) {
        			isLoggedIn = true;
        			  try {
        				    {
//        				      ServerLinkService.callBack = this;
//        				      Intent svc = new Intent(this, ServerLinkService.class);
//        				      startService(svc);
        				    }
        				  }
        				  catch (Exception e) {
        				    Log.e("ROLECLIENT", "Service problem", e);
        				  }			        
        		}              	
        	}
    	}
    	if (isLoggedIn) {
  		  try {
			    {
//			      ServerLinkService.callBack = this;
//			      Intent svc = new Intent(this, ServerLinkService.class);
//			      startService(svc);
			    }
			  }
			  catch (Exception e) {
			    Log.e(this.getLocalClassName(), "Server started already", e);
			  }	
    		ServerLinkService.callBack = this;
    	}
    }
    
	public void receiveData(Hashtable<String, Object> data, int requestType) {
		if (data == null) {
			Log.d("RoleClient","Received empty data. Request Type " + Integer.toString(requestType));
			return;
		}
		try {
	    	if (requestType == ServerLink.CHAR_CREATE) {
	            final EditText name = (EditText) findViewById(R.id.createAccountNameText);    
	            final EditText password = (EditText) findViewById(R.id.createAccountPasswordText);
	    		final Button save = (Button) findViewById(R.id.createAccountSaveButton);
	    		final TextView feedback = (TextView) findViewById(R.id.createAccountFeedback);
				isRequestingRegister = false;
		    	setProgressBarIndeterminateVisibility(false);
				if (name.getText().toString().length() > 0 && password.getText().toString().length() > 0 && !isRequestingRegister && charSelectedName != null)
					save.setEnabled(true);
	    		Hashtable<String, Object> character = (Hashtable<String, Object>) data.get("character");
	    		String status = (String) character.get("status");
	    		if (status.equals("success")) {
	    			application.saveLogin(name.getText().toString(), password.getText().toString());
	    			application.setPlayerId(Integer.parseInt((String) character.get("id")));
	    			Toast.makeText(this, (String) character.get("message"), Toast.LENGTH_SHORT).show(); 
	            	isLoggedIn = true;
	                findViewById(R.id.worldMap).setVisibility(View.VISIBLE);
	            	View view = findViewById(R.id.createAccount);
	            	((ViewGroup) view.getParent()).removeView(view);
	            	view = findViewById(R.id.login);
	            	((ViewGroup) view.getParent()).removeView(view);
	            	application.stopMusic();
	            	application.startWorldMusic();
	                ServerLinkService.callBack = this;
	                Intent svc = new Intent(this, ServerLinkService.class);
	                startService(svc);
	    			int charId = application.getPlayerId();
	    			serverLink.sendServiceRequest(ServerLink.CHAR_INSPECT, charId);    	        
	    		} else if (status.equals("error")) {
					String message = (String) character.get("message");
					Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
					feedback.setText(message);
	    		}
	    	} else if (requestType == ServerLink.CHAR_LOGIN) {
               	isRequestingLogin = false;
            	setProgressBarIndeterminateVisibility(false);
            	Hashtable<String, Object> character = (Hashtable<String, Object>) data.get("character-session");
            	String status = (String) character.get("status");
	            if (findViewById(R.id.useAccount) == null) {
	            	Button button = (Button) findViewById(R.id.loginCreateAccountButton);
	               	button.setEnabled(true);
	            	button = (Button) findViewById(R.id.loginUseAccountButton);
	               	button.setEnabled(true);
	            	button = (Button) findViewById(R.id.loginHelp);
	               	button.setEnabled(true);
	            } else {
	            	final EditText name = (EditText) findViewById(R.id.useAccountNameText);    
	            	final EditText password = (EditText) findViewById(R.id.useAccountPasswordText);
	            	final Button save = (Button) findViewById(R.id.useAccountSaveButton);
	            	final TextView feedback = (TextView) findViewById(R.id.useAccountFeedback);
	            	if (name.getText().toString().length() > 0 && password.getText().toString().length() > 0)
	            		save.setEnabled(true);
	            	if (status.equals("success")) {
	            		application.saveLogin(name.getText().toString(), password.getText().toString());
	            	} else if (status.equals("error")) {
	            		String message = (String) character.get("message");
	            		feedback.setText(message);
	            	}
	            }
	            if (status.equals("success")) {
            		application.setPlayerId(Integer.parseInt((String) character.get("id")));
            		Toast.makeText(this, (String) character.get("message"), Toast.LENGTH_SHORT).show();
            		isLoggedIn = true;
            		findViewById(R.id.worldMap).setVisibility(View.VISIBLE);
            		View view = findViewById(R.id.useAccount);
            		if (view != null)
            			((ViewGroup) view.getParent()).removeView(view);
            		view = findViewById(R.id.login);
            		((ViewGroup) view.getParent()).removeView(view);
	            	application.stopMusic();
	            	application.startWorldMusic();
            		ServerLinkService.callBack = this;
            		Intent svc = new Intent(this, ServerLinkService.class);
            		startService(svc);
            		int charId = application.getPlayerId();
            		serverLink.sendServiceRequest(ServerLink.CHAR_INSPECT, charId);
	            } else if (status.equals("error")) {
            		String message = (String) character.get("message");
            		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();	            	
	            }
			} else if (requestType == ServerLink.CHAR_INSPECT) {
	    		Hashtable<String, Object> character = (Hashtable<String, Object>) data.get("character");
				if (findViewById(R.id.charStatus) != null || isViewingSelectedPlayer) {
			    	setProgressBarIndeterminateVisibility(false);
			    	String name = (String) character.get("name");
			    	String level = (String) character.get("level");
			    	String charClass = (String) character.get("class");
			    	String health = (String) character.get("health");
			    	String maxhealth = (String) character.get("max-health");
			    	String info = name 
			    				+ "\tLevel: " + level
			    				+ "\tClass: " + charClass
			    				+ "\tHealth: " + health + "/" + maxhealth;
			    	TextView description;
			    	ImageView iv;
			    	ImageButton action;
			    	if (findViewById(R.id.charStatus) != null ) {
			    		description = (TextView) findViewById(R.id.charStatusDescription);
			    		iv = (ImageView) findViewById(R.id.charStatusImage);
			    		action = (ImageButton) findViewById(R.id.charStatusSkillsButton);
			    		String exp = (String) character.get("experience");
			    		String tnl = (String) character.get("tnl");
			    		info = info + "\tExperience: " + exp +"/" + tnl;
			    	} else {
			    		selectedPlayerName = name;
			    		selectedPlayerMaxHealth = Integer.parseInt(maxhealth);
			    		description = (TextView) findViewById(R.id.selectedPlayerDescription);
			    		iv = (ImageView) findViewById(R.id.selectedPlayerImage);
			    		action = (ImageButton) findViewById(R.id.selectedPlayerFightButton);
			    	}
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
					action.setEnabled(true);
				}
				if (Integer.parseInt((String) character.get("id")) == application.getPlayerId()) {
					
					application.setSkills((Object[]) ((Hashtable <String,Object>)character.get("skills")).get("skill"));
		    		application.setmaxhealth(Integer.parseInt((String) character.get("max-health")));
		    		application.setPlayerName((String) character.get("name"));
				}
			} else if (requestType == ServerLink.CHAR_CLASSES) {
		    	setProgressBarIndeterminateVisibility(false);
		    	if (findViewById(R.id.charSelection) != null) {
		    		((TextView) findViewById(R.id.charSelectionTextLabel)).setText("Select your class");
		    		((View) findViewById(R.id.charSelectionButtons)).setVisibility(View.VISIBLE);
		    		Hashtable<String,Object> test = (Hashtable<String, Object>) data.get("character-classes");
		    		charClasses = (Object[]) test.get("character-class");
		    		class1 = (Hashtable<String,Object>)charClasses[0];
		    		class2 = (Hashtable<String,Object>)charClasses[1];
		    		class3 = (Hashtable<String,Object>)charClasses[2];
		    		((Button) findViewById(R.id.charSelectionCharButton1)).setText((String) class1.get("name"));
		    		((Button) findViewById(R.id.charSelectionCharButton2)).setText((String) class2.get("name"));
		    		((Button) findViewById(R.id.charSelectionCharButton3)).setText((String) class3.get("name"));
		    	} else if (findViewById(R.id.skillsList) != null) {
		            String skill1, skill2, skill3;
		            skill1 = application.getSkill1();
		            skill2 = application.getSkill2();
		            skill3 = application.getSkill3();
		    		Hashtable<String,Object> test = (Hashtable<String, Object>) data.get("character-classes");
		    		charClasses = (Object[]) test.get("character-class");
		    		class1 = (Hashtable<String,Object>)charClasses[0];
		    		class2 = (Hashtable<String,Object>)charClasses[1];
		    		class3 = (Hashtable<String,Object>)charClasses[2];
		            Object[] skills;
		            skills = (Object[]) ((Hashtable<String, Object>) class1.get("skills")).get("skill");
		            for (int i = 0; i < skills.length; i++) {
		            	Hashtable<String, Object> skill = (Hashtable<String, Object>)skills[i];
		            	String name = (String) skill.get("skill-name");
		            	TextView formula;
		            	Log.v("Stuff",name);
		            	if (name.equals(skill1)) {
		                    formula = (TextView) findViewById(R.id.skillsListFormula1);
		                    formula.setText("Formula: " +(String) skill.get("formula"));
		            	} else if (name.equals(skill2)) {
		                    formula = (TextView) findViewById(R.id.skillsListFormula2);
		                    formula.setText("Formula: " +(String) skill.get("formula"));
		            	} else if (name.equals(skill3)) {
		                    formula = (TextView) findViewById(R.id.skillsListFormula3);
		                    formula.setText("Formula: " +(String) skill.get("formula"));
		            	}
		            }
		            skills = (Object[]) ((Hashtable<String, Object>) class2.get("skills")).get("skill");
		            for (int i = 0; i < skills.length; i++) {
		            	Hashtable<String, Object> skill = (Hashtable<String, Object>)skills[i];
		            	String name = (String) skill.get("skill-name");
		            	TextView formula;
		            	if (name.equals(skill1)) {
		                    formula = (TextView) findViewById(R.id.skillsListFormula1);
		                    formula.setText("Formula: " +(String) skill.get("formula"));
		            	} else if (name.equals(skill2)) {
		                    formula = (TextView) findViewById(R.id.skillsListFormula2);
		                    formula.setText("Formula: " +(String) skill.get("formula"));
		            	} else if (name.equals(skill3)) {
		                    formula = (TextView) findViewById(R.id.skillsListFormula3);
		                    formula.setText("Formula: " +(String) skill.get("formula"));
		            	}
		            }
		            skills = (Object[]) ((Hashtable<String, Object>) class3.get("skills")).get("skill");
		            for (int i = 0; i < skills.length; i++) {
		            	Hashtable<String, Object> skill = (Hashtable<String, Object>)skills[i];
		            	String name = (String) skill.get("skill-name");
		            	TextView formula;
		            	if (name.equals(skill1)) {
		                    formula = (TextView) findViewById(R.id.skillsListFormula1);
		                    formula.setText("Formula: " +(String) skill.get("formula"));
		            	} else if (name.equals(skill2)) {
		                    formula = (TextView) findViewById(R.id.skillsListFormula2);
		                    formula.setText("Formula: " +(String) skill.get("formula"));
		            	} else if (name.equals(skill3)) {
		                    formula = (TextView) findViewById(R.id.skillsListFormula3);
		                    formula.setText("Formula: " +(String) skill.get("formula"));
		            	}
		            }
		    	}
			} else if (requestType == ServerLink.LOC_GET) {
	    		mapView.getOverlays().remove(players);
	            Hashtable<String, Object> updates = (Hashtable<String, Object> ) data.get("updates");
	            Hashtable<String, Object> locations = (Hashtable<String, Object>) updates.get("locations");
	            Object characters[] = (Object[]) locations.get("character");
	    		players = new PlayersOverlay(PlayerDrawable, characters);
	    		players.setActivity(this);
	    		mapView.getOverlays().add(players);
	    		mapView.postInvalidate();
    			updatePlayers(data);
	    	}
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
    }
	
	public void startBattle(Hashtable<String, Object> updates) {
		Log.v("ROLECLIENT", "Starting BAttle alert");
		int opponentId = Integer.parseInt((String) ((Hashtable<String, Object>) updates.get("battle")).get("initiator"));
		Intent myIntent = new Intent("role.intent.action.BATTLE_ALERT.RECEIVE_INVITE", Uri.parse("id://" + opponentId), this, BattleAlert.class);
//	    Intent svc = new Intent(this, ServerLinkService.class);
//	    stopService(svc);
		startActivityForResult(myIntent, 0);
	}
	
/*	public void startPoll() {
		ServerLinkService.callBack = this;
		Intent svc = new Intent(this, ServerLinkService.class);
		startService(svc);	
	}*/

	public boolean checkInternet() {
    	ConnectivityManager connect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connect.getNetworkInfo(0).isConnectedOrConnecting() || connect.getNetworkInfo(1).isConnectedOrConnecting() ) 
        	return true;                        	  
        else
        	return false;
    }

	public void setCharImage(String name) {
		ImageView iv = (ImageView) findViewById(R.id.charSelectionImage);
		if (name.equals("Assassin")) {
			iv.setImageResource(R.drawable.assassin);
		} else if (name.equals("Paladin")) {
			iv.setImageResource(R.drawable.paladin);
		} else {
			//name == "warrior"
			iv.setImageResource(R.drawable.warrior);
		}
	}

	public String getCharInfo(Hashtable<String,Object> charClass) {
    	//TODO get character tag
    	
    	String name = (String) charClass.get("name");
    	String description = (String) charClass.get("description");
    	Hashtable<String, Object> skill_hash = (Hashtable<String, Object>) charClass.get("skills");
    	Object skills[] = (Object[]) skill_hash.get("skill");
    	String skillsInfo = "";
    	for (int i = 0; i < skills.length; i++) {
    		Hashtable<String,Object> s = (Hashtable<String,Object>) skills[i];
    		String skillName = (String) s.get("skill-name");
    		String minLevel = (String) s.get("min-level");
    		String formula = (String) s.get("formula");
    		skillsInfo += skillName + ": Min Level = " + minLevel + ", Formula = " + formula + "\n";
    	}
    	
    	String info = name + "\n" + description + "\n" + skillsInfo;
    	return info;
    }

	public void updatePlayers(Hashtable<String, Object> data) {
		Hashtable<String, Object> updates = (Hashtable<String, Object> ) data.get("updates");
		Object locations[] = (Object[]) ((Hashtable<String, Object> ) updates.get("locations")).get("character");
		ListView list;
		ArrayAdapter<Player> adapter;
		if (findViewById(R.id.listPlayers) != null) {
			list = (ListView) findViewById(R.id.listPlayersList);
			adapter = (ArrayAdapter<Player>) list.getAdapter();
			adapter.clear();
			TextView feedback = (TextView) findViewById(R.id.listPlayersText);
			for (int i = 0; i < locations.length; i++) {
				Player player = new Player();
				player.setId(Integer.parseInt((String) ((Hashtable<String, Object>) locations[i]).get("id")));
				player.setName((String)((Hashtable<String, Object>) locations[i]).get("name"));
				adapter.add(player);
			}
			if (adapter.isEmpty()) {
				feedback.setText("There are currently no other online players.");
			} else {
				feedback.setText(null);
			}
		} else {
			playersList.clear();
			for (int i = 0; i < locations.length; i++) {
				Player player = new Player();
				player.setId(Integer.parseInt((String) ((Hashtable<String, Object>) locations[i]).get("id")));
				player.setName((String)((Hashtable<String, Object>) locations[i]).get("name"));
				playersList.add(player);
			}
		}
	}
	
	public void viewSelectedPlayer(int charId) {
		application.playButtonSound();
		isViewingSelectedPlayer = true;
        View view = View.inflate(RoleClient.this, R.layout.selectedplayerinfo, null);
        addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
        view = findViewById(R.id.listPlayers);
        if (view == null)
        	view = findViewById(R.id.worldMap);
       	view.setVisibility(View.INVISIBLE);
        selectedPlayerId = charId;
		((ImageButton) findViewById(R.id.selectedPlayerFightButton)).setEnabled(false);
    	setProgressBarIndeterminateVisibility(true);
        serverLink.sendRequest(ServerLink.CHAR_INSPECT, selectedPlayerId);
	}
	
/*	@Override
	protected void onDestroy() {
		super.onDestroy();
        Intent svc = new Intent(this, ServerLinkService.class);
        stopService(svc);
		
	}*/
}