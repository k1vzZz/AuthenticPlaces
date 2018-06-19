package com.maps.developer.authenticplaces.model.input;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

public class InputInfoLatLngMarker {

    private List<MarkerLatLng> markerLatLngList;

    public InputInfoLatLngMarker() {
    }

    public InputInfoLatLngMarker(List<MarkerLatLng> markerLatLngList) {
        this.markerLatLngList = markerLatLngList;
    }

    public static InputInfoLatLngMarker createFromJSON(String json){
        Gson gson = new Gson();
        InputInfoLatLngMarker infoMarker = new InputInfoLatLngMarker();
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
        return "InputInfoLatLngMarker{" +
                "markerLatLngList=" + markerLatLngList +
                '}';
    }
}
