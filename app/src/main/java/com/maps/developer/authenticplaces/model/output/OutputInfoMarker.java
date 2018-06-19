package com.maps.developer.authenticplaces.model.output;

import com.google.gson.Gson;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OutputInfoMarker {

    private Integer idMarker;
    private String identifierClient;
    private String urlImage;
    private String login;
    private List<String> photos;
    private List<String> commentsText;
    private List<Long> times;
    private Double latitude;
    private Double longitude;

    public OutputInfoMarker() {
        photos = new ArrayList<>();
        commentsText = new ArrayList<>();
        times = new ArrayList<>();
    }

    public String getJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public Integer getIdMarker() {
        return idMarker;
    }

    public void setIdMarker(Integer idMarker) {
        this.idMarker = idMarker;
    }

    public String getIdentifierClient() {
        return identifierClient;
    }

    public void setIdentifierClient(String identifierClient) {
        this.identifierClient = identifierClient;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public List<String> getCommentsText() {
        return commentsText;
    }

    public void setCommentsText(List<String> commentsText) {
        this.commentsText = commentsText;
    }

    public List<Long> getTimes() {
        return times;
    }

    public void setTimes(List<Long> times) {
        this.times = times;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void addPhoto(String photo){
        photos.add(photo);
    }

    public void addCommentText(String comment){
        commentsText.add(comment);
    }

    public void addTime(Timestamp time){
        times.add(time.getTime());
    }

    @Override
    public String toString() {
        return "OutputInfoMarker{" +
                "idMarker=" + idMarker +
                ", identifierClient='" + identifierClient + '\'' +
                ", photos=" + photos +
                ", commentsText=" + commentsText +
                ", times=" + times +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
