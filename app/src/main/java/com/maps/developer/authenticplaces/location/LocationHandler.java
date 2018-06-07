package com.maps.developer.authenticplaces.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.maps.developer.authenticplaces.MapsActivity;
import com.maps.developer.authenticplaces.interfaces.LocationReceiver;

public class LocationHandler implements LocationListener{
    private static final String TAG = LocationHandler.class.getSimpleName();

    private final LocationReceiver locationReceiver;

    public LocationHandler(LocationReceiver locationReceiver) {
        this.locationReceiver = locationReceiver;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        Log.d(TAG, "onLocationChanged: invoke method (location from Internet).");
        if (location == null) return;
        Log.d(TAG, "onLocationChanged: location is not null.");
        locationReceiver.sendLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
