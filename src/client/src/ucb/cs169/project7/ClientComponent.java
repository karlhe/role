package ucb.cs169.project7;

import java.util.Hashtable;

import android.app.Application;

public interface ClientComponent {
	public void receiveData(Hashtable<String, Object> data, int requestType);

	//public void startBattle(Hashtable<String, Object> updates);

	public Application getApplication();
	
	//public void startPoll();
}

