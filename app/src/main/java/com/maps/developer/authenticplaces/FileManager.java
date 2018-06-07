package com.maps.developer.authenticplaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileManager {

    public Bitmap bitmapFromUri(Context context, Uri uri){
        Bitmap result = null;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            result = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

}
