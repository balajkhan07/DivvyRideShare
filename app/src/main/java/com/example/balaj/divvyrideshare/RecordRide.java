package com.example.balaj.divvyrideshare;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RecordRide extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Calendar cal1;
    private Calendar cal2;
    private Date currentLocalTime1;
    private Date currentLocalTime2;
    private double distance = 0d;
    private double totalDistance;
    private Boolean rideActive = false;
    private Button rideButton;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location prevLocation;
    private Intent intent;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String riderUserId;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_ride);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        handler = new Handler();
        intent = getIntent();
        riderUserId = intent.getStringExtra("riderUserId");
        rideButton = findViewById(R.id.rideButton);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("RecordRide");
    }

    public void startEndRide(View view){

        if (rideActive){

            rideButton.setText("Start Ride");
            cal2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
            currentLocalTime2 = cal2.getTime();
            DateFormat date = new SimpleDateFormat("HH:mm:ss a");
            date.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
            rideActive = false;

            intent = new Intent(getApplicationContext(), FairCalculation.class);
            long totalTime = printDifference(currentLocalTime1,currentLocalTime2);
            databaseReference.child(riderUserId).child("totalTime").setValue(totalTime);
            intent.putExtra("riderUserId", riderUserId);
            startActivity(intent);

        }else {

            rideButton.setText("End Ride");
            cal1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
            currentLocalTime1 = cal1.getTime();
            DateFormat date = new SimpleDateFormat("HH:mm:ss a");
            date.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
            rideActive = true;
            startCalculatingDistance();

            if (ContextCompat.checkSelfPermission(RecordRide.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(RecordRide.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    prevLocation = lastKnownLocation;
                }
            }

        }
    }

    public void startCalculatingDistance(){

        if (ContextCompat.checkSelfPermission(RecordRide.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(RecordRide.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                updateMap(lastKnownLocation);
            }
        }

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                totalDistance = updateDistance(location);
                DecimalFormat decimalFormat = new DecimalFormat("#");
                databaseReference.child(riderUserId).child("totalDistance").setValue(decimalFormat.format(totalDistance));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(RecordRide.this, "Please Turn On Location For Location.", Toast.LENGTH_SHORT).show();
            }
        };

    }

    public double updateDistance(Location location) {

        double distanceToLast = location.distanceTo(prevLocation);

        // if less than 1 metres, do not record
        if (distanceToLast < 1.00) {
            Log.i("Location Not Record.", "Minor Change.");
        } else{
            distance += distanceToLast;
        }

        prevLocation = location;
        return distance;
    }

    public void updateMap(Location location){

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        map.clear();
        map.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));
    }

    public long printDifference(Date startDate, Date endDate) {

        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return elapsedSeconds;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                updateMap(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ContextCompat.checkSelfPermission(RecordRide.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(RecordRide.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                updateMap(lastKnownLocation);
            }
        }
    }

    public void onRequestPermissionsResult ( int requestCode, String permissions[], int[] grantResults){

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateMap(lastKnownLocation);
                }
            } else {

                Log.i("Error", "Permission Required");
            }
        }
    }
}
