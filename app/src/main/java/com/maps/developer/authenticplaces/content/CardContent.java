package com.maps.developer.authenticplaces.content;

import android.net.Uri;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardContent {

    private String author;
    private String content;
    private Timestamp time;
    private Uri imageUri;

    public CardContent(String author, String content, Timestamp time, Uri imageUri) {
        this.author = author;
        this.content = content;
        this.time = time;
        this.imageUri = imageUri;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public static List<CardContent> getTestComments(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        List<CardContent> cardContentList = new ArrayList<>();
        Collections.addAll(cardContentList,
                new CardContent("k1vzz", "The most place", timestamp, Uri.parse("http://i.imgur.com/ovr0NAF.jpg")),
                new CardContent("developer", "Nice place", timestamp, Uri.parse("http://i.imgur.com/ovr0NAF.jpg")),
                new CardContent("Urik", "COOL!", timestamp, Uri.parse("http://i.imgur.com/ovr0NAF.jpg")),
                new CardContent("Aloha", "Hello guys", timestamp, Uri.parse("http://i.imgur.com/ovr0NAF.jpg")),
                new CardContent("Aloha", "I'm cool", timestamp, Uri.parse("http://i.imgur.com/ovr0NAF.jpg")),
                new CardContent("Serj", "Hello guys", timestamp, Uri.parse("http://i.imgur.com/ovr0NAF.jpg")));
        return cardContentList;
    }
}
