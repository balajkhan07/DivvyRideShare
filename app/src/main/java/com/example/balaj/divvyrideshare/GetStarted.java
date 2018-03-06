package com.example.balaj.divvyrideshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class GetStarted extends AppCompatActivity {

    private Button getStarted;
    private Switch userTypeSwitch;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Divvy Ride Share");
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){

            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);

                    if (user.usertype != null && user.usertype.equals("rider")){

                        Intent intent = new Intent(GetStarted.this, RiderActivity.class);
                        startActivity(intent);
                    }if (user.usertype != null && user.usertype.equals("driver")){

                        Intent intent = new Intent(GetStarted.this, DriverActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        getSupportActionBar().hide();

        getStarted = (Button)findViewById(R.id.getStarted);
        userTypeSwitch = (Switch)findViewById(R.id.userTypeSwitch);

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userType = "rider";
                if(userTypeSwitch.isChecked())
                    userType = "driver";
                Intent i = new Intent(GetStarted.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userType", userType);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }
}