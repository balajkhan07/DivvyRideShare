package com.example.balaj.divvyrideshare;

public class ActiveRide {

    boolean rideIsActive;
    String riderUserId;
    String driverUserId;
    long timeInterval;

    public ActiveRide(){

    }

    public ActiveRide(boolean rideIsActive, String riderUserId, String driverUserId, long timeInterval){

        this.rideIsActive = rideIsActive;
        this.riderUserId = riderUserId;
        this.driverUserId = driverUserId;
        this.timeInterval = timeInterval;
    }

}
