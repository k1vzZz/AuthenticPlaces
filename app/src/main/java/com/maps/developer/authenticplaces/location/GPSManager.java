package com.maps.developer.authenticplaces.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.maps.developer.authenticplaces.MajorManager;
import com.maps.developer.authenticplaces.MapsActivity;
import com.maps.developer.authenticplaces.interfaces.LocationReceiver;

public class GPSManager extends LocationCallback implements LocationReceiver {
    private static final String TAG = MapsActivity.class.getSimpleName();

    public static final String PROVIDER_GPS = "fused";
    public static final String PROVIDER_NETWORK = "network";
    public static final String LAST_AVAILABLE_LOCATION = "last";

    public static final int REQUEST_CHECK_SETTINGS = 20;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequestHighAccuracy;
    private LocationSettingsRequest.Builder builder;
    private InternetLocationManager internetManager;
    private final LocationReceiver locationReceiver;
    private boolean updating = false;

    public GPSManager(Context context, LocationManager locationManager,
                      LocationReceiver locationReceiver) {
        this.locationReceiver = locationReceiver;
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        internetManager = new InternetLocationManager(context, locationManager, locationReceiver);
        createLocationRequest();
    }

    private void createLocationRequest() {
        Log.d(TAG, "createLocationRequest: create request.");
        mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setInterval(10000);
        mLocationRequestHighAccuracy.setFastestInterval(5000);
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequestHighAccuracy);
    }

    private void getLastAvailableDeviceLocation(final Context context) {
        Log.d(TAG, "getLastAvailableDeviceLocation: getting current location");
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Task<Location> location = mFusedLocationClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found last available location.");
                            Location location = task.getResult();
                            if (location != null) {
                                location.setProvider(LAST_AVAILABLE_LOCATION);
                                sendLocation(location);
                            }
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getLastAvailableDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    @Override
    public void sendLocation(Location location){
        Log.d(TAG, "sendLocation: invoke method (send location from GPS module).");
        if (location == null) return;
        Log.d(TAG, "sendLocation: location is not null");
        locationReceiver.sendLocation(location);
    }

    public void startTrackingGPS(Context context){
        Log.d(TAG, "startTrackingGPS");
        getLastAvailableDeviceLocation(context);
        startLocationUpdatesViaGPS(context);
        internetManager.startUpdateLocation(context);
    }

    public void stopTrackingGPS(){
        Log.d(TAG, "stopTrackingGPS");
        stopUpdatesViaInternet();
        stopLocationUpdatesViaGPS();
    }

    public void stopUpdatesViaInternet(){
        internetManager.stopUpdateLocation();
    }

    private void startLocationUpdatesViaGPS(final Context context) {
        Log.d(TAG, "startLocationUpdatesViaGPS: check permissions");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d(TAG, "startLocationUpdatesViaGPS: start updates.");
        updating = true;
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy,
                this,
                null);
    }

    private void stopLocationUpdatesViaGPS(){
        if (updating) {
            Log.d(TAG, "stopLocationUpdatesViaGPS.");
            mFusedLocationClient.removeLocationUpdates(this);
            updating = false;
        }
    }

    public void checkSettingsGPS(final Activity activity){
        Log.d(TAG, "checkSettingsGPS");
        SettingsClient client = LocationServices.getSettingsClient(activity.getApplicationContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, "checkSettingsGPS: onSuccess: success.");
                MajorManager.setAvailableLocationUpdates(true);
                startTrackingGPS(activity.getApplicationContext());
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e1) {
                        Log.w(TAG, "onFailure: " + e1.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        Log.d(TAG, "onLocationResult");
        if (locationResult == null) {
            return;
        }
        for (Location location : locationResult.getLocations()) {
            Log.d(TAG, "onLocationResult: " + location);
            if (location == null) continue;
            sendLocation(location);
        }
    }
}
