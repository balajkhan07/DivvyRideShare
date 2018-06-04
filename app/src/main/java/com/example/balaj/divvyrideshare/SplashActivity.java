package com.example.balaj.divvyrideshare;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends Activity implements Animation.AnimationListener {

    private Intent intent;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences prefs;
    private String userTypeInPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("DivvyRideShare");
        firebaseAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("UserType", MODE_PRIVATE);
        userTypeInPref = prefs.getString("userType", null);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotator);
        ImageView car = (ImageView) findViewById(R.id.carimg);
        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_fade_in);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_linear);

        animFadeIn.setAnimationListener(this);
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.startAnimation(animFadeIn);
        car.startAnimation(rotate);
    }

    @Override
    public void onAnimationStart(Animation animation) {

        if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null){

            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {

        if (userTypeInPref != null) {
            String uType = prefs.getString("userType", "NoUserAvailable");//"No name defined" is the default value.
            if (uType.equals("rider")){
                intent = new Intent(SplashActivity.this, RiderActivity.class);
                startActivity(intent);
            }else if (uType.equals("driver")){
                intent = new Intent(SplashActivity.this, DriverActivity.class);
                startActivity(intent);
            }else{
                intent = new Intent(SplashActivity.this, GetStarted.class);
                startActivity(intent);
            }
        }else{
            intent = new Intent(SplashActivity.this, GetStarted.class);
            startActivity(intent);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
