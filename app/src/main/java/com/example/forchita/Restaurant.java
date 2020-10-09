package com.example.forchita;

import android.os.Parcel;
import android.os.Parcelable;

public class Restaurant implements Parcelable {
    private String restId;
    private String restName;
    private String restCat;
    private String restImage;
    private String ratingBar;
    private String restAdresse;
    private String restLatitude;
    private String restLongitude;
    private String restNombre;

    public Restaurant(){

    }


    public Restaurant(String restId, String restName, String restCat, String restImage, String ratingBar, String restAdresse, String restLatitude, String restLongitude,String restNombre){
        this.restId = restId;
        this.restName = restName;
        this.restCat = restCat;
        this.restImage = restImage;
        this.ratingBar = ratingBar;
        this.restAdresse = restAdresse;
        this.restLatitude = restLatitude;
        this.restLongitude = restLongitude;
        this.restNombre = restNombre;
    }

    protected Restaurant(Parcel in) {
        restName = in.readString();
        restCat = in.readString();
        restImage = in.readString();
        ratingBar = in.readString();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public String getRestNombre() {
        return restNombre;
    }

    public void setRestNombre(String restNombre) {
        this.restNombre = restNombre;
    }

    public String getRestId() {
        return restId;
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public String getRestLatitude() {
        return restLatitude;
    }

    public void setRestLatitude(String restLatitude) {
        this.restLatitude = restLatitude;
    }

    public String getRestLongitude() {
        return restLongitude;
    }

    public void setRestLongitude(String restLongitude) {
        this.restLongitude = restLongitude;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getRestCat() {
        return restCat;
    }

    public void setRestCat(String restCat) {
        this.restCat = restCat;
    }

    public String getRestImage() {
        return restImage;
    }

    public void setRestImage(String restImage) {
        this.restImage = restImage;
    }

    public String getRatingBar() {
        return ratingBar;
    }

    public void setRatingBar(String ratingBar) {
        this.ratingBar = ratingBar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(restName);
        parcel.writeString(restCat);
        parcel.writeString(restImage);
        parcel.writeString(ratingBar);
    }

    public String getRestAdresse() {
        return restAdresse;
    }

    public void setRestAdresse(String restAdresse) {
        this.restAdresse = restAdresse;
    }
}
