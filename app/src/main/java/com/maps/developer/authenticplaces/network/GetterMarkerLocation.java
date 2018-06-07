package com.maps.developer.authenticplaces.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.maps.developer.authenticplaces.StringUtils;
import com.maps.developer.authenticplaces.model.InputInfoMarker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetterMarkerLocation implements Runnable {

    private static final String TAG = GetterMarkerLocation.class.getSimpleName();

    private final Handler handler;

    public GetterMarkerLocation(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        if (NetworkCommunication.checkNetworkConnectivity()){
            try {
                requestForGetMarkers();
            } catch (IOException e) {
                e.printStackTrace();
                Log.w(TAG, "IOException " + e.getMessage());
            }
        }
    }

    private void requestForGetMarkers() throws IOException {
        Log.d(TAG, "requestForGetMarkers: get coordinates");
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String inputJson = null;
        try {
            URL url = new URL("https://authenticplaces.herokuapp.com/markers");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.connect();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                stream = connection.getInputStream();
                inputJson = StringUtils.readStream(stream);
                Log.d(TAG, "Receive json: " + inputJson);
                InputInfoMarker infoMarker = parseJson(inputJson, InputInfoMarker.class);
                Log.d(TAG, "InputInfoMarker: " + infoMarker);
                refreshMarkers(infoMarker);
            }
        } finally {
            if (stream != null){
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void refreshMarkers(InputInfoMarker inputInfoMarker){
        Message message = handler.obtainMessage();
        message.what = NetworkCommunication.REFRESH_MARKERS;
        message.obj = inputInfoMarker;
        handler.sendMessage(message);
    }

    private <T> T parseJson(String json, Class<T> clazz){
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }
}
