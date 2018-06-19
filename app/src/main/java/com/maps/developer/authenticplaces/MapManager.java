package com.maps.developer.authenticplaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maps.developer.authenticplaces.interfaces.MapCallback;
import com.maps.developer.authenticplaces.model.input.InputInfoLatLngMarker;
import com.maps.developer.authenticplaces.model.input.MarkerLatLng;
import com.maps.developer.authenticplaces.utils.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapManager implements OnMapReadyCallback, MapCallback, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener {

    private static final String TAG = MapManager.class.getSimpleName();

    private static final float DEFAULT_ZOOM = 15f;
    private static final double RADIUS_CIRCLE = 150d;

    private Location lastLocation;
    private boolean startPosition = false;
    private Circle outerCircle;
    private Circle innerCircle;
    private Map<Integer, Marker> markerMap;
    private Marker centerMarker;
    private List<Circle> circleList;

    private GoogleMap mMap;
    private final BottomSheetHandler bottomSheetHandler;
    private final Context context;
    private final int resourceFile;
    private boolean showLocation;

    public MapManager(Context context, BottomSheetHandler bottomSheetHandler, int resourceFile) {
        this.context = context;
        this.bottomSheetHandler = bottomSheetHandler;
        this.resourceFile = resourceFile;
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
        if (getStateCamera()){
            startPosition = true;
        }
        mMap.setOnMarkerClickListener(bottomSheetHandler);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
        refreshMap();
    }

    private void refreshMap() {
        markerMap = null;
        if (centerMarker != null){
            addMarkerToCenter();
        }
        if (outerCircle != null){
            createCurrentCircle(outerCircle.getCenter());
        }
    }

    @Override
    public void refreshMarkers(InputInfoLatLngMarker inputInfoLatLngMarker){
        Log.d(TAG, "refreshMarkers");
        if (markerMap == null){
            markerMap = new HashMap<>();
        }
        List<MarkerLatLng> markerLatLngList = inputInfoLatLngMarker.getMarkerLatLngList();
        if (markerLatLngList == null){
            clearAllMarkers();
            return;
        }
        for (MarkerLatLng markerLatLng : markerLatLngList){
            Integer idMarker = markerLatLng.getId();
            LatLng latLng = new LatLng(markerLatLng.getLatitude(), markerLatLng.getLongitude());
            if (markerMap.containsKey(idMarker)){
                markerMap.get(idMarker).setPosition(latLng);
            } else {
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
                marker.setTag(markerLatLng.getId());
                changeMarkerIcon(marker, R.drawable.ic_marker_icon);
                if (centerMarker != null){
                    addMarkerRegions(marker);
                }
                markerMap.put(markerLatLng.getId(), marker);
            }
        }
        removeExtraMarkers(markerLatLngList);
    }

    private void changeMarkerIcon(Marker marker, int drawableResource) {
        Drawable iconDrawable = context.getResources().getDrawable(drawableResource);
        BitmapDescriptor icon = getMarkerIconFromDrawable(iconDrawable);
        marker.setIcon(icon);
        marker.setAnchor(0.5f, 1f);
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void removeExtraMarkers(List<MarkerLatLng> markerLatLngList) {
        Log.d(TAG, "removeExtraMarkers");
        if (markerMap == null) return;
        Iterator iterator = markerMap.keySet().iterator();
        while (iterator.hasNext()){
            boolean existence = false;
            Integer key = (Integer) iterator.next();
            for (MarkerLatLng markerLatLng : markerLatLngList){
                if (key.equals(markerLatLng.getId())){
                    existence = true;
                    break;
                }
            }
            if (existence == false){
                markerMap.get(key).remove();
                iterator.remove();
            }
        }
    }

    private void clearAllMarkers() {
        Log.d(TAG, "clearAllMarkers");
        if (markerMap == null) return;
        Iterator iterator = markerMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer, Marker> entry = (Map.Entry<Integer, Marker>) iterator.next();
            markerMap.get(entry.getKey()).remove();
            iterator.remove();
        }
    }

    private void createStartPosition() {
        if (lastLocation != null){
            Log.d(TAG, "createStartPosition: move camera.");
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
            startPosition = true;
        }
    }

    public void saveStateCamera(){
        Log.d(TAG, "saveStateCamera");
        LatLng latLng = mMap.getCameraPosition().target;
        Double latitude = latLng.latitude;
        Double longitude = latLng.longitude;
        float zoom = mMap.getCameraPosition().zoom;
        SharedPreferenceUtils.saveState(context, resourceFile, zoom, latitude, longitude);
    }

    private boolean getStateCamera(){
        Log.d(TAG, "getStateCamera");
        if (startPosition == false) {
            float zoom = SharedPreferenceUtils.getZoom(context, resourceFile);
            LatLng latLng = SharedPreferenceUtils.getLatLng(context, resourceFile);
            if (zoom == -1.0f || latLng == null) {
                return false;
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
        return true;
    }

    public void updateCurrentLocation(Location location){
        Log.d(TAG, "updateCurrentLocation");
        lastLocation = location;
        if (mMap != null){
            if (startPosition == false){
                createStartPosition();
            } else {
                if (showLocation){
                    showDeviceLocation(false);
                }
            }
            LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
            if (outerCircle == null) {
                createCurrentCircle(center);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, DEFAULT_ZOOM));
            } else {
                outerCircle.setCenter(center);
                innerCircle.setCenter(center);
            }
        }
    }

    private void createCurrentCircle(LatLng center){
        CircleOptions circleOptionsOuter = createCircle(center, 15,
                Color.argb(150, 47, 155, 240),
                2, Color.rgb(53, 153, 195));
        outerCircle = mMap.addCircle(circleOptionsOuter);

        CircleOptions circleOptionsInner = createCircle(center, 7,
                Color.argb(150, 47, 68, 240),
                2, Color.rgb(0, 0, 0));
        innerCircle = mMap.addCircle(circleOptionsInner);
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

    public void addMarkerToCenter() {
        Log.d(TAG, "addMarkerToCenter");
        addMarkerRegions(null);
        LatLng center = mMap.getCameraPosition().target;
        centerMarker = mMap.addMarker(new MarkerOptions()
                .position(center)
                .anchor(0.5f, 1f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.image_new_marker)));
    }

    private void addMarkerRegions(Marker singleMarker) {
        Log.d(TAG, "addMarkerRegions");
        if (markerMap == null) return;
        if (circleList == null){
            circleList = new ArrayList<>();
        }
        if (singleMarker == null) {
            for (Marker marker : markerMap.values()) {
                LatLng position = marker.getPosition();
                CircleOptions circleOptions = createCircle(position, 150,
                        Color.argb(100, 47, 155, 240),
                        2, Color.rgb(53, 153, 195));
                circleList.add(mMap.addCircle(circleOptions));
            }
        } else {
            CircleOptions circleOptions = createCircle(singleMarker.getPosition(), RADIUS_CIRCLE,
                    Color.argb(30, 47, 155, 240),
                    2, Color.rgb(53, 153, 195));
            circleList.add(mMap.addCircle(circleOptions));
        }
    }

    @Override
    public void onCameraMove() {
        if (centerMarker != null){
            centerMarker.setPosition(mMap.getCameraPosition().target);
        }
    }

    @Override
    public void onCameraMoveStarted(int i) {
        if (i == REASON_GESTURE && centerMarker != null){
            bottomSheetHandler.setStateBottomSheet(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void onCameraIdle() {
        if (centerMarker != null){
            bottomSheetHandler.setStateBottomSheet(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public boolean isAdditionMarker(){
        Log.d(TAG, "isAdditionMarker");
        return centerMarker != null;
    }

    public void deleteMarkerToCenter() {
        Log.d(TAG, "deleteMarkerToCenter");
        removeMarkerRegions();
        centerMarker.remove();
        centerMarker = null;
    }

    private void removeMarkerRegions() {
        Log.d(TAG, "removeMarkerRegions");
        for (Circle circle : circleList){
            circle.remove();
        }
        circleList.clear();
    }

    public LatLng getCenterMarkerLatLng() {
        Log.d(TAG, "getCenterMarkerLatLng");
        return centerMarker != null ? centerMarker.getPosition() : null;
    }

    public void addMarkerSuccess(Integer idMarker) {
        Log.d(TAG, "addMarkerSuccess");
        Marker marker = mMap.addMarker(new MarkerOptions().position(centerMarker.getPosition()));
        marker.setTag(idMarker);
        changeMarkerIcon(marker, R.drawable.ic_marker_icon);
        markerMap.put(idMarker, marker);
        deleteMarkerToCenter();
    }

    public void showDeviceLocation(boolean showLocation) {
        Log.d(TAG, "showDeviceLocation");
        this.showLocation = showLocation;
        if (lastLocation != null){
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
            this.showLocation = false;
        }
    }

    @Override
    public boolean availablePlace() {
        boolean result = true;
        if (centerMarker != null && circleList != null && circleList.size() > 0){
            for (Circle circle : circleList){
                float[] distance = new float[3];
                LatLng center = centerMarker.getPosition();
                LatLng circleCenter = circle.getCenter();
                Location.distanceBetween(center.latitude, center.longitude,
                        circleCenter.latitude, circleCenter.longitude, distance);
                Log.d(TAG, "availablePlace: " + Arrays.toString(distance));
                if (distance[0] <= RADIUS_CIRCLE){
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    public void block() {
        Log.d(TAG, "block");
        mMap.setOnCameraMoveListener(null);
        mMap.setOnCameraMoveStartedListener(null);
        mMap.setOnCameraIdleListener(null);
    }

    public void unblock(){
        Log.d(TAG, "unblock");
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
    }
}