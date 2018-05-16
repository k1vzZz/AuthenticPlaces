package com.maps.developer.authenticplaces.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.maps.developer.authenticplaces.MapsActivity;
import com.maps.developer.authenticplaces.R;
import com.maps.developer.authenticplaces.interfaces.LocationReceiver;

public class InternetLocationManager {
    private static final String TAG = MapsActivity.class.getSimpleName();

    private int minTime;
    private int minDistance;
    private static boolean updating = false;

    private LocationManager locationManager;
    private LocationListener locationListener;

    public InternetLocationManager(Context context, LocationManager locationManager,
                                   LocationReceiver locationReceiver) {
        this.locationManager = locationManager;
        this.locationListener = new LocationHandler(locationReceiver);
        minTime = context.getResources().getInteger(R.integer.min_time);
        minDistance = context.getResources().getInteger(R.integer.min_distance);
    }

    public void startUpdateLocation(Context context) {
        Log.d(TAG, "startUpdateLocation: check permissions GPS.");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (MapsActivity.isLocationPermissionInternet() == false ||
                hasInternetConnection(context) == false){
            updating = false;
            return;
        }
        Log.d(TAG, "startUpdateLocation: request location updates via Internet");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, locationListener);
        updating = true;
    }

    public void stopUpdateLocation(){
        if (updating) {
            Log.d(TAG, "stopUpdateLocation: stop update via Internet.");
            locationManager.removeUpdates(locationListener);
            updating = false;
        }
    }

    public boolean hasInternetConnection(Context context){
        Log.d(TAG, "hasInternetConnection");
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) return false;
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_MOBILE
                        && networkInfo.getType() != ConnectivityManager.TYPE_WIFI)){
            return false;
        }
        return true;
    }

    public static boolean isUpdating() {
        return updating;
    }
}
