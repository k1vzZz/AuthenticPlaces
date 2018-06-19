package com.maps.developer.authenticplaces.model.input;

import java.sql.Timestamp;

public class InputComment {

    private String identifierClient;

    private String login;

    private String urlImageAuthor;

    private String text;

    private Long uploadDate;

    public InputComment(String identifierClient, String login,
                        String urlImageAuthor, String text, Timestamp uploadDate) {
        this.identifierClient = identifierClient;
        this.login = login;
        this.urlImageAuthor = urlImageAuthor;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Long uploadDate) {
        this.uploadDate = uploadDate;
    }
}
