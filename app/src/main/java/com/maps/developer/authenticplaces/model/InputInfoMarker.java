package com.maps.developer.authenticplaces.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

public class InputInfoMarker {

    private List<MarkerLatLng> markerLatLngList;

    public InputInfoMarker() {
    }

    public InputInfoMarker(List<MarkerLatLng> markerLatLngList) {
        this.markerLatLngList = markerLatLngList;
    }

    public static InputInfoMarker createFromJSON(String json){
        Gson gson = new GsonBuilder().create();
        InputInfoMarker infoMarker = new InputInfoMarker();
        infoMarker.markerLatLngList = Arrays.asList(gson.fromJson(json, MarkerLatLng[].class));
        return infoMarker;
    }

    public List<MarkerLatLng> getMarkerLatLngList() {
        return markerLatLngList;
    }

    public void setMarkerLatLngList(List<MarkerLatLng> markerLatLngList) {
        this.markerLatLngList = markerLatLngList;
    }

    @Override
    public String toString() {
        return "InputInfoMarker{" +
                "markerLatLngList=" + markerLatLngList +
                '}';
    }
}
