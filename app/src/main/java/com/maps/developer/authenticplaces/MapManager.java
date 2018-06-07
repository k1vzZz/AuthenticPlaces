package com.maps.developer.authenticplaces;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maps.developer.authenticplaces.interfaces.MarkerCallback;
import com.maps.developer.authenticplaces.model.InputInfoMarker;
import com.maps.developer.authenticplaces.model.MarkerLatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapManager implements OnMapReadyCallback, MarkerCallback{

    private static final String TAG = MapManager.class.getSimpleName();

    public static final float DEFAULT_ZOOM = 15f;

    private Location lastLocation;
    private boolean startPosition = false;
    private Circle outerCircle;
    private Circle innerCircle;
    private Map<Integer, Marker> markerList;

    private GoogleMap mMap;
    private final BottomSheetHandler bottomSheetHandler;
    private final Context context;

    public MapManager(Context context, BottomSheetHandler bottomSheetHandler) {
        this.context = context;
        this.bottomSheetHandler = bottomSheetHandler;
    }

    public void uploadMap(SupportMapFragment mapFragment){
        Log.d(TAG, "uploadMap");
        if (mapFragment == null) return;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        mMap = googleMap;
        mMap.setOnMarkerClickListener(bottomSheetHandler);
    }

    @Override
    public void refreshMarkers(InputInfoMarker inputInfoMarker){
        if (markerList == null){
            markerList = new HashMap<>();
        }
        List<MarkerLatLng> markerLatLngList = inputInfoMarker.getMarkerLatLngList();
        for (MarkerLatLng markerLatLng : markerLatLngList){
            Integer idMarker = markerLatLng.getId();
            LatLng latLng = new LatLng(markerLatLng.getLatitude(), markerLatLng.getLongitude());
            if (markerList.containsKey(idMarker)){
                markerList.get(idMarker).setPosition(latLng);
            } else {
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
                marker.setTag(markerLatLng.getId());
                markerList.put(markerLatLng.getId(), marker);
            }
        }
    }

    public void testMarker(){
        LatLng latLng = new LatLng(56.28251711622431, 44.085486070693925);
        mMap.addMarker(new MarkerOptions().position(latLng));
    }

    private void createStartPosition() {
        if (lastLocation != null){
            Log.d(TAG, "createStartPosition: move camera.");
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
            testMarker();
            startPosition = true;
        }
    }

    public void updateCurrentLocation(Location location){
        Log.d(TAG, "updateCurrentLocation");
        if (lastLocation == null){
            lastLocation = location;
        }
        if (mMap != null){
            if (startPosition == false){
                createStartPosition();
            }
            LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
            if (outerCircle == null) {
                CircleOptions circleOptionsOuter = createCircle(center, 15,
                        Color.argb(150, 47, 155, 240),
                        2, Color.rgb(53, 153, 195));
                outerCircle = mMap.addCircle(circleOptionsOuter);

                CircleOptions circleOptionsInner = createCircle(center, 7,
                        Color.argb(150, 47, 68, 240),
                        2, Color.rgb(0, 0, 0));
                innerCircle = mMap.addCircle(circleOptionsInner);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, DEFAULT_ZOOM));
            } else {
                outerCircle.setCenter(center);
                innerCircle.setCenter(center);
            }
        }
    }

    private CircleOptions createCircle(LatLng center, double radius,
                                       int fillColor, float strokeWidth, int strokeColor){
        Log.d(TAG, "createCircle.");
        return new CircleOptions()
                .center(center)
                .radius(radius)
                .fillColor(fillColor)
                .strokeWidth(strokeWidth)
                .strokeColor(strokeColor);
    }
}
