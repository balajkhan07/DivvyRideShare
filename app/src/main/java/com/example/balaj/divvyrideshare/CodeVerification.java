package com.example.balaj.divvyrideshare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CodeVerification extends AppCompatActivity {

    Button submitCode;
    EditText codeVerify;
    String codeNumber;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String getUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        codeVerify = (EditText)findViewById(R.id.codeVerify);
        submitCode = (Button)findViewById(R.id.submitCode);
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
                Toast.makeText(CodeVerification.this, getUserType, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    if (getUserType.equals("rider")) {
                        Intent intent = new Intent(CodeVerification.this, RiderActivity.class);
                        startActivity(intent);
                    }
                    // ...
                    //myRef = database.getReference(mAuth.getCurrentUser().getPhoneNumber());
                    //writeNewUser(mAuth.getCurrentUser().getUid(), "Balaj Khan", "balajkhan07@gmail.com");
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
                }
            });
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(userId,name, email);

        myRef.setValue(user);
    }
}

class User {

    public String username;
    public String email;
    String userId;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userId, String username, String email) {
        this.username = username;
        this.email = email;
        this.userId = userId;
    }

}
