package com.maps.developer.authenticplaces.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class SharedPreferenceUtils {

    private static final String TAG = SharedPreferenceUtils.class.getSimpleName();

    private static final String ZOOM = "zoom_state";
    private static final String LATITUDE = "latitude_state";
    private static final String LONGITUDE = "longitude_state";

    public static void saveState(Context context, int resourceFile,
                          float zoom, Double latitude, Double longitude){
        Log.d(TAG, "saveState");
        SharedPreferences.Editor editor = context.getSharedPreferences(
                context.getResources().getString(resourceFile), Context.MODE_PRIVATE).edit();
        editor.putFloat(ZOOM, zoom);
        editor.putString(LATITUDE, latitude.toString());
        editor.putString(LONGITUDE, longitude.toString());
        editor.apply();
    }

    public static LatLng getLatLng(Context context, int resourceFile){
        Log.d(TAG, "getLatLng");
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(resourceFile), Context.MODE_PRIVATE);
        String latitudeString = preferences.getString(LATITUDE, "0.0");
        String longitudeString = preferences.getString(LONGITUDE, "0.0");
        Double latitude = Double.parseDouble(latitudeString);
        Double longitude = Double.parseDouble(longitudeString);
        if (latitude == 0.0 || longitude == 0.0){
            return null;
        } else {
            return new LatLng(latitude, longitude);
        }
    }

    public static float getZoom(Context context, int resourceFile){
        Log.d(TAG, "getZoom");
        SharedPreferences preferences = context.getSharedPreferences(
                context.getResources().getString(resourceFile), Context.MODE_PRIVATE);
        return preferences.getFloat(ZOOM, -1.0f);
    }
}
