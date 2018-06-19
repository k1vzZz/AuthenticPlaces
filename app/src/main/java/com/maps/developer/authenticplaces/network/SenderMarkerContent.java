package com.maps.developer.authenticplaces.network;

import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.maps.developer.authenticplaces.content.MarkerContent;
import com.maps.developer.authenticplaces.content.MarkerPhoto;
import com.maps.developer.authenticplaces.model.output.OutputInfoMarker;
import com.maps.developer.authenticplaces.utils.ConverterUtils;
import com.maps.developer.authenticplaces.utils.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class SenderMarkerContent implements Runnable{

    private static final String TAG = SenderMarkerContent.class.getSimpleName();

    private Handler handler;
    private NetworkInfo networkInfo;
    private OutputInfoMarker outputInfoMarker;
    private List<MarkerPhoto> addedPhotos;

    public SenderMarkerContent(Handler handler) {
        this.handler = handler;
    }

    public void setNetworkInfo(NetworkInfo networkInfo) {
        this.networkInfo = networkInfo;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setOutputInfoMarker(OutputInfoMarker outputInfoMarker) {
        this.outputInfoMarker = outputInfoMarker;
    }

    public void setImages(List<MarkerPhoto> addedPhotos) {
        if (addedPhotos == null || addedPhotos.isEmpty()) return;
        this.addedPhotos = addedPhotos;
    }

    private void convertImages(){
        if (addedPhotos != null) {
            for (MarkerPhoto markerPhoto : addedPhotos) {
                String image = ConverterUtils.getStringFromBitmap(markerPhoto.getImage());
                outputInfoMarker.addPhoto(image);
                markerPhoto.setImage(null);
            }
        }
    }

    @Override
    public void run() {
        if (NetworkCommunication.checkNetworkConnectivity(networkInfo)){
            convertImages();
            Log.d(TAG, "sendMarkerContent: " + outputInfoMarker);
            sendData(outputInfoMarker.getJson());
        }
        addedPhotos = null;
    }

    private void sendData(String outputJson) {
        Log.d(TAG, "sendData: send marker content");
        HttpsURLConnection connection = null;
        try {
            URL url = createURL();
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            StringUtils.writeToStream(connection.getOutputStream(), outputJson);
            connection.connect();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                String inputJson = StringUtils.readStream(connection.getInputStream());
                Log.d(TAG, "Receive json: " + inputJson);
                handleReceiveJson(inputJson);
            }
        } catch (IOException e){
            e.printStackTrace();
            Log.w(TAG, "IOException: catch: " + e.getMessage());
            sendToUiThread(NetworkCommunication.SEND_ERROR);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void handleReceiveJson(String inputJson){
        Log.d(TAG, "handleReceiveJson");
        Gson gson = new Gson();
        Integer response = gson.fromJson(inputJson, Integer.class);
        sendToUiThread(response);
    }

    private void sendToUiThread(Integer response){
        Log.d(TAG, "sendToUiThread: response: " + response);
        Message message = handler.obtainMessage();
        switch (response){
            case NetworkCommunication.UPDATE_SUCCESS:
                message.what = NetworkCommunication.UPDATE_SUCCESS;
                break;
            case NetworkCommunication.SEND_ERROR:
                message.what = NetworkCommunication.SEND_ERROR;
                break;
            default:
                message.what = NetworkCommunication.ADDITION_SUCCESS;
                message.obj = response;
        }
        handler.sendMessage(message);
    }

    private URL createURL() throws MalformedURLException {
        Integer idMarker = outputInfoMarker.getIdMarker();
        URL url;
        if (idMarker.equals(MarkerContent.DEFAULT_IDENTIFIER)){
            Log.d(TAG, "createURL: new marker");
            url = new URL("https://authenticplaces.herokuapp.com/markers/new");
        } else {
            Log.d(TAG, "createURL: update content marker");
            url = new URL("https://authenticplaces.herokuapp.com/markers/" + idMarker + "/update");
        }
        return url;
    }
}