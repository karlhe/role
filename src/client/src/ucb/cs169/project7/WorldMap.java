package ucb.cs169.project7;

import java.util.Hashtable;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class WorldMap extends MapActivity implements ClientComponent {
	private Drawable PlayerDrawable;
    private MapView mapView;
    private ServerLink serverLink;
    private LocationManager locationManager;
    private PlayersOverlay players;
    double latitude, longitude;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worldmap);
        mapView = (MapView) findViewById(R.id.worldMapMapView);
        serverLink = new ServerLink(this);
        ServerLinkService.callBack = this;

    	PlayerDrawable = getResources().getDrawable(R.drawable.emo_im_wtf);
    	PlayerDrawable.setBounds(0, 0, PlayerDrawable.getIntrinsicWidth(), PlayerDrawable.getIntrinsicHeight());

		// PlayersOverlay.setActivity(this);
        final MapController mapController = mapView.getController();
        mapView.setBuiltInZoomControls(true);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria providerCriteria = new Criteria();
        providerCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationManager.requestLocationUpdates(locationManager.getBestProvider(providerCriteria, true), 60000, 15, new LocationListener() {
            public void onLocationChanged(Location location) {
                Hashtable<String, Object> data = new Hashtable<String, Object>();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                data.put("latitude", latitude);
                data.put("longitude", longitude);
                mapController.animateTo(new GeoPoint((int) (latitude * 1000000), (int) (longitude * 1000000)));
                serverLink.sendServiceRequest(ServerLink.LOC_SEND, data);
            }

            public void onProviderDisabled(String provider) {
                Toast.makeText(WorldMap.this, "GPS location updates have been disabled.", Toast.LENGTH_SHORT).show();
            }

            public void onProviderEnabled(String provider) {
                Toast.makeText(WorldMap.this, "GPS location updates have been enabled.", Toast.LENGTH_SHORT).show();
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
        });
        mapController.animateTo(new GeoPoint(37875581, -122258922));
        mapController.setZoom(21);
        MyLocationOverlay player = new MyLocationOverlay(this, mapView);
        player.enableMyLocation();
        mapView.getOverlays().add(player);
    }
    
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public void receiveData(Hashtable<String, Object> data, int requestType) {
    	if (requestType == ServerLink.LOC_GET) {
    		mapView.getOverlays().remove(players);
            Hashtable<String, Object> updates = (Hashtable<String, Object> ) data.get("updates");
            Hashtable<String, Object> locations = (Hashtable<String, Object>) updates.get("locations");
            Object characters[] = (Object[]) locations.get("character");
    		players = new PlayersOverlay(PlayerDrawable, characters);
    		mapView.getOverlays().add(players);
    		mapView.postInvalidate();
    	}

    	/*
        Hashtable<String, Object> updates = (Hashtable<String, Object> ) data.get("updates");
        Hashtable<String, Object> locations = (Hashtable<String, Object>) updates.get("locations");
        Object characters[] = (Object[]) locations.get("character");
        mapView.getOverlays().clear();
        for (int i = 0; i < characters.length; i++) {
            Hashtable<String, Object> character = (Hashtable<String,Object>) characters[i];
            Integer id = Integer.parseInt((String) character.get("id"));
            Player player;
            if (players.containsKey(id)) {
                player = players.get(id);
            } else {
                player = new Player();
                player.setId(id.intValue());
                player.setName((String) character.get("name"));
            }
            Hashtable<String,Object> location = (Hashtable<String, Object>) character.get("location");
            player.setLocation(new GeoPoint(Integer.parseInt((String) location.get("latitude")) * 1000000, Integer.parseInt((String) location.get("longitude")) * 1000000));
            mapView.getOverlays().add(player);
        }
        */
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
		  try {
			    {
			      ServerLinkService.callBack = this;
			      Intent svc = new Intent(this, ServerLinkService.class);
			      startService(svc);
			    }
			  }
			  catch (Exception e) {
			    Log.e(this.getLocalClassName(), "Server started already", e);
			  }	
        ServerLinkService.callBack = this;
    }
    
	public void startPoll() {
		ServerLinkService.callBack = this;
		Intent svc = new Intent(this, ServerLinkService.class);
		startService(svc);	
	}
}