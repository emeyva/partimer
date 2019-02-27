package com.emeyva.firebasefirstlook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LogInActivity extends AppCompatActivity
        implements View.OnClickListener{
    private final String TAG = "FB_LOGIN";

    // TODO: Add Auth members
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String type;

    private EditText etPass;
    private EditText etEmail;

    /**
     * Standard Activity lifecycle methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_log_in);

        // Set up click handlers and view item references

        findViewById(R.id.createJobbtn).setOnClickListener(this);
        findViewById(R.id.registerbtn).setOnClickListener(this);
        findViewById(R.id.instagramImage).setOnClickListener(this);
        findViewById(R.id.facebookImage).setOnClickListener(this);


        etEmail = (EditText)findViewById(R.id.etEmailAddr);
        etPass = (EditText)findViewById(R.id.etPassword);

        // TODO: Get a reference to the Firebase auth object
        mAuth = FirebaseAuth.getInstance();


        // TODO: Attach a new AuthListener to detect sign in and out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "Signed in:" + user.getUid());
                }
                else Log.d(TAG, "Currently Signed out");
            }
        };

    }

    /**
     * When the Activity starts and stops, the app needs to connect and
     * disconnect the AuthListener
     */
    @Override
    public void onStart() {
        super.onStart();
        // TODO: add the AuthListener
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        // TODO: Remove the AuthListener
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createJobbtn:
                signUserIn();
                break;
            case R.id.registerbtn:
                startActivity(new Intent(LogInActivity.this,SignInOption.class));
                break;
            case R.id.instagramImage:
                Uri uri = Uri.parse("http://www.instagram.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Log.d(TAG, "Instagram Loaded");
                break;
            case R.id.facebookImage:
                startActivity(new Intent(LogInActivity.this,ViewJobUser.class));
                Log.d(TAG, "View Job Loaded");
                break;

        }
    }

    private boolean checkFormFields() {
        String email, password;

        email = etEmail.getText().toString();
        password = etPass.getText().toString();

        if (email.isEmpty()) {
            etEmail.setError("Email Required");
            return false;
        }
        if (password.isEmpty()){
            etPass.setError("Password Required");
            return false;
        }

        return true;
    }


    private void updateStatus(String stat) {

        Toast.makeText(LogInActivity.this,
                stat, Toast.LENGTH_SHORT).show();
    }

    private void signUserIn() {
        if (!checkFormFields())
            return;

        String email = etEmail.getText().toString();
        String password = etPass.getText().toString();

        // TODO: sign the user in with email and password credentials
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users_type").child(userId);
                            database.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    type = dataSnapshot.child("type").getValue(String.class);
                                    if (type.equals("user")){
                                        Log.w(TAG, "user Log in");
                                        startActivity(new Intent(LogInActivity.this, EmployeeMapActivity.class));
                                    }else{
                                        Log.w(TAG, "company Log in");
                                        startActivity(new Intent(LogInActivity.this, WelcomeCompany.class));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                                }
                            });



                        }
                        else{
                            Toast.makeText(LogInActivity.this,
                                    "Sign in failed", Toast.LENGTH_SHORT).show();
                        }
                        //updateStatus();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException){
                            updateStatus("Invalid Password");
                        }
                        else if (e instanceof FirebaseAuthInvalidUserException){
                            updateStatus("No account with this email");
                        }
                        else {
                            updateStatus(e.getLocalizedMessage());
                        }
                    }
                });



    }




}
