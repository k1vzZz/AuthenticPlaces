package com.maps.developer.authenticplaces.network;

import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.maps.developer.authenticplaces.utils.StringUtils;
import com.maps.developer.authenticplaces.model.input.InputInfoLatLngMarker;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetterMarkerLocation implements Runnable {

    private static final String TAG = GetterMarkerLocation.class.getSimpleName();

    private Handler handler;
    private NetworkInfo networkInfo;

    public GetterMarkerLocation(Handler handler) {
        this.handler = handler;
    }

    public void setNetworkInfo(NetworkInfo networkInfo) {
        this.networkInfo = networkInfo;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        if (NetworkCommunication.checkNetworkConnectivity(networkInfo)){
            requestForGetMarkersLocation();
        }
    }

    private void requestForGetMarkersLocation() {
        Log.d(TAG, "requestForGetMarkersLocation: get coordinates");
        HttpsURLConnection connection = null;
        try {
            URL url = new URL("https://authenticplaces.herokuapp.com/markers");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                String inputJson = StringUtils.readStream(connection.getInputStream());
                Log.d(TAG, "Receive json: " + inputJson);
                InputInfoLatLngMarker infoMarker = InputInfoLatLngMarker.createFromJSON(inputJson);
                Log.d(TAG, "InputInfoLatLngMarker: " + infoMarker);
                refreshMarkers(infoMarker);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, "IOException " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void refreshMarkers(InputInfoLatLngMarker inputInfoLatLngMarker){
        Message message = handler.obtainMessage();
        message.what = NetworkCommunication.REFRESH_MARKERS;
        message.obj = inputInfoLatLngMarker;
        handler.sendMessage(message);
    }
}
