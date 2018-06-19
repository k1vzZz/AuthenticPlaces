package com.maps.developer.authenticplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
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
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.SupportMapFragment;
import com.maps.developer.authenticplaces.account.AccountInfo;
import com.maps.developer.authenticplaces.account.ProfileFragment;
import com.maps.developer.authenticplaces.content.CardContent;
import com.maps.developer.authenticplaces.interfaces.DownloadingCallback;
import com.maps.developer.authenticplaces.location.GPSManager;
import com.maps.developer.authenticplaces.network.NetworkCommunication;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements View.OnClickListener,
        CommentsFragment.OnFragmentInteractionListener, ProfileFragment.SignOutListener,
        DownloadingCallback {

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
    private FloatingActionButton buttonCancellationOfAddingMarker;
    private ContentLoadingProgressBar downloadingProgress;
    private Button buttonComments;
    private Button buttonAdditionImage;

    private boolean isBlocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate.");
        setContentView(R.layout.activity_maps);
        variablesInitialization(savedInstanceState);
        if (savedInstanceState == null){
            searchingDeviceLocation();
        }
    }

    private void variablesInitialization(Bundle savedInstanceState) {
        Log.d(TAG, "variablesInitialization: initializing.");
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBackgroundSnackbar));

        downloadingProgress = (ContentLoadingProgressBar) findViewById(R.id.progress_downloading);
        downloadingProgress.hide();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        buttonAdditionImage = (Button) findViewById(R.id.btn_addition_image);
        buttonAdditionImage.setOnClickListener(this);

        buttonComments = (Button) findViewById(R.id.btn_comments);
        buttonComments.setOnClickListener(this);

        buttonLocation = (FloatingActionButton) findViewById(R.id.btn_location);
        buttonLocation.setOnClickListener(this);

        buttonCancellationOfAddingMarker = (FloatingActionButton)
                findViewById(R.id.btn_cancel_addition_marker);
        buttonCancellationOfAddingMarker.setOnClickListener(this);

        buttonAdditionMarker = (FloatingActionButton) findViewById(R.id.btn_addition_marker);
        buttonAdditionMarker.setOnClickListener(this);
        LinearLayout llBottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (savedInstanceState == null) {
            LocationManager locationManager = (LocationManager) getApplicationContext().
                    getSystemService(LOCATION_SERVICE);
            majorManager = new MajorManager(getApplicationContext(), locationManager,
                    mapFragment, bottomSheetBehavior, R.string.shared_file_map);
        } else {
            refreshInstance(mapFragment, bottomSheetBehavior);
        }
        recyclerView.setAdapter(majorManager.getAdapter());

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        majorManager.setAccountInfo(account);
        majorManager.setDownloadingCallback(this);
    }

    private void refreshInstance(SupportMapFragment mapFragment, BottomSheetBehavior bottomSheetBehavior) {
        MainActivityFacade facade = (MainActivityFacade) getLastCustomNonConfigurationInstance();
        buttonAdditionImage.setVisibility(facade.additionImage);
        buttonComments.setVisibility(facade.comments);
        buttonLocation.setVisibility(facade.location);
        buttonAdditionMarker.setVisibility(facade.additionMarker);
        if (facade.cancellationOfAddingMarker == View.VISIBLE){
            changeStateScreen(true);
        } else {
            changeStateScreen(false);
        }
        isBlocked = facade.isBlocked;
        majorManager = facade.majorManager;
        majorManager.refreshMap(mapFragment);
        majorManager.refreshBottomSheet(bottomSheetBehavior);
        majorManager.refreshHandler();
        if (isBlocked){
            blockButtons();
        } else {
            unblockButtons();
        }
    }

    public NetworkInfo getActiveNetworkInfo() {
        Log.d(TAG, "getActiveNetworkInfo");
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Log.d(TAG, "onRetainCustomNonConfigurationInstance");
        majorManager.dropHandler();
        MainActivityFacade facade = new MainActivityFacade();
        facade.additionImage = buttonAdditionImage.getVisibility();
        facade.additionMarker = buttonAdditionMarker.getVisibility();
        facade.cancellationOfAddingMarker = buttonCancellationOfAddingMarker.getVisibility();
        facade.comments = buttonComments.getVisibility();
        facade.isBlocked = isBlocked;
        facade.location = buttonLocation.getVisibility();
        facade.majorManager = majorManager;
        return facade;
    }

    @Override
    public void onBackPressed() {
        if (majorManager.isShowCommentsFragment() || majorManager.isShowProfileFragment()){
            boolean showTwoFragments = majorManager.isShowCommentsFragment()
                    && majorManager.isShowProfileFragment();
            if (majorManager.isShowProfileFragment()){
                majorManager.exitProfileFragment();
            } else {
                majorManager.exitCommentsFragment();
            }
            if (!showTwoFragments) {
                showFloatingButtons(true);
            }
        } else {
            if (majorManager.isAdditionMarker()) {
                changeStateScreen(false);
                majorManager.cancelAdditionMarker();
                return;
            } else {
                if (majorManager.hideBottomSheet()) {
                    return;
                }
            }
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
                if (isBlocked) return true;
                if (majorManager.isAdditionMarker()) return true;
                if (majorManager.isAuthorization()){
                    majorManager.showProfileFragment(this);
                    buttonLocation.setVisibility(View.INVISIBLE);
                    buttonAdditionMarker.setVisibility(View.INVISIBLE);
                    buttonCancellationOfAddingMarker.setVisibility(View.INVISIBLE);
                } else {
                    majorManager.startSignInActivity(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeStateScreen(boolean showForAdditionMarker){
        Log.d(TAG, "changeStateScreen: " + showForAdditionMarker);
        if (showForAdditionMarker){
            buttonAdditionMarker.setImageResource(R.drawable.ic_done_new_marker);
            buttonCancellationOfAddingMarker.setVisibility(View.VISIBLE);
        } else {
            buttonCancellationOfAddingMarker.setVisibility(View.INVISIBLE);
            buttonAdditionMarker.setImageResource(R.drawable.ic_add_marker);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_addition_marker:
                Log.d(TAG, "buttonAdditionMarker: onCLick");
                if (majorManager.isAdditionMarker()){
                    int result = majorManager.doneAdditionMarker(getActiveNetworkInfo());
                    if(result == MajorManager.ADDITION_SUCCESS){
                        //progress bar
                        blockButtons();
                    } else {
                        if (result == MajorManager.EMPTY_PHOTO) {
                            Toast.makeText(this, "Add one image",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            if (result == MajorManager.CHOICE_OTHER_PLACE){
                                Toast.makeText(this, "Choice other place",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Turn on Internet",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } else {
                    if (majorManager.isAuthorization()) {
                        majorManager.addMarkerToMap();
                        changeStateScreen(true);
                    } else {
                        majorManager.startSignInActivity(this);
                    }
                }
                break;
            case R.id.btn_cancel_addition_marker:
                changeStateScreen(false);
                majorManager.cancelAdditionMarker();
                break;
            case R.id.btn_location:
                Log.d(TAG, "buttonLocation: onCLick");
                majorManager.showDeviceLocation(true);
                searchingDeviceLocation();
                break;
            case R.id.btn_addition_image:
                Log.d(TAG, "buttonAdditionImage: onClick");
                if (majorManager.isAuthorization()) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, CHOICE_IMAGE);
                } else {
                    majorManager.startSignInActivity(this);
                }
                break;
            case R.id.btn_comments:
                Log.d(TAG, "buttonComments: onClick");
                majorManager.showCommentsFragment(this);
                showFloatingButtons(false);
                majorManager.hideBottomSheet();
                break;
        }
    }

    public void showFloatingButtons(boolean show){
        Log.d(TAG, "showFloatingButtons: " + show);
        if (show){
            buttonAdditionMarker.setVisibility(View.VISIBLE);
            buttonLocation.setVisibility(View.VISIBLE);
            if (majorManager.isAdditionMarker()){
                buttonCancellationOfAddingMarker.setVisibility(View.VISIBLE);
            }
        } else {
            buttonAdditionMarker.setVisibility(View.INVISIBLE);
            buttonLocation.setVisibility(View.INVISIBLE);
            buttonCancellationOfAddingMarker.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void downloadedSuccessfully() {
        Log.d(TAG, "downloadedSuccessfully");
        changeStateScreen(false);
        unblockButtons();
    }

    private void unblockButtons() {
        Log.d(TAG, "unblockButtons");
        majorManager.unblockMap();
        downloadingProgress.hide();
        buttonCancellationOfAddingMarker.setEnabled(true);
        buttonAdditionMarker.setEnabled(true);
        buttonLocation.setEnabled(true);
        buttonComments.setEnabled(true);
        buttonAdditionImage.setEnabled(true);
        isBlocked = false;
    }

    @Override
    public void downloadError() {
        Log.d(TAG, "downloadError");
        downloadingProgress.hide();
    }

    private void blockButtons() {
        Log.d(TAG, "blockButtons");
        majorManager.blockMap();
        downloadingProgress.show();
        buttonCancellationOfAddingMarker.setEnabled(false);
        buttonAdditionMarker.setEnabled(false);
        buttonLocation.setEnabled(false);
        buttonComments.setEnabled(false);
        buttonAdditionImage.setEnabled(false);
        isBlocked = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        majorManager.startTrackingLocation(getApplicationContext());
        majorManager.startNetwork(getActiveNetworkInfo());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        majorManager.stopTrackingLocation();
        majorManager.stopNetwork();
        majorManager.saveState();
    }

    public static boolean isLocationPermissionsGPS() {
        return mLocationPermissionsGPS;
    }

    public static boolean isLocationPermissionInternet() {
        return mLocationPermissionInternet;
    }

    private void searchingDeviceLocation(){
        getLocationPermission();
        majorManager.checkSettings(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: receiving response");
        switch (requestCode){
            case GPSManager.REQUEST_CHECK_SETTINGS:
                Log.d(TAG, "onActivityResult: REQUEST_CHECK_SETTINGS: " + resultCode);
                switch (resultCode){
                    case RESULT_OK:
                        majorManager.updatingLocation(true);
                        break;
                    case RESULT_CANCELED:
                        majorManager.updatingLocation(false);
                        break;
                }
                break;
            case AccountInfo.REQUEST_SIGN:
                Log.d(TAG, "onActivityResult: REQUEST_SIGN: " + resultCode);
                switch (resultCode){
                    case RESULT_OK:
                        if (data == null) return;
                        GoogleSignInAccount account = data.getParcelableExtra(
                                AccountInfo.RECEIVED_ACCOUNT);
                        majorManager.setAccountInfo(account);
                        showSnackbar(R.string.signed_success);
                        break;
                    case RESULT_CANCELED:
                        break;
                }
                break;
            case CHOICE_IMAGE:
                Log.d(TAG, "onActivityResult: CHOICE_IMAGE: " + resultCode);
                switch (resultCode){
                    case RESULT_OK:
                        NetworkInfo networkInfo = getActiveNetworkInfo();
                        majorManager.setNetworkInfo(networkInfo);
                        if (NetworkCommunication.checkNetworkConnectivity(networkInfo)) {
                            try {
                                final Uri imageUri = data.getData();
                                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                majorManager.addImageToMarkerContent(data.getData(), selectedImage);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast(R.string.turn_on_internet);
                        }
                        break;
                }
                break;
        }
    }

    private void showToast(int resId){
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

    private void showSnackbar(int resId){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_parent), resId,
                Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().
                getColor(R.color.colorBackgroundSnackbar));
        snackbar.show();
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissionsGPS = {FINE_LOCATION, COARSE_LOCATION};
        String[] permissionsInternet = {INTERNET};
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(),
                COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGPS = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    permissionsGPS,
                    LOCATION_PERMISSION_REQUEST_GPS);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
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
                    majorManager.checkSettings(this);
                }
                break;
            case LOCATION_PERMISSION_REQUEST_INTERNET:
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionInternet = true;
                }
                break;
        }
    }

    @Override
    public void signOutAccount() {
        Log.d(TAG, "signOutAccount");
        majorManager.setAccountInfo(null);
    }

    @Override
    public void onDetachFragment(List<CardContent> addedComments) {
        Log.d(TAG, "onDetachFragment");
        majorManager.addComments(addedComments);
    }

    @Override
    public List<CardContent> getCardContentList() {
        return majorManager.getCardContentList();
    }

    @Override
    public boolean isAdditionMarker() {
        return majorManager.isAdditionMarker();
    }

    @Override
    public AccountInfo getAccountInfo() {
        return majorManager != null ? majorManager.getAccountInfo() : null;
    }

    @Override
    public void sendComment(CardContent cardContent) {
        Log.d(TAG, "sendComment");
        majorManager.sendComment(cardContent);
    }

    @Override
    public void authorization() {
        Log.d(TAG, "authorization");
        majorManager.startSignInActivity(this);
    }
}
