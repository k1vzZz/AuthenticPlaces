package com.maps.developer.authenticplaces.model.input;

import com.google.gson.Gson;

import java.util.List;

public class InputContentMarker {

    private Integer idCreatorMarker;

    private String identifierClient;

    private String login;

    private String urlImageCreator;

    private List<InputSnapshot> snapshots;

    private List<InputComment> comments;

    public InputContentMarker() {
    }

    public static InputContentMarker createFromJSON(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, InputContentMarker.class);
    }

    public Integer getIdCreatorMarker() {
        return idCreatorMarker;
    }

    public void setIdCreatorMarker(Integer idCreatorMarker) {
        this.idCreatorMarker = idCreatorMarker;
    }

    public String getIdentifierClient() {
        return identifierClient;
    }

    public void setIdentifierClient(String identifierClient) {
        this.identifierClient = identifierClient;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUrlImageCreator() {
        return urlImageCreator;
    }

    public void setUrlImageCreator(String urlImageCreator) {
        this.urlImageCreator = urlImageCreator;
    }

    public List<InputSnapshot> getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(List<InputSnapshot> snapshots) {
        this.snapshots = snapshots;
    }

    public List<InputComment> getComments() {
        return comments;
    }

    public void setComments(List<InputComment> comments) {
        this.comments = comments;
    }
}
