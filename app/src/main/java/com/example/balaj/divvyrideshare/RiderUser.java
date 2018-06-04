package com.example.balaj.divvyrideshare;

public class RiderUser {

    double latitude;
    double longitude;
    String riderUserId;
    String driverUserId;

    RiderUser(){

    }

    RiderUser(double lat, double lon, String riderUserId, String driverUserId){

        this.latitude = lat;
        this.longitude = lon;
        this.riderUserId = riderUserId;
        this.driverUserId = driverUserId;
    }
}