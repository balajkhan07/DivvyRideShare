package com.example.balaj.divvyrideshare;

public class GeoPoints{

    double latitude;
    double longitude;
    String userId;

    GeoPoints(){

    }

    GeoPoints(double lat, double lon, String userId){

        this.latitude = lat;
        this.longitude = lon;
        this.userId = userId;
    }
}