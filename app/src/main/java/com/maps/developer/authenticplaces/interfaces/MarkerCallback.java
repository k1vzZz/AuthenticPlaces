package com.maps.developer.authenticplaces.interfaces;

import com.google.android.gms.maps.model.LatLng;
import com.maps.developer.authenticplaces.model.input.InputInfoLatLngMarker;

public interface MarkerCallback {

    void refreshMarkers(InputInfoLatLngMarker inputInfoLatLngMarker);

    void addMarkerSuccess(Integer idMarker);

    LatLng getCenterMarkerLatLng();

    void deleteMarkerToCenter();

    boolean isAdditionMarker();

    void addMarkerToCenter();

    boolean availablePlace();
}
