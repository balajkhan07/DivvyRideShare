package com.example.balaj.divvyrideshare;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FairCalculation extends AppCompatActivity {

    private Intent intent;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String riderUserId;
    private String riderUserId2;
    private TextView totalTime;
    private TextView totalDistance;
    private TextView totalTime2;
    private TextView totalDistance2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fair_calculation);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setBackgroundColor(Color.rgb(92, 0, 0));
        myToolbar.setTitleTextColor(Color.WHITE);
        setTitle("FAIR CALCULATION");

        intent = getIntent();
        riderUserId =  intent.getStringExtra("riderUserId");
        riderUserId2 =  intent.getStringExtra("riderUserId2");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("RecordRide");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long totalT = (long) dataSnapshot.child(riderUserId).child("totalTime").getValue();
                long totalD = (long) dataSnapshot.child(riderUserId).child("totalDistance").getValue();
                long totalT2 = (long) dataSnapshot.child(riderUserId2).child("totalTime").getValue();
                long totalD2 = (long) dataSnapshot.child(riderUserId2).child("totalDistance").getValue();

                totalTime = (TextView) findViewById(R.id.time);
                totalDistance = (TextView) findViewById(R.id.distance);
                totalTime.setText(totalT+"");
                totalDistance.setText(totalD+"");

                totalTime2 = (TextView) findViewById(R.id.time2);
                totalDistance2 = (TextView) findViewById(R.id.distance2);
                totalTime2.setText(totalT2+"");
                totalDistance2.setText(totalD2+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), DriverActivity.class);
        startActivity(intent);

    }

}
