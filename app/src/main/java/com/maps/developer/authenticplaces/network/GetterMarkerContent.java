package com.maps.developer.authenticplaces.network;

import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.maps.developer.authenticplaces.model.input.InputContentMarker;
import com.maps.developer.authenticplaces.utils.StringUtils;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetterMarkerContent implements Runnable{

    private static final String TAG = GetterMarkerContent.class.getSimpleName();

    private Handler handler;
    private NetworkInfo networkInfo;

    private Integer idMarker;

    public GetterMarkerContent(Handler handler) {
        this.handler = handler;
    }

    public void setIdMarker(Integer idMarker) {
        this.idMarker = idMarker;
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
            requestForGetMarkerContent();
        }
    }

    private void requestForGetMarkerContent() {
        Log.d(TAG, "requestForGetMarkerContent");
        HttpsURLConnection connection = null;
        try {
            URL url = new URL("https://authenticplaces.herokuapp.com/markers/" + idMarker);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                String inputJson = StringUtils.readStream(connection.getInputStream());
                Log.d(TAG, "Receive marker content: " + inputJson);
                InputContentMarker contentMarker = InputContentMarker.createFromJSON(inputJson);
                refreshMarkerContent(contentMarker);
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

    private void refreshMarkerContent(InputContentMarker contentMarker){
        Message message = handler.obtainMessage();
        message.what = NetworkCommunication.CONTENT_MARKER;
        message.obj = contentMarker;
        handler.sendMessage(message);
    }
}
