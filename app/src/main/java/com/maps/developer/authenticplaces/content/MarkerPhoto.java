package com.maps.developer.authenticplaces.content;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MarkerPhoto implements Parcelable {

    private String uri;
    private String title;
    private Bitmap image;

    public MarkerPhoto(String uri, String title) {
        this.uri = uri;
        this.title = title;
    }

    protected MarkerPhoto(Parcel in){
        uri = in.readString();
        title = in.readString();
    }

    public static final Creator<MarkerPhoto> CREATOR = new Creator<MarkerPhoto>() {
        @Override
        public MarkerPhoto createFromParcel(Parcel source) {
            return new MarkerPhoto(source);
        }

        @Override
        public MarkerPhoto[] newArray(int size) {
            return new MarkerPhoto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeString(title);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkerPhoto that = (MarkerPhoto) o;
        return Objects.equals(uri, that.uri) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, title);
    }

    public static List<MarkerPhoto> getTestPhotos() {
        List<MarkerPhoto> markerPhotos = new ArrayList<>();
        Collections.addAll(markerPhotos,
                new MarkerPhoto("http://i.imgur.com/zuG2bGQ.jpg", "Galaxy"),
                new MarkerPhoto("http://i.imgur.com/ovr0NAF.jpg", "Space Shuttle"),
                new MarkerPhoto("http://i.imgur.com/n6RfJX2.jpg", "Galaxy Orion"),
                new MarkerPhoto("http://i.imgur.com/qpr5LR2.jpg", "Earth"),
                new MarkerPhoto("http://i.imgur.com/pSHXfu5.jpg", "Astronaut"),
                new MarkerPhoto("http://i.imgur.com/3wQcZeY.jpg", "Satellite")
        );
        return markerPhotos;
    }
}
