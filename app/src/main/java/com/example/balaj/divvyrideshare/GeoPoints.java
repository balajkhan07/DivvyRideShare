package com.example.balaj.divvyrideshare;

public class GeoPoints{

    String latitude;
    String longitude;
    String userId;

    GeoPoints(){

    }

    GeoPoints(String lat, String lon, String uId){

        this.latitude = lat;
        this.longitude = lon;
        this.userId = uId;
    }
}