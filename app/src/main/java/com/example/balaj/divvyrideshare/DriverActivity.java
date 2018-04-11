package com.example.balaj.divvyrideshare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DriverActivity extends AppCompatActivity {

    private ListView requestlistView;
    private ArrayList<String> requests;
    private ArrayList<Double> requestLatitude;
    private ArrayList<Double> requestLongitude;
    private ArrayList<String> usernames;
    private ArrayAdapter arrayAdapter;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    public DriverActivity() {
        requestLatitude = new ArrayList<>();
        requestLongitude = new ArrayList<>();
        requests = new ArrayList<>();
        usernames = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setBackgroundColor(Color.rgb(92,0,0));
        myToolbar.setTitleTextColor(Color.WHITE);
        setTitle("ACTIVE USERS REQUESTS");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        requests.clear();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("request");
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, requests);
        requestlistView = (ListView) findViewById(R.id.listView);
        requests.add("Getting nearby requests");
        requestlistView.setAdapter(arrayAdapter);

        requestlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (Build.VERSION.SDK_INT > 23 || ContextCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (requestLatitude.size() > 0 && requestLongitude.size() >0 && lastKnownLocation != null){
                        Intent intent = new Intent(getApplicationContext(), DriverLocationActivity.class);
                        intent.putExtra("riderLatitude", requestLatitude.get(i));
                        intent.putExtra("riderLongitude", requestLongitude.get(i));
                        intent.putExtra("driverLatitude", lastKnownLocation.getLatitude());
                        intent.putExtra("driverLongitude", lastKnownLocation.getLongitude());
                        intent.putExtra("riderUsername", usernames.get(i));
                        startActivity(intent);
                    }
                }
            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(final Location location) {
                updateListView(location);
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }
            @Override
            public void onProviderEnabled(String s) {

            }
            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(getApplicationContext(), "Please Turn On Location For Location.", Toast.LENGTH_SHORT).show();
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateListView(lastKnownLocation);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public void signOutButton(View view){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), GetStarted.class);
        startActivity(intent);
    }

    public void updateListView(final Location location) {
        requests.clear();
        if (location != null) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    requests.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                                String lat = (String) snapshot.child("latitude").getValue();
                                String lon = (String) snapshot.child("longitude").getValue();
                                String uId = (String) snapshot.child("userId").getValue();
                                String latitude = AESCrypt.Decrypt(lat);
                                String longitude = AESCrypt.Decrypt(lon);
                                double distanceInMiles = Distance.distance(Double.parseDouble(latitude), Double.parseDouble(longitude), location.getLatitude(), location.getLongitude());
                                double distanceODP = (double) Math.round(distanceInMiles * 1.60934 * 10) / 10;

                                if (!uId.equals(userId)){
                                    if (!uId.equals("driver")){
                                        requests.add(Double.toString(distanceODP) + " km");
                                        requestLatitude.add(Double.parseDouble(latitude));
                                        requestLongitude.add(Double.parseDouble(longitude));
                                        usernames.add(uId);
                                    }
                                }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, String permissions[], int[] grantResults){

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateListView(lastKnownLocation);
                }
            } else {

                Log.i("Error", "Permission Required");
            }
        }
    }
}
