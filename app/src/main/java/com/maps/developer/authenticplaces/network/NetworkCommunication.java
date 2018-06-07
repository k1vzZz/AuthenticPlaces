package com.maps.developer.authenticplaces.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.maps.developer.authenticplaces.model.InputInfoMarker;
import com.maps.developer.authenticplaces.model.MarkerLatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class NetworkCommunication {

    private static final String TAG = NetworkCommunication.class.getSimpleName();

    public static final int REFRESH_MARKERS = 1;

    private static NetworkInfo networkInfo;
    private final ScheduledExecutorService service;
    private final Handler handler;
    private GetterMarkerContent getterMarkerContent;

    public NetworkCommunication(Handler handler, NetworkInfo networkInfo) {
        this.handler = handler;
        //may be null
        NetworkCommunication.networkInfo = networkInfo;
        service = Executors.newScheduledThreadPool(2);
    }

    public static boolean checkNetworkConnectivity(){
        Log.d(TAG, "checkNetworkConnectivity.");
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    public void startNetworkWork(){
        GetterMarkerLocation getterMarkerLocation = new GetterMarkerLocation(handler);
        service.scheduleAtFixedRate(getterMarkerLocation,0, 15, TimeUnit.SECONDS);
    }

    public void stopNetworkWork(){
        service.shutdownNow();
    }

    public void startGettingMarkerContent(){
        if (getterMarkerContent == null){
            getterMarkerContent = new GetterMarkerContent();
        }
        service.submit(getterMarkerContent);
    }
}