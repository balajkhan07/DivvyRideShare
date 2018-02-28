package com.example.balaj.divvyrideshare;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class GetStarted extends AppCompatActivity {

    Button getStarted;
    Switch userTypeSwitch;

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
