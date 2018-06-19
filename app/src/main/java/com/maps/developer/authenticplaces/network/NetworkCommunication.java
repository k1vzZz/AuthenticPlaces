package com.maps.developer.authenticplaces.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.maps.developer.authenticplaces.account.AccountInfo;
import com.maps.developer.authenticplaces.content.MarkerContent;
import com.maps.developer.authenticplaces.model.output.OutputInfoMarker;
import com.maps.developer.authenticplaces.utils.ConverterUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class NetworkCommunication {

    private static final String TAG = NetworkCommunication.class.getSimpleName();

    public static final int REFRESH_MARKERS = 1;
    public static final int ADDITION_SUCCESS = 2;
    public static final int SEND_ERROR = -1;
    public static final int UPDATE_SUCCESS = -5;
    public static final int CONTENT_MARKER = 3;

    private final ScheduledExecutorService service;
    private NetworkInfo networkInfo;
    private Handler handler;
    private ScheduledFuture future;
    private GetterMarkerLocation getterMarkerLocation;
    private GetterMarkerContent getterMarkerContent;
    private SenderMarkerContent senderMarkerContent;

    public NetworkCommunication(Handler handler) {
        this.handler = handler;
        service = Executors.newScheduledThreadPool(3);
    }

    public void setNetworkInfo(NetworkInfo networkInfo) {
        this.networkInfo = networkInfo;
        if (getterMarkerLocation != null){
            getterMarkerLocation.setNetworkInfo(networkInfo);
        }
        if (getterMarkerContent != null){
            getterMarkerContent.setNetworkInfo(networkInfo);
        }
        if (senderMarkerContent != null){
            senderMarkerContent.setNetworkInfo(networkInfo);
        }
    }

    public static boolean checkNetworkConnectivity(NetworkInfo networkInfo){
        Log.d(TAG, "checkNetworkConnectivity.");
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    public void startNetworkWork(){
        Log.d(TAG, "startNetworkWork");
        if (getterMarkerLocation == null){
            getterMarkerLocation = new GetterMarkerLocation(handler);
            getterMarkerLocation.setNetworkInfo(networkInfo);
        }
        future = service.scheduleAtFixedRate(getterMarkerLocation,
                0, 15, TimeUnit.SECONDS);
    }

    public void stopNetworkWork(){
        Log.d(TAG, "stopNetworkWork");
        future.cancel(true);
    }

    public void startGettingMarkerContent(Integer id){
        if (getterMarkerContent == null){
            getterMarkerContent = new GetterMarkerContent(handler);
            getterMarkerContent.setNetworkInfo(networkInfo);
        }
        getterMarkerContent.setIdMarker(id);
        service.submit(getterMarkerContent);
    }

    public void sendMarkerContent(AccountInfo account, MarkerContent markerContent, LatLng latLng) {
        Log.d(TAG, "sendMarkerContent");
        if (senderMarkerContent == null){
            senderMarkerContent = new SenderMarkerContent(handler);
            senderMarkerContent.setNetworkInfo(networkInfo);
        }
        OutputInfoMarker outputInfoMarker = ConverterUtils.convertFromMarkerContent(markerContent);
        outputInfoMarker.setIdentifierClient(account.getId());
        outputInfoMarker.setLogin(account.getEmail());
        outputInfoMarker.setUrlImage(account.getPhotoUrl().toString());
        if (latLng != null) {
            outputInfoMarker.setLatitude(latLng.latitude);
            outputInfoMarker.setLongitude(latLng.longitude);
        }
        senderMarkerContent.setOutputInfoMarker(outputInfoMarker);
        senderMarkerContent.setImages(markerContent.getAddedPhotos());
        service.submit(senderMarkerContent);
    }

    public void refreshHandler(Handler handler) {
        this.handler = handler;
        if (getterMarkerLocation != null){
            getterMarkerLocation.setHandler(handler);
        }
        if (getterMarkerContent != null){
            getterMarkerContent.setHandler(handler);
        }
        if (senderMarkerContent != null){
            senderMarkerContent.setHandler(handler);
        }
    }

    public void dropHandler(){
        this.handler = null;
        if (getterMarkerLocation != null){
            getterMarkerLocation.setHandler(null);
        }
        if (getterMarkerContent != null){
            getterMarkerContent.setHandler(null);
        }
        if (senderMarkerContent != null){
            senderMarkerContent.setHandler(null);
        }
    }
}