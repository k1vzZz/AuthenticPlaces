package com.maps.developer.authenticplaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.maps.developer.authenticplaces.content.CardContent;
import com.maps.developer.authenticplaces.content.ImageMarkerAdapter;
import com.maps.developer.authenticplaces.content.MarkerContent;
import com.maps.developer.authenticplaces.content.MarkerPhoto;
import com.maps.developer.authenticplaces.interfaces.OnBottomContentListener;
import com.maps.developer.authenticplaces.model.input.InputContentMarker;
import com.maps.developer.authenticplaces.utils.ConverterUtils;

import java.util.List;

public class BottomSheetHandler implements GoogleMap.OnMarkerClickListener{

    private static final String TAG = BottomSheetHandler.class.getSimpleName();

    private BottomSheetBehavior bottomSheetBehavior;
    private MarkerContent markerContent;
    private OnBottomContentListener onBottomContentListener;
    private RecyclerView.Adapter adapter;
    private int previousState = 0;

    public BottomSheetHandler(Context context, OnBottomContentListener onBottomContentListener,
                              BottomSheetBehavior bottomSheetBehavior) {
        this.bottomSheetBehavior = bottomSheetBehavior;
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        this.onBottomContentListener = onBottomContentListener;
        markerContent = new MarkerContent();
        adapter = new ImageMarkerAdapter(context, markerContent.getPhotos());
    }

    public void setPhotos(List<MarkerPhoto> markerPhotos){
        markerContent.clearPhotos();
        markerContent.addPhotos(markerPhotos);
        adapter.notifyDataSetChanged();
    }

    public void setComments(List<CardContent> comments){
        markerContent.clearComments();
        markerContent.receiveComments(comments);
    }

    public void setStateBottomSheet(int state){
        Log.d(TAG, "setStateBottomSheet: " + state);
        previousState = bottomSheetBehavior.getState();
        bottomSheetBehavior.setState(state);
    }

    public boolean hideBottomSheet(){
        Log.d(TAG, "hideBottomSheet");
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                previousState = BottomSheetBehavior.STATE_COLLAPSED;
            } else {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    previousState = BottomSheetBehavior.STATE_EXPANDED;
                }
            }
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            return true;
        } else {
            return false;
        }
    }

    public void addMarkerPhoto(String uri, String title, Bitmap image){
        Log.d(TAG, "addMarkerPhoto");
        MarkerPhoto markerPhoto = new MarkerPhoto(uri, title);
        markerPhoto.setImage(image);
        markerContent.addPhoto(markerPhoto);
        adapter.notifyDataSetChanged();
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClick");
        //request (Integer) marker.getTag()
        if (onBottomContentListener.isAdditionMarker() == false) {
            Log.d(TAG, "onMarkerClick: not additional marker");
            Integer id = (Integer) marker.getTag();
            if (id.equals(getIdMarker()) == false) {
                setIdMarker(id);
                onBottomContentListener.refreshContent(id);
            }
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        return true;
    }

    public boolean isNotEmptyPhotos() {
        Log.d(TAG, "isNotEmptyPhotos");
        return markerContent.isNotEmptyPhotos();
    }

    public void setPreviousState() {
        Log.d(TAG, "setPreviousState");
        bottomSheetBehavior.setState(previousState);
    }

    public void addComments(List<CardContent> addedComments) {
        Log.d(TAG, "addComments");
        markerContent.addComments(addedComments);
    }

    public List<CardContent> getComments() {
        return markerContent.getComments();
    }

    public void clearOldContent() {
        Log.d(TAG, "clearOldContent");
        markerContent.clearPhotos();
        adapter.notifyDataSetChanged();
        markerContent.clearComments();
        markerContent.setIdMarker(MarkerContent.DEFAULT_IDENTIFIER);
        setStateBottomSheet(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public MarkerContent getMarkerContent() {
        return markerContent;
    }

    private void setIdMarker(Integer id) {
        markerContent.setIdMarker(id);
    }

    private Integer getIdMarker(){
        return markerContent.getIdMarker();
    }

    public void addComment(CardContent cardContent) {
        markerContent.addComment(cardContent);
    }

    public void clearAddedComments() {
        markerContent.clearAddedComments();
    }

    public void clearAddedPhotos() {
        markerContent.clearAddedPhotos();
    }

    public void refreshContent(InputContentMarker contentMarker) {
        setPhotos(ConverterUtils.convertToMarkerPhotoList(contentMarker));
        setComments(ConverterUtils.convertToMarkerCommentList(contentMarker));
    }

    public void blockHide() {
        bottomSheetBehavior.setHideable(false);
    }

    public void unblockHide(){
        bottomSheetBehavior.setHideable(true);
    }

    public void setBottomSheetBehavior(BottomSheetBehavior bottomSheetBehavior) {
        this.bottomSheetBehavior = bottomSheetBehavior;
    }
}