package com.example.forchita;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Api {
    String url="https://forchita2020.herokuapp.com/";

    @GET("id={id}&latitude={latitude}&longitude={longitude}")
    Call<Post> getRecommendations(
            @Path("id") String userID,
            @Path("latitude") double latitude,
            @Path("longitude") double longitude
    );

    @GET("nearby/latitude={latitude}&longitude={longitude}")
    Call<Near> getNearby(
            @Path("latitude") double latitude,
            @Path("longitude") double longitude
    );

    /*
    @GET("id=hWG7oE3GFaNcdQ42KqfDOym4TuH2&restaurant_id=394&latitude=35.2003759&longitude=-0.6484649")
    Call<Void> updateWeights();*/



    @GET("id={id}&restaurant_id={resto_id}&latitude={latitude}&longitude={longitude}")
    Call<Void> updateWeights(
            @Path("id") String userID,
            @Path("resto_id") int restaurant_id,
            @Path("latitude") double latitude,
            @Path("longitude") double longitude

    );
}
