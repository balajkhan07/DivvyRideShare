package com.example.balaj.divvyrideshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GetStarted extends AppCompatActivity {

    private Switch userTypeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        Button getStarted = (Button) findViewById(R.id.getStarted);
        userTypeSwitch = (Switch)findViewById(R.id.userTypeSwitch);

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userType = "rider";
                if(userTypeSwitch.isChecked())
                    userType = "driver";
                Intent i = new Intent(GetStarted.this, LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userType", userType);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }
}