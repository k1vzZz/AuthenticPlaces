package com.maps.developer.authenticplaces;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.SupportMapFragment;
import com.maps.developer.authenticplaces.location.GPSManager;

public class MapsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String INTERNET = Manifest.permission.INTERNET;

    private static final int LOCATION_PERMISSION_REQUEST_GPS = 10;
    private static final int LOCATION_PERMISSION_REQUEST_INTERNET = 11;

    private static boolean mLocationPermissionsGPS = false;
    private static boolean mLocationPermissionInternet = false;

    private MajorManager majorManager;
    private LocationManager locationManager;

    private FloatingActionButton buttonLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate.");
        setContentView(R.layout.activity_maps);
        variablesInitialization();
        searchingDeviceLocation();
    }

    private void variablesInitialization() {
        Log.d(TAG, "variablesInitialization: initializing.");
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        buttonLocation = findViewById(R.id.btnLocation);
        buttonLocation.setOnClickListener(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Log.d(TAG, "variablesInitialization: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        majorManager = new MajorManager(this, locationManager, mapFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.option_settings:
                return true;
            case R.id.option_profile:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLocation) {
            Log.d(TAG, "buttonSearch: onCLick");
            searchingDeviceLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        majorManager.startTrackingLocation(this.getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        majorManager.stopTrackingLocation();
    }

    public static boolean isLocationPermissionsGPS() {
        return mLocationPermissionsGPS;
    }

    public static boolean isLocationPermissionInternet() {
        return mLocationPermissionInternet;
    }

    private void searchingDeviceLocation(){
        getLocationPermission();
        majorManager.startSearching(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: receiving response");
        switch (requestCode){
            case GPSManager.REQUEST_CHECK_SETTINGS:
                Log.d(TAG, "onActivityResult: CHECK_SETTINGS: " + resultCode);
                switch (resultCode){
                    case RESULT_OK:
                        MajorManager.setAvailableLocationUpdates(true);
                        break;
                    case RESULT_CANCELED:
                        MajorManager.setAvailableLocationUpdates(false);
                        break;
                }
                break;
        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissionsGPS = {FINE_LOCATION, COARSE_LOCATION};
        String[] permissionsInternet = {INTERNET};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGPS = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    permissionsGPS,
                    LOCATION_PERMISSION_REQUEST_GPS);
        }
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                INTERNET) == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionInternet = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    permissionsInternet,
                    LOCATION_PERMISSION_REQUEST_INTERNET);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called.");
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_GPS:
                if (permissions.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionsGPS = true;
                    majorManager.startSearching(this);
                }
                break;
            case LOCATION_PERMISSION_REQUEST_INTERNET:
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionInternet = true;
                }
                break;
        }
    }
}
