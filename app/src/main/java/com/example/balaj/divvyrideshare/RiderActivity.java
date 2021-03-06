package com.example.balaj.divvyrideshare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.util.Map;

public class RiderActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private FirebaseAuth firebaseAuth;
    private Button callRide;
    private DatabaseReference databaseReference;
    private Boolean requestActive = false;
    private Handler handler;
    private TextView infoTextView;
    private SharedPreferences.Editor prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setBackgroundColor(Color.rgb(92,0,0));
        myToolbar.setTitleTextColor(Color.WHITE);
        setTitle("PICKUP LOCATION");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            Toast.makeText(this, "Location is not turned on. Please turn on location for accurate location.", Toast.LENGTH_SHORT).show();
        }
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Requests");
        prefs = getSharedPreferences("UserType", MODE_PRIVATE).edit();
        firebaseAuth = FirebaseAuth.getInstance();
        callRide = (Button)findViewById(R.id.callRide);
        infoTextView = (TextView)findViewById(R.id.infoTextView);
        handler = new Handler();

        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                if (map != null){

                    requestActive = true;
                    callRide.setText("Cancel Ride");

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            checkForUpdates();
                        }
                    }, 3000);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                Toast.makeText(RiderActivity.this, "Please Turn On Location For Location.", Toast.LENGTH_SHORT).show();
            }
        };

        if (ContextCompat.checkSelfPermission(RiderActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(RiderActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {

                updateMap(lastKnownLocation);
            }
        }
    }

    public void callRideButton(View view) throws Exception {

        if (requestActive) {

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map != null && firebaseAuth.getCurrentUser().getUid() != null){

                        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                        requestActive = false;
                        callRide.setText("Call A Ride");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {

            if (ContextCompat.checkSelfPermission(RiderActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                final Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null && firebaseAuth.getCurrentUser().getUid() != null) {

                    final RiderUser riderUser = new RiderUser(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), firebaseAuth.getCurrentUser().getUid(), "null");
                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(riderUser);
                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                            if (map != null){

                                requestActive = true;
                                callRide.setText("Cancel Ride");

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        checkForUpdates();
                                    }
                                }, 3000);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, String permissions[], int[] grantResults){

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && isEnabled) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateMap(lastKnownLocation);
                }
            } else {

                Toast.makeText(this, "Please enable location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateMap(Location location){

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        map.clear();
        map.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));
    }

    public void signOutButton(View view){

        prefs.remove("userType");
        prefs.apply();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), GetStarted.class);
        startActivity(intent);

    }

    public void checkForUpdates() {

        if (firebaseAuth != null) {

            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (!((map != null ? map.get("driverUserId") : null) != null && map.get("driverUserId").equals("null"))) {

                        infoTextView.setText("Your Driver Is On The Way.");
                        callRide.setVisibility(View.INVISIBLE);
                    }

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            checkForUpdates();
                        }
                    }, 3000);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}
