package com.maps.developer.authenticplaces;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringUtils {

    private static final String TAG = StringUtils.class.getSimpleName();

    public static String readStream(InputStream stream) {
        Log.d(TAG, "readStream: receiving response");
        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }
        } catch (IOException e){
            Log.d(TAG, "readStream: Error" + e.getMessage());
        }
        return stringBuilder.toString();
    }
}
