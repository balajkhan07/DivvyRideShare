package com.example.balaj.divvyrideshare;

public class RiderUser {

    String latitude;
    String longitude;
    String riderUserId;
    String driverUserId;

    RiderUser(){

    }

    RiderUser(String lat, String lon, String riderUserId, String driverUserId){

        this.latitude = lat;
        this.longitude = lon;
        this.riderUserId = riderUserId;
        this.driverUserId = driverUserId;
    }
}