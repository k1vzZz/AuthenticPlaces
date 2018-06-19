package com.maps.developer.authenticplaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.maps.developer.authenticplaces.account.AccountInfo;
import com.maps.developer.authenticplaces.account.ProfileFragment;
import com.maps.developer.authenticplaces.account.SignInActivity;
import com.maps.developer.authenticplaces.content.CardContent;
import com.maps.developer.authenticplaces.interfaces.DownloadingCallback;
import com.maps.developer.authenticplaces.interfaces.LocationReceiver;
import com.maps.developer.authenticplaces.interfaces.MapCallback;
import com.maps.developer.authenticplaces.interfaces.OnBottomContentListener;
import com.maps.developer.authenticplaces.location.LocationUpdater;
import com.maps.developer.authenticplaces.model.input.InputContentMarker;
import com.maps.developer.authenticplaces.model.input.InputInfoLatLngMarker;
import com.maps.developer.authenticplaces.network.NetworkCommunication;

import java.util.List;

public class MajorManager implements LocationReceiver, Handler.Callback, OnBottomContentListener {
    private static final String TAG = MajorManager.class.getSimpleName();

    public static final int ADDITION_SUCCESS = 33;
    public static final int INTERNET_NOT_AVAILABLE = 34;
    public static final int EMPTY_PHOTO = 35;
    public static final int CHOICE_OTHER_PLACE = 36;

    private AccountInfo accountInfo;
    private NetworkCommunication networkCommunication;
    private boolean showCommentsFragment;
    private boolean showProfileFragment;
    private Handler majorHandler;
    private final LocationUpdater locationUpdater;
    private final MapCallback mapCallback;
    private final BottomSheetHandler bottomSheetHandler;
    private DownloadingCallback downloadingCallback;

    public MajorManager(Context context, LocationManager locationManager,
                        SupportMapFragment mapFragment, BottomSheetBehavior bottomSheetBehavior,
                        int resourceFile) {
        locationUpdater = new LocationUpdater(context, locationManager, this);
        bottomSheetHandler = new BottomSheetHandler(context, this,
                bottomSheetBehavior);
        majorHandler = new Handler(this);
        accountInfo = new AccountInfo();
        networkCommunication = new NetworkCommunication(majorHandler);
        mapCallback = new MapManager(context, bottomSheetHandler, resourceFile);
        mapCallback.uploadMap(mapFragment);
    }

    public void setDownloadingCallback(DownloadingCallback downloadingCallback) {
        this.downloadingCallback = downloadingCallback;
    }

    public void startNetwork(NetworkInfo networkInfo){
        Log.d(TAG, "startNetwork: " + (networkInfo!=null));
        networkCommunication.setNetworkInfo(networkInfo);
        networkCommunication.startNetworkWork();
    }

    public void setAccountInfo(GoogleSignInAccount account) {
        accountInfo.setAccount(account);
    }

    public boolean isAuthorization() {
        return accountInfo != null && accountInfo.getAccount() != null;
    }

    public boolean hideBottomSheet(){
        return bottomSheetHandler.hideBottomSheet();
    }

    public void startTrackingLocation(Context context){
        Log.d(TAG, "startTrackingLocation");
        locationUpdater.startLocationUpdates(context);
    }

    public void checkSettings(Activity activity){
        Log.d(TAG, "checkSettings");
        locationUpdater.checkSettingsGPS(activity);
    }

    public void stopTrackingLocation(){
        Log.d(TAG, "stopTrackingLocation");
        locationUpdater.stopLocationUpdates();
    }

    public void updatingLocation(boolean updating) {
        locationUpdater.setUpdating(updating);
    }

    @Override
    public void sendLocation(Location location) {
        Log.d(TAG, "sendLocation: send in MapManager.");
        mapCallback.updateCurrentLocation(location);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case NetworkCommunication.REFRESH_MARKERS:
                Log.d(TAG, "Major handler: " + NetworkCommunication.REFRESH_MARKERS);
                mapCallback.refreshMarkers((InputInfoLatLngMarker) msg.obj);
                break;
            case NetworkCommunication.ADDITION_SUCCESS:
                //this identifier Marker
                mapCallback.addMarkerSuccess((Integer) msg.obj);
                bottomSheetHandler.clearAddedPhotos();
                bottomSheetHandler.clearAddedComments();
                bottomSheetHandler.unblockHide();
                downloadingCallback.downloadedSuccessfully();
                bottomSheetHandler.hideBottomSheet();
                break;
            case NetworkCommunication.UPDATE_SUCCESS:
                break;
            case NetworkCommunication.SEND_ERROR:
                downloadingCallback.downloadError();
                break;
            case NetworkCommunication.CONTENT_MARKER:
                bottomSheetHandler.refreshContent((InputContentMarker) msg.obj);
                break;
        }
        return true;
    }

    public void addImageToMarkerContent(Uri imageUri, Bitmap imageBitmap) {
        Log.d(TAG, "addImageToMarkerContent");
        //send image to server
        bottomSheetHandler.addMarkerPhoto(imageUri.toString(),null, imageBitmap);
        if (isAdditionMarker() == false){
            networkCommunication.sendMarkerContent(accountInfo,
                    bottomSheetHandler.getMarkerContent(), null);
            bottomSheetHandler.clearAddedPhotos();
        }
    }

    public RecyclerView.Adapter getAdapter() {
        return bottomSheetHandler.getAdapter();
    }

    public void showBottomSheet() {
        bottomSheetHandler.setPreviousState();
    }

    public void showCommentsFragment(MapsActivity activity) {
        Log.d(TAG, "showCommentsFragment");
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        CommentsFragment fragment = new CommentsFragment();
        //Fetch data from MarkerComment
        transaction.replace(R.id.main_parent, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        showCommentsFragment = true;
    }

    public void showProfileFragment(MapsActivity activity) {
        Log.d(TAG, "showProfileFragment");
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        ProfileFragment fragment = new ProfileFragment();
        transaction.replace(R.id.main_parent, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        showProfileFragment = true;
    }

    public boolean isShowCommentsFragment() {
        return showCommentsFragment;
    }

    public boolean isShowProfileFragment() {
        return showProfileFragment;
    }

    public void exitCommentsFragment(){
        Log.d(TAG, "exitCommentsFragment");
        showBottomSheet();
        showCommentsFragment = false;
    }

    public void exitProfileFragment() {
        Log.d(TAG, "exitProfileFragment");
        showProfileFragment = false;
    }

    @Override
    public void refreshContent(Integer id) {
        Log.d(TAG, "refreshContent");
        //getting marker content
        networkCommunication.startGettingMarkerContent(id);
    }

    public void startSignInActivity(Activity activity){
        Log.d(TAG, "startSignInActivity");
        Intent intent = new Intent(activity, SignInActivity.class);
        activity.startActivityForResult(intent, AccountInfo.REQUEST_SIGN);
    }

    public void addMarkerToMap() {
        Log.d(TAG, "addMarkerToMap");
        //add marker to center
        mapCallback.addMarkerToCenter();
        bottomSheetHandler.clearOldContent();
    }

    @Override
    public boolean isAdditionMarker() {
        Log.d(TAG, "isAdditionMarker: check.");
        return mapCallback.isAdditionMarker();
    }

    public void cancelAdditionMarker() {
        Log.d(TAG, "cancelAdditionMarker");
        mapCallback.deleteMarkerToCenter();
        bottomSheetHandler.hideBottomSheet();
    }

    public int doneAdditionMarker(NetworkInfo networkInfo) {
        Log.d(TAG, "doneAdditionMarker: invoke.");
        networkCommunication.setNetworkInfo(networkInfo);
        if (NetworkCommunication.checkNetworkConnectivity(networkInfo)) {
            if (bottomSheetHandler.isNotEmptyPhotos()) {
                if (mapCallback.availablePlace() == false) return CHOICE_OTHER_PLACE;
                //send data photos and comments to Server
                bottomSheetHandler.blockHide();
                networkCommunication.sendMarkerContent(accountInfo,
                        bottomSheetHandler.getMarkerContent(), mapCallback.getCenterMarkerLatLng());
                return ADDITION_SUCCESS;
            } else {
                Log.d(TAG, "doneAdditionMarker: empty photo.");
                return EMPTY_PHOTO;
            }
        } else{
            Log.d(TAG, "doneAdditionMarker: not available internet.");
            return INTERNET_NOT_AVAILABLE;
        }
    }

    public void addComments(List<CardContent> addedComments) {
        Log.d(TAG, "addComments");
        bottomSheetHandler.addComments(addedComments);
    }

    public void sendComment(CardContent cardContent) {
        Log.d(TAG, "sendComment");
        bottomSheetHandler.addComment(cardContent);
        networkCommunication.sendMarkerContent(accountInfo, bottomSheetHandler.getMarkerContent(),
                null);
        bottomSheetHandler.clearAddedComments();
    }

    public void showDeviceLocation(boolean showLocation) {
        Log.d(TAG, "showDeviceLocation");
        mapCallback.showDeviceLocation(showLocation);
    }

    public void saveState() {
        mapCallback.saveStateCamera();
    }

    public void stopNetwork() {
        networkCommunication.stopNetworkWork();
    }

    public void setNetworkInfo(NetworkInfo networkInfo) {
        networkCommunication.setNetworkInfo(networkInfo);
    }

    public void blockMap() {
        Log.d(TAG, "blockMap");
        mapCallback.block();
    }

    public void unblockMap(){
        Log.d(TAG, "unblockMap");
        mapCallback.unblock();
    }

    public void dropHandler() {
        networkCommunication.dropHandler();
    }

    public void refreshBottomSheet(BottomSheetBehavior bottomSheetBehavior) {
        bottomSheetHandler.setBottomSheetBehavior(bottomSheetBehavior);
    }

    public void refreshMap(SupportMapFragment mapFragment) {
        mapCallback.uploadMap(mapFragment);
    }

    public void refreshHandler() {
        majorHandler = new Handler(this);
        networkCommunication.refreshHandler(majorHandler);
    }

    public List<CardContent> getCardContentList() {
        return bottomSheetHandler.getComments();
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }
}