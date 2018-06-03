package com.example.balaj.divvyrideshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private TextView totalTime;
    private TextView totalDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fair_calculation);


        intent = getIntent();
        riderUserId =  intent.getStringExtra("riderUserId");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("RecordRide");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long totalT = (long) dataSnapshot.child(riderUserId).child("totalTime").getValue();
                long totalD = (long) dataSnapshot.child(riderUserId).child("totalDistance").getValue();

                totalTime = (TextView) findViewById(R.id.time);
                totalDistance = (TextView) findViewById(R.id.distance);

                totalTime.setText(totalT+"");
                totalDistance.setText(totalD+"");
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
