package com.maps.developer.authenticplaces;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;
import com.maps.developer.authenticplaces.interfaces.LocationReceiver;
import com.maps.developer.authenticplaces.location.LocationUpdater;

public class MajorManager implements LocationReceiver {
    private static final String TAG = MapsActivity.class.getSimpleName();

    private static boolean mRequestingLocationUpdates = false;

    private final LocationUpdater locationUpdater;
    private final MapManager mapManager;

    public MajorManager(Context context, LocationManager locationManager,
                        SupportMapFragment mapFragment) {
        locationUpdater = new LocationUpdater(context, locationManager, this);
        mapManager = new MapManager(context);
        mapManager.uploadMap(mapFragment);
    }

    public void startTrackingLocation(Context context){
        Log.d(TAG, "startTrackingLocation");
        if (mRequestingLocationUpdates) {
            locationUpdater.startLocationUpdates(context);
        }
    }

    public void startSearching(Activity activity){
        Log.d(TAG, "startSearching");
        if (mRequestingLocationUpdates == false){
            locationUpdater.checkSettingsGPS(activity);
        }
    }

    public void stopTrackingLocation(){
        Log.d(TAG, "stopTrackingLocation");
        if (mRequestingLocationUpdates) {
            locationUpdater.stopLocationUpdates();
        }
    }

    public static void setAvailableLocationUpdates(boolean mRequestingLocationUpdates) {
        Log.d(TAG, "setAvailableLocationUpdates");
        MajorManager.mRequestingLocationUpdates = mRequestingLocationUpdates;
    }

    @Override
    public void sendLocation(Location location) {
        Log.d(TAG, "sendLocation: send in MapManager.");
        mapManager.updateCurrentLocation(location);
    }
}
