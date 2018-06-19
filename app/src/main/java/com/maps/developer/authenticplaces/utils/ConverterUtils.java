package com.maps.developer.authenticplaces.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;
import com.maps.developer.authenticplaces.content.CardContent;
import com.maps.developer.authenticplaces.content.MarkerContent;
import com.maps.developer.authenticplaces.content.MarkerPhoto;
import com.maps.developer.authenticplaces.model.input.InputComment;
import com.maps.developer.authenticplaces.model.input.InputContentMarker;
import com.maps.developer.authenticplaces.model.input.InputSnapshot;
import com.maps.developer.authenticplaces.model.output.OutputInfoMarker;

//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.codec.binary.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ConverterUtils {

    private static final int COMPRESSION_QUALITY = 70;

    private static final String TAG = ConverterUtils.class.getSimpleName();

    public static OutputInfoMarker convertFromMarkerContent(MarkerContent markerContent) {
        Log.d(TAG, "convertFromMarkerContent");
        List<CardContent> comments = markerContent.getAddedComments();
        OutputInfoMarker outputInfoMarker = new OutputInfoMarker();
        outputInfoMarker.setIdMarker(markerContent.getIdMarker());
        if (comments != null) {
            for (CardContent cardContent : comments) {
                outputInfoMarker.addCommentText(cardContent.getContent());
                outputInfoMarker.addTime(cardContent.getTime());
            }
        }
        return outputInfoMarker;
    }

    public static String getStringFromBitmap(Bitmap bitmapPicture) {
        Log.d(TAG, "getStringFromBitmap");
        byte[] byteArray = null;
        try(ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream()) {
            bitmapPicture.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY,
                    byteArrayBitmapStream);
            byteArray = byteArrayBitmapStream.toByteArray();
        } catch (IOException e){
            e.printStackTrace();
        }
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static List<MarkerPhoto> convertToMarkerPhotoList(InputContentMarker contentMarker) {
        Log.d(TAG, "convertToMarkerPhotoList");
        List<MarkerPhoto> markerPhotoList = new ArrayList<>();
        if (contentMarker.getSnapshots() != null) {
            for (InputSnapshot snapshot : contentMarker.getSnapshots()) {
                String uri = snapshot.getUrl();
                MarkerPhoto markerPhoto = new MarkerPhoto(uri, null);
                markerPhotoList.add(markerPhoto);
            }
        }
        return markerPhotoList;
    }

    public static List<CardContent> convertToMarkerCommentList(InputContentMarker contentMarker) {
        Log.d(TAG, "convertToMarkerCommentList");
        List<CardContent> cardContentList = new ArrayList<>();
        if (contentMarker.getComments() != null) {
            for (InputComment comment : contentMarker.getComments()) {
                String author = comment.getLogin();
                String text = comment.getText();
                Timestamp time = new Timestamp(comment.getUploadDate());
                Uri imageUri = Uri.parse(comment.getUrlImageAuthor());
                CardContent cardContent = new CardContent(author, text, time, imageUri);
                cardContentList.add(cardContent);
            }
        }
        return cardContentList;
    }
}
