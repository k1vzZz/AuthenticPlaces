package com.maps.developer.authenticplaces.location;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.maps.developer.authenticplaces.MapsActivity;
import com.maps.developer.authenticplaces.R;
import com.maps.developer.authenticplaces.interfaces.LocationReceiver;

public class LocationUpdater implements LocationReceiver {
    private static final String TAG = MapsActivity.class.getSimpleName();

    private Location mCurrentLocation;
    private GPSManager gpsManager;
    private LocationReceiver locationReceiver;
    private int numberSteps;

    public LocationUpdater(Context context, LocationManager locationManager,
                           LocationReceiver locationReceiver) {
        this.locationReceiver = locationReceiver;
        gpsManager = new GPSManager(context, locationManager, this);
        numberSteps = context.getResources().getInteger(R.integer.number_steps);
    }

    @Override
    public void sendLocation(Location location){
        Log.d(TAG, "sendLocation: invoke method (update location).");
        if (location == null) return;
        Log.d(TAG, "sendLocation: send location in MajorManager.");
        Log.d(TAG, "sendLocation: provider = " + location.getProvider());
        if(checkProviderByLocation(location)) {
            mCurrentLocation = location;
            locationReceiver.sendLocation(location);
        }
    }

    private boolean checkProviderByLocation(Location location){
        Log.d(TAG, "checkProviderByLocation");
        if (InternetLocationManager.isUpdating()
                && location.getProvider().equals(GPSManager.PROVIDER_GPS)){
            numberSteps--;
            if (numberSteps == 0) {
                gpsManager.stopUpdatesViaInternet();
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public void checkSettingsGPS(final Activity activity){
        if (MapsActivity.isLocationPermissionsGPS()) {
            gpsManager.checkSettingsGPS(activity);
        }
    }

    public void startLocationUpdates(Context context){
        Log.d(TAG, "startLocationUpdates");
        if (MapsActivity.isLocationPermissionsGPS()){
            gpsManager.startTrackingGPS(context);
        }
    }

    public void stopLocationUpdates(){
        Log.d(TAG, "stopLocationUpdates");
        if (MapsActivity.isLocationPermissionsGPS()){
            gpsManager.stopTrackingGPS();
        }
    }
}
