package ucb.cs169.project7;

import java.io.FileOutputStream;
import java.io.PrintStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;


/**
 * Android accelerometer sensor tutorial
 * @author antoine vianey
 * under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 */
public class Accelerometer extends Activity implements AccelerometerListener {
	
	float lastX, lastY, lastZ, storedX, storedY, storedZ;
	Button action;
	Time storedTime;
	boolean isDone;
	boolean isPressed;
	String positions = "";
	TextView xtest, ytest, ztest, xtest2, ytest2, ztest2;
	
	
	private static Context CONTEXT;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accelerometer);
        CONTEXT = this;
        
        /**
         * Testing
         */
        
        xtest = (TextView) findViewById(R.id.xtest);
        ytest = (TextView) findViewById(R.id.ytest);
        ztest = (TextView) findViewById(R.id.ztest);
        xtest2 = (TextView) findViewById(R.id.xtest2);
        ytest2 = (TextView) findViewById(R.id.ytest2);
        ztest2 = (TextView) findViewById(R.id.ztest2);
        /**
         * Testing End
         */
        
        action = (Button) findViewById(R.id.action);
        storedTime = new Time();
        action.setOnTouchListener(new OnTouchListener() {
        	public boolean onTouch(View v, MotionEvent m) {
        		((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(300);
        		if (m.getAction() == MotionEvent.ACTION_DOWN) {
            		//button pressed
        			storedTime.setToNow();
            		storedX = lastX;
            		storedY = lastY;
            		storedZ = lastZ;
            		action.setTextColor(Color.RED);	
            		
            		xtest.setText(String.valueOf(lastX));
            		ytest.setText(String.valueOf(lastY));
            		ztest.setText(String.valueOf(lastZ));
            		
            		
        		} else if (m.getAction() == MotionEvent.ACTION_UP) {
            		//button being released
            		Time now = new Time();
            		now.setToNow();
            		test();
            		if (now.second - storedTime.second > 2) {
            			Toast.makeText(Accelerometer.this,"Too slow!",Toast.LENGTH_SHORT).show();
            		} else if (lastX - storedX > 0 && lastX - storedX < 14 && lastY - storedY < 3 && lastY - storedY > -15 && lastZ - storedZ > -12 && lastZ - storedZ < 2) {
            			Toast.makeText(Accelerometer.this,"Stabbed!!",Toast.LENGTH_SHORT).show();
            		} else if (lastX - storedX < 5 && lastX - storedX > -5 && storedY - lastY > 7 && lastZ - storedZ > 9) {
            			Toast.makeText(Accelerometer.this,"Slapped down!!",Toast.LENGTH_SHORT).show();
            		} else if (lastX - storedX < 5 && lastX - storedX > -5 && lastY-storedY > 0 && lastY-storedY < 13 && lastZ - storedZ > -21 && lastZ - storedZ < -1 ) {
            			Toast.makeText(Accelerometer.this,"Slapped up!!",Toast.LENGTH_SHORT).show();
            		} else {
            			Toast.makeText(Accelerometer.this,"Attack move failed!",Toast.LENGTH_SHORT).show();
            		}
            		
            		action.setTextColor(Color.BLACK);
            		
            		xtest2.setText(String.valueOf(lastX));
            		ytest2.setText(String.valueOf(lastY));
            		ztest2.setText(String.valueOf(lastZ));
            		
            		
        		}
        		return true;
        	}
        });
   
        isDone = false;
        isPressed = false;
        Button test = (Button) findViewById(R.id.test);
        test.setOnTouchListener(new OnTouchListener() {
        	public boolean onTouch(View v, MotionEvent m) {
        		if (m.getAction() == MotionEvent.ACTION_DOWN) {
        			Toast.makeText(Accelerometer.this,"down",Toast.LENGTH_SHORT).show();
        			isPressed = true;
        		} else if (m.getAction() == MotionEvent.ACTION_UP) {
        			isDone = true;
        			Toast.makeText(Accelerometer.this,"up",Toast.LENGTH_SHORT).show();
        		}
        		  
        		return true;
        	}
        });
    }

    public void test() {
    	if (AccelerometerManager.isSupported()) {
    		AccelerometerManager.startListening(this);
    		for (int i = 0; i < 100; i++) {
    			
    		}
    		Toast.makeText(Accelerometer.this,String.valueOf(AccelerometerManager.test()),Toast.LENGTH_SHORT).show();
    		
    	}
    }
    
    @Override
	protected void onResume() {
    	super.onResume();
    	if (AccelerometerManager.isSupported()) {
    		AccelerometerManager.startListening(this);
    	}
    }
    
    @Override
	protected void onDestroy() {
    	super.onDestroy();
    	if (AccelerometerManager.isListening()) {
    		AccelerometerManager.stopListening();
    	}
    	
    }
	
    public static Context getContext() {
		return CONTEXT;
	}

    /**
     * onShake callback
     */
	public void onShake(float force) {
		Toast.makeText(this, "Phone shaked : " + force, 1000).show();
	}

	/**
	 * onAccelerationChanged callback
	 */
	public void onAccelerationChanged(float x, float y, float z) {
		((TextView) findViewById(R.id.x)).setText(String.valueOf(x));
		((TextView) findViewById(R.id.y)).setText(String.valueOf(y));
		((TextView) findViewById(R.id.z)).setText(String.valueOf(z));
		lastX = x;
		lastY = y;
		lastZ = z;
		
		if (isPressed == true) {
			positions = positions + String.valueOf(x) + "\t" + String.valueOf(y) + "\t" + String.valueOf(z) + "\n";
		}
		if (isDone == true) {
			FileOutputStream out;
			PrintStream p;
			try {
				 System.out.println(positions);
				 isDone = false;
				 isPressed = false;
				Toast.makeText(Accelerometer.this,"Done!!!",Toast.LENGTH_SHORT).show();
			}
			catch (Exception e) {
				Toast.makeText(Accelerometer.this,"Error...",Toast.LENGTH_SHORT).show();
			}
		}
		
	}
    
}