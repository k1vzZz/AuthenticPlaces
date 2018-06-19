package com.maps.developer.authenticplaces.interfaces;

import android.location.Location;

import com.google.android.gms.maps.SupportMapFragment;

public interface MapCallback extends MarkerCallback{

    void uploadMap(SupportMapFragment mapFragment);

    void updateCurrentLocation(Location location);

    void showDeviceLocation(boolean showLocation);

    void saveStateCamera();

    void block();

    void unblock();

}
