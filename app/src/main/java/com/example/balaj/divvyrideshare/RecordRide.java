package com.example.balaj.divvyrideshare;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class RecordRide extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Calendar cal1;
    private Calendar cal2;
    private Date currentLocalTime1;
    private Date currentLocalTime2;
    private double distance = 0d;
    private double totalDistance;
    private Boolean rideActiveOne = false;
    private Boolean rideActiveTwo = false;
    private Button rideButton;
    private Button rideButton2;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location prevLocation;
    private Intent intent;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabaseCheckRide;
    private DatabaseReference databaseReferenceCheckRide;
    private String riderUserId;
    private String riderUserId2;
    private Boolean rideIsActive = false;
    long totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_ride);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setBackgroundColor(Color.rgb(92, 0, 0));
        myToolbar.setTitleTextColor(Color.WHITE);
        setTitle("PICKUP LOCATION");

        intent = getIntent();
        riderUserId = intent.getStringExtra("riderUserId");
        riderUserId2 = intent.getStringExtra("riderUserId");
        Toast.makeText(this, riderUserId2.toString()+"", Toast.LENGTH_SHORT).show();
        rideButton = findViewById(R.id.rideButton);
        rideButton2 = findViewById(R.id.rideButton2);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("RecordRide");
        firebaseDatabaseCheckRide = FirebaseDatabase.getInstance();
        databaseReferenceCheckRide = firebaseDatabaseCheckRide.getReference("RideIsActive");

        databaseReferenceCheckRide.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String driverUserId = (String) dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("driverUserId").getValue();

                if (driverUserId != null && driverUserId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                   riderUserId = (String) dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("riderUserId").getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void startEndRide(View view){

        if (rideActiveOne){

            rideButton.setText("Start Ride One");
            cal2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
            currentLocalTime2 = cal2.getTime();
            DateFormat date = new SimpleDateFormat("HH:mm:ss a");
            date.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
            rideActiveOne = false;

            intent = new Intent(getApplicationContext(), FairCalculation.class);
            totalTime = printDifference(currentLocalTime1,currentLocalTime2);
            databaseReferenceCheckRide.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
            databaseReference.child(riderUserId).child("totalTime").setValue(totalTime);
            intent.putExtra("riderUserId", riderUserId);
            startActivity(intent);

        }else {

            rideButton.setText("End Ride One");
            cal1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
            currentLocalTime1 = cal1.getTime();
            DateFormat date = new SimpleDateFormat("HH:mm:ss a");
            date.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
            rideActiveOne = true;
            rideIsActive = true;
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

    public void getRequestsList(View view){

        ActiveRide activeRide = new ActiveRide(rideIsActive, riderUserId, FirebaseAuth.getInstance().getCurrentUser().getUid(), totalTime);
        databaseReferenceCheckRide.child(FirebaseAuth.getInstance().getUid()).setValue(activeRide);
        Intent intent = new Intent(getApplicationContext(), DriverActivity.class);
        startActivity(intent);
    }

    public void startEndRide2(View view){

        if (rideActiveTwo){

            rideButton2.setText("Start Ride Two");
            cal2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
            currentLocalTime2 = cal2.getTime();
            DateFormat date = new SimpleDateFormat("HH:mm:ss a");
            date.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
            rideActiveTwo = false;

            intent = new Intent(getApplicationContext(), FairCalculation.class);
            long totalTime = printDifference(currentLocalTime1,currentLocalTime2);
            databaseReference.child(riderUserId2).child("totalTime").setValue(totalTime);
            intent.putExtra("riderUserId", riderUserId2);
            startActivity(intent);

        }else {

            rideButton2.setText("End Ride Two");
            cal1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
            currentLocalTime1 = cal1.getTime();
            DateFormat date = new SimpleDateFormat("HH:mm:ss a");
            date.setTimeZone(TimeZone.getTimeZone("GMT+5:00"));
            rideActiveTwo = true;
            startCalculatingDistance2();

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

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 6, locationListener);
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
                int td = (int)totalDistance;
                databaseReference.child(riderUserId).child("totalDistance").setValue(td);
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

    public void startCalculatingDistance2(){

        if (ContextCompat.checkSelfPermission(RecordRide.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(RecordRide.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 6, locationListener);
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
                int td = (int)totalDistance;
                databaseReference.child(riderUserId2).child("totalDistance").setValue(td);
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

        // if less than 0.5 metres, do not record
        if (distanceToLast < 0.5) {
            //
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
