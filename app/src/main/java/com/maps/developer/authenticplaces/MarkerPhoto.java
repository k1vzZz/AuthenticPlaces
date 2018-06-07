package com.maps.developer.authenticplaces;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MarkerPhoto implements Parcelable {

    private String mUri;
    private String mTitle;

    public MarkerPhoto(String mUri, String mTitle) {
        this.mUri = mUri;
        this.mTitle = mTitle;
    }

    protected MarkerPhoto(Parcel in){
        mUri = in.readString();
        mTitle = in.readString();
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
        dest.writeString(mUri);
        dest.writeString(mTitle);
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String mUri) {
        this.mUri = mUri;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkerPhoto that = (MarkerPhoto) o;
        return Objects.equals(mUri, that.mUri) &&
                Objects.equals(mTitle, that.mTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mUri, mTitle);
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
