package com.maps.developer.authenticplaces;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class BottomSheetHandler implements GoogleMap.OnMarkerClickListener{

    private static final String TAG = BottomSheetHandler.class.getSimpleName();

    private BottomSheetBehavior bottomSheetBehavior;
    private List<MarkerPhoto> markerPhotoList;
    private RecyclerView.Adapter adapter;

    public BottomSheetHandler(Context context, BottomSheetBehavior bottomSheetBehavior) {
        this.bottomSheetBehavior = bottomSheetBehavior;
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        markerPhotoList = MarkerPhoto.getTestPhotos();
        adapter = new ImageMarkerAdapter(context, markerPhotoList);
    }

    public void setStateBottomSheet(int state){
        bottomSheetBehavior.setState(state);
    }

    public int getStateBottomSheet(){
        return bottomSheetBehavior.getState();
    }

    public boolean hideBottomSheet(){
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED
                || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            return true;
        } else {
            return false;
        }
    }

    public void addMarkerPhoto(String uri, String title){
        MarkerPhoto markerPhoto = new MarkerPhoto(uri, title);
        if (markerPhotoList.contains(markerPhoto) == false) {
            markerPhotoList.add(markerPhoto);
            adapter.notifyDataSetChanged();
        }
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //request (Integer) marker.getTag()
        return true;
    }
}
