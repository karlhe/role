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

public class CharacterSelection extends Activity implements ClientComponent {
    private TextView description; //CharacterType ct; CharacterClass charType; 
    private boolean isSelected, isCharClassReceived;
    private ImageView iv;
	private String charSelectedName;
	private ServerLink serverLink;
	private Hashtable<String,Object> class1, class2, class3;
    Button char1, char2, char3;
    RoleClientApplication application;
    
	/*
    public String getInfo(CharacterClass charType) {
    	String info = charType.getName() + "\n" 
    					+ charType.getDescription() + "\n"
    					+ "Health:\t" + Integer.toString(charType.getHealth()) + "\n" 
    					+ "Attack:\t" + Integer.toString(charType.getAttack()) + "\n" 
    					+ "Defense:\t" + Integer.toString(charType.getDefense()) + "\n" 
    					+ "Stealth:\t" + Integer.toString(charType.getStealth());
    	return info;
    }
    */
	
	public void setImage(String name) {
		if (name.equals("Assassin")) {
			iv.setImageResource(R.drawable.assassin);
		} else if (name.equals("Paladin")) {
			iv.setImageResource(R.drawable.paladin);
		} else {
			//name == "warrior"
			iv.setImageResource(R.drawable.warrior);
		}
	}
    
    public String getInfo(Hashtable<String,Object> charClass) {
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
    
    public String getClassName(Hashtable<String,Object> charClass) {
    	String name = (String) charClass.get("name");
    	return name;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.characterselection);
        
        //ct = new CharacterType();
        isSelected = false; //is a class picked?
        isCharClassReceived = false; //did server send back info?
        
        description = (TextView) findViewById(R.id.charSelectionDescription);
    	iv = (ImageView) findViewById(R.id.charImage); 
       
    	application = (RoleClientApplication) getApplication();
    	
        //handle button
        char1 = (Button) findViewById(R.id.charSelectionCharButton1);
        char1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	application.playButtonSound();
            	//charType = ct.getAssassin();
            	if (isCharClassReceived) {
                	description.setText(getInfo(class1));
                	charSelectedName = getClassName(class1);
                	//iv.setImageResource(R.drawable.assassin);
                	setImage(charSelectedName);
                	isSelected = true;
            	} else {
            		//server has not responded yet
            		Toast.makeText(CharacterSelection.this,"Server has not responded. Please try again.",Toast.LENGTH_SHORT).show();
            	}
            }
        });
        
        //handle button
        char2 = (Button) findViewById(R.id.charSelectionCharButton2);
        char2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	application.playButtonSound();
            	//charType = ct.getWarrior();
            	if (isCharClassReceived) {
                	description.setText(getInfo(class2));
                	charSelectedName = getClassName(class2);
                	//iv.setImageResource(R.drawable.warrior);
                	setImage(charSelectedName);
                	isSelected = true;
            	} else {
            		//server has not responded yet
            		Toast.makeText(CharacterSelection.this,"Server has not responded. Please try again.",Toast.LENGTH_SHORT).show();
            	}
            }
        });
        
        //handle button
        char3 = (Button) findViewById(R.id.charSelectionCharButton3);
        char3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	application.playButtonSound();
            	//charType = ct.getPaladin();
            	if (isCharClassReceived) {
                	description.setText(getInfo(class3));
                	charSelectedName = getClassName(class3);
                	//iv.setImageResource(R.drawable.paladin);
                	setImage(charSelectedName);
                	isSelected = true;
            	} else {
            		//server has not responded yet
            		Toast.makeText(CharacterSelection.this,"Server has not responded. Please try again.",Toast.LENGTH_SHORT).show();
            	}
            }
        });
        
        //handle confirm button
        Button confirm = (Button) findViewById(R.id.charSelectionConfirmButton);
        confirm.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	application.playButtonSound();
            	if (isSelected == false) {
            		Toast.makeText(CharacterSelection.this,"Please select a character.",Toast.LENGTH_SHORT).show();
            	} else {
                    //TODO: send info
            		Intent intent = new Intent();
                    intent.putExtra("characterSelected", charSelectedName);
            		setResult(RESULT_OK, intent);
                    finish();
            	}
            }
        });
        
        //handle cancel button
        Button cancel = (Button) findViewById(R.id.charSelectionCancelButton);
        cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	application.playButtonSound();
            	Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });      
        
        serverLink = new ServerLink(this);
        serverLink.sendServiceRequest(ServerLink.CHAR_CLASSES);
        
    }
    
	public void receiveData(Hashtable<String, Object> data, int requestType) {
    	try {
			if (requestType == ServerLink.CHAR_CLASSES) {
				Hashtable<String,Object> test = (Hashtable<String, Object>) data.get("character-classes");
				Object test2 = test.get("character-class");
				Object[] charClasses = (Object[]) test.get("character-class");
	    		class1 = (Hashtable<String,Object>)charClasses[0];
	    		class2 = (Hashtable<String,Object>)charClasses[1];
	    		class3 = (Hashtable<String,Object>)charClasses[2];
	    		isCharClassReceived = true;	
	    		char1.setText(getClassName(class1));
	    		char2.setText(getClassName(class2));
	    		char3.setText(getClassName(class3));
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}		
		
    }
	
	public void startPoll() {
		//Ignore
	}

}