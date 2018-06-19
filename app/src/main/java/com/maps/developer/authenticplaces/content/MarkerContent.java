package com.maps.developer.authenticplaces.content;

import java.util.ArrayList;
import java.util.List;

public class MarkerContent {

    public static final Integer DEFAULT_IDENTIFIER = -1;

    private Integer idMarker = DEFAULT_IDENTIFIER;
    private List<CardContent> comments;
    private List<MarkerPhoto> photos;
    private List<CardContent> addedComments;
    private List<MarkerPhoto> addedPhotos;

    public MarkerContent() {
        photos = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public List<MarkerPhoto> getPhotos() {
        return photos;
    }

    public void addPhoto(MarkerPhoto markerPhoto) {
        if (photos.contains(markerPhoto) == false) {
            if (addedPhotos == null){
                addedPhotos = new ArrayList<>();
            }
            photos.add(markerPhoto);
            addedPhotos.add(markerPhoto);
        }
    }

    public boolean isNotEmptyPhotos() {
        return addedPhotos != null && addedPhotos.size() > 0;
    }

    public void addComments(List<CardContent> addedComments) {
        if (addedComments == null) return;
        if (this.addedComments == null){
            this.addedComments = new ArrayList<>();
        }
        this.addedComments.addAll(addedComments);
    }

    public void addPhotos(List<MarkerPhoto> markerPhotos) {
        photos.addAll(markerPhotos);
    }

    public void clearPhotos() {
        photos.clear();
        if (addedPhotos != null) {
            addedPhotos.clear();
        }
    }

    public void clearComments() {
        comments.clear();
        if (addedComments != null){
            addedComments.clear();
        }
    }

    public List<CardContent> getComments() {
        return comments;
    }

    public void receiveComments(List<CardContent> comments) {
        this.comments.addAll(comments);
    }

    public List<CardContent> getAddedComments() {
        return addedComments;
    }

    public List<MarkerPhoto> getAddedPhotos() {
        return addedPhotos;
    }

    public void setIdMarker(Integer id) {
        idMarker = id;
    }

    public Integer getIdMarker() {
        return idMarker;
    }

    public void addComment(CardContent cardContent) {
        if (addedComments == null){
            addedComments = new ArrayList<>();
        }
        addedComments.add(cardContent);
    }

    public void clearAddedComments() {
        if (addedComments != null) {
            addedComments.clear();
        }
    }

    public void clearAddedPhotos() {
        if (addedPhotos != null) {
            addedPhotos.clear();
        }
    }
}
