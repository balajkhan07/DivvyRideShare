package com.example.balaj.divvyrideshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
//                String riderId = (String) dataSnapshot.child(riderUserId).getValue();
                long totalTime = (long) dataSnapshot.child(riderUserId).child("totalTime").getValue();
                double totalDistance = (double) dataSnapshot.child(riderUserId).child("totalDistance").getValue();

                Toast.makeText(FairCalculation.this, "Total Distance = " +totalDistance+ "Total Time = " +totalTime, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
