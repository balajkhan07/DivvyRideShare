package com.example.balaj.divvyrideshare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CodeVerification extends AppCompatActivity {

    private EditText codeVerify;
    private String codeNumber;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseReference;
    private String getUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setBackgroundColor(Color.rgb(92,0,0));
        myToolbar.setTitleTextColor(Color.WHITE);
        setTitle("SMS VERIFICATION");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseReference = firebaseDatabase.getReference("Divvy Ride Share");
        mAuth = FirebaseAuth.getInstance();
        codeVerify = (EditText)findViewById(R.id.codeVerify);
        Button submitCode = (Button) findViewById(R.id.submitCode);
        final PhoneAuthCredential[] credential = new PhoneAuthCredential[1];
        Bundle bundle = getIntent().getExtras();
        String verifyCode = "verifyCode";
        final String mVerificationId = bundle.getString(verifyCode);
        String userType = "userType";
        getUserType = bundle.getString(userType);

        submitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeNumber = codeVerify.getText().toString();
                credential[0] = PhoneAuthProvider.getCredential(mVerificationId, codeNumber);
                signInWithPhoneAuthCredential(credential[0]);
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    if (getUserType.equals("rider")) {
                        User users = new User(getUserType, Double.valueOf(mAuth.getCurrentUser().getPhoneNumber()));
                        mFirebaseReference.child(mAuth.getCurrentUser().getUid()).setValue(users);
                        Intent intent = new Intent(CodeVerification.this, RiderActivity.class);
                        startActivity(intent);
                    }if (getUserType.equals("driver")){
                        User users = new User(getUserType, Double.valueOf(mAuth.getCurrentUser().getPhoneNumber()));
                        mFirebaseReference.child(mAuth.getCurrentUser().getUid()).setValue(users);
                        Intent intent = new Intent(CodeVerification.this, DriverActivity.class);
                        startActivity(intent);
                    }
                } else {

                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                    }
                }
                }
            });
    }
}
