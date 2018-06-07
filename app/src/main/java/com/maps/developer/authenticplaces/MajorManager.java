package com.maps.developer.authenticplaces;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.SupportMapFragment;
import com.maps.developer.authenticplaces.interfaces.LocationReceiver;
import com.maps.developer.authenticplaces.location.LocationUpdater;
import com.maps.developer.authenticplaces.model.InputInfoMarker;
import com.maps.developer.authenticplaces.network.NetworkCommunication;

import java.io.File;
import java.net.URI;
import java.util.List;

public class MajorManager implements LocationReceiver, Handler.Callback{
    private static final String TAG = MajorManager.class.getSimpleName();

    private static boolean mRequestingLocationUpdates = false;

    private AccountInfo accountInfo;
    private NetworkCommunication networkCommunication;
    private final Handler majorHandler;
    private final LocationUpdater locationUpdater;
    private final MapManager mapManager;
    private final BottomSheetHandler bottomSheetHandler;

    public MajorManager(Context context, LocationManager locationManager,
                        SupportMapFragment mapFragment, BottomSheetBehavior bottomSheetBehavior) {
        locationUpdater = new LocationUpdater(context, locationManager, this);
        bottomSheetHandler = new BottomSheetHandler(context, bottomSheetBehavior);
        majorHandler = new Handler(this);
        mapManager = new MapManager(context, bottomSheetHandler);
        mapManager.uploadMap(mapFragment);
    }

    public void startNetwork(NetworkInfo networkInfo){
        if (networkCommunication == null) {
            networkCommunication = new NetworkCommunication(majorHandler, networkInfo);
            networkCommunication.startNetworkWork();
        }
    }

    public void setAccountInfo(GoogleSignInAccount account) {
        if (accountInfo == null) {
            accountInfo = new AccountInfo(account);
        }
    }

    public boolean isAuthorization() {
        return accountInfo != null;
    }

    public GoogleSignInAccount getAccount(){
        return accountInfo.getAccount();
    }

    public boolean hideBottomSheet(){
        return bottomSheetHandler.hideBottomSheet();
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

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case NetworkCommunication.REFRESH_MARKERS:
                mapManager.refreshMarkers((InputInfoMarker) msg.obj);
                break;
        }
        return true;
    }

    public void addImageToMarkerContent(Uri imageUri) {
        //send image to server
        bottomSheetHandler.addMarkerPhoto(imageUri.toString(),"");
    }

    public RecyclerView.Adapter getAdapter() {
        return bottomSheetHandler.getAdapter();
    }

    public void showBottomSheet() {
        bottomSheetHandler.setStateBottomSheet(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void showCommentsFragment(MapsActivity activity) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        CommentsFragment fragment = new CommentsFragment();
        //Fetch data from server
        transaction.replace(R.id.main_parent, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
