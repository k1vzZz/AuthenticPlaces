package com.maps.developer.authenticplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.SupportMapFragment;
import com.maps.developer.authenticplaces.location.GPSManager;

public class MapsActivity extends AppCompatActivity implements View.OnClickListener, CommentsFragment.OnFragmentInteractionListener {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String INTERNET = Manifest.permission.INTERNET;

    private static final int LOCATION_PERMISSION_REQUEST_GPS = 10;
    private static final int LOCATION_PERMISSION_REQUEST_INTERNET = 11;

    private static final int CHOICE_IMAGE = 12;

    private static boolean mLocationPermissionsGPS = false;
    private static boolean mLocationPermissionInternet = false;

    private MajorManager majorManager;

    private FloatingActionButton buttonLocation;
    private FloatingActionButton buttonAdditionMarker;

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

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        LinearLayout llBottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        Button buttonAdditionImage = (Button) findViewById(R.id.btn_addition_image);
        buttonAdditionImage.setOnClickListener(this);

        Button buttonComments = (Button) findViewById(R.id.btn_comments);
        buttonComments.setOnClickListener(this);

        buttonLocation = findViewById(R.id.btn_location);
        buttonLocation.setOnClickListener(this);

        buttonAdditionMarker = (FloatingActionButton) findViewById(R.id.btn_addition_marker);
        buttonAdditionMarker.setOnClickListener(this);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Log.d(TAG, "variablesInitialization: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        majorManager = new MajorManager(this, locationManager,
                mapFragment, bottomSheetBehavior);

        recyclerView.setAdapter(majorManager.getAdapter());
    }

    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
//        return super.onRetainCustomNonConfigurationInstance();
        return majorManager;
    }

    @Override
    public void onDetachFragment() {
        majorManager.showBottomSheet();
        buttonAdditionMarker.setVisibility(View.VISIBLE);
        buttonLocation.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (majorManager.hideBottomSheet()){
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.option_profile:
                if (majorManager.isAuthorization()){
                    Intent intent = new Intent(this, ProfileActivity.class);
                    intent.putExtra(AccountInfo.RECEIVED_ACCOUNT, majorManager.getAccount());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, SignInActivity.class);
                    startActivityForResult(intent, AccountInfo.REQUEST_SIGN);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_location:
                Log.d(TAG, "buttonLocation: onCLick");
                searchingDeviceLocation();
                break;
            case R.id.btn_addition_image:
                Log.d(TAG, "buttonAdditionImage: onClick");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, CHOICE_IMAGE);
                break;
            case R.id.btn_comments:
                Log.d(TAG, "buttonComments: onClick");
                majorManager.showCommentsFragment(this);
                buttonAdditionMarker.setVisibility(View.INVISIBLE);
                buttonLocation.setVisibility(View.INVISIBLE);
                majorManager.hideBottomSheet();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        majorManager.startTrackingLocation(this.getApplicationContext());
        majorManager.startNetwork(getActiveNetworkInfo());
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
            case AccountInfo.REQUEST_SIGN:
                Log.d(TAG, "onActivityResult: REQUEST_SIGN");
                switch (resultCode){
                    case RESULT_OK:
                        Log.d(TAG, "onActivityResult: RESULT_OK: signed successful.");
                        if (data == null) return;
                        GoogleSignInAccount account = data.getParcelableExtra(AccountInfo.RECEIVED_ACCOUNT);
                        majorManager.setAccountInfo(account);
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_parent),
                                R.string.signed_success,
                                Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundSnackbar));
                        snackbar.show();
                        break;
                    case RESULT_CANCELED:
                        Log.d(TAG, "onActivityResult: RESULT_CANCELED");
                        break;
                }
                break;
            case CHOICE_IMAGE:
                Log.d(TAG, "onActivityResult: CHOICE_IMAGE");
                switch (resultCode){
                    case RESULT_OK:
                        majorManager.addImageToMarkerContent(data.getData());
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
