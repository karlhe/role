package ucb.cs169.project7;

import java.util.Hashtable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class PlayersOverlay extends ItemizedOverlay {
	Object characters[];
	static RoleClient  activity;
	
	public PlayersOverlay(Drawable defaultMarker) {
		super(defaultMarker);
	}
	
	public PlayersOverlay(Drawable defaultMarker, Object characters[]) {
		super(defaultMarker);
		this.characters = characters;
		boundCenterBottom(defaultMarker);
		populate();
	}
		
	public static void setActivity(RoleClient a) {
		activity = a;
	}

	@Override
	protected OverlayItem createItem(int i) {
		Hashtable<String, Object> character = (Hashtable<String, Object>) characters[i];
		Hashtable<String, Object> location = (Hashtable<String, Object>) character.get("location");		
		String name = (String) character.get("name");
		GeoPoint point = new GeoPoint((int) (new Double((String) location.get("latitude")) * 1000000), (int) (new Double((String) location.get("longitude")) * 1000000));
		return new OverlayItem(point, name, name);
	}

	@Override
	public int size() {
		return characters.length;
	}

	@Override
	protected boolean onTap(int index) {
		Hashtable<String, Object> character = (Hashtable<String, Object>) characters[index];
		activity.viewSelectedPlayer(Integer.parseInt((String) character.get("id")));
    	return true;
	}
}
