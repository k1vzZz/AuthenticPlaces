package com.maps.developer.authenticplaces.model.input;

import java.sql.Timestamp;

public class InputSnapshot {

    private String identifierClient;

    private String login;

    private String urlImageAuthor;

    private String url;

    private Long uploadDate;

    public InputSnapshot(String identifierClient, String login,
                         String urlImageAuthor, String url, Timestamp uploadDate) {
        this.identifierClient = identifierClient;
        this.login = login;
        this.urlImageAuthor = urlImageAuthor;
        this.url = url;
        this.uploadDate = uploadDate.getTime();
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

    public String getUrlImageAuthor() {
        return urlImageAuthor;
    }

    public void setUrlImageAuthor(String urlImageAuthor) {
        this.urlImageAuthor = urlImageAuthor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Long uploadDate) {
        this.uploadDate = uploadDate;
    }
}
