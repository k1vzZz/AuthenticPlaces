package com.maps.developer.authenticplaces.utils;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class StringUtils {

    private static final String TAG = StringUtils.class.getSimpleName();

    public static String readStream(InputStream stream) throws IOException {
        Log.d(TAG, "readStream: receiving response");
        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    public static void writeToStream(OutputStream outputStream, String outputJson) throws IOException{
        Log.d(TAG, "writeToStream: send to Stream JSON: " + outputJson);
        try(OutputStream writer = new BufferedOutputStream(outputStream)) {
            writer.write(outputJson.getBytes());
            writer.flush();
        }
    }
}
