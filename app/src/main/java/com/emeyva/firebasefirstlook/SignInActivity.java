package com.emeyva.firebasefirstlook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity
    implements View.OnClickListener{
    private final String TAG = "FB_SIGNIN";

    // TODO: Add Auth members
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private EditText etPass;
    private EditText etEmail;
    private EditText Name;
    private EditText City;
    private EditText Country;
    private EditText Age;

    /**
     * Standard Activity lifecycle methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Set up click handlers and view item references
        findViewById(R.id.createJobbtn).setOnClickListener(this);
        findViewById(R.id.gobackbtn).setOnClickListener(this);

        etEmail = (EditText)findViewById(R.id.etEmailAddr);
        etPass = (EditText)findViewById(R.id.etPassword);
        Name = (EditText)findViewById(R.id.etName);
        City = (EditText)findViewById(R.id.etCity);
        Country = (EditText)findViewById(R.id.etCountry);
        Age = (EditText)findViewById(R.id.etAge);

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
                createUserAccount();
                break;

            case R.id.gobackbtn:
                startActivity(new Intent(SignInActivity.this,SignInOption.class));
                break;


        }
    }

    private boolean checkFormFields() {
        String email, password, name, age, city, country;

        email = etEmail.getText().toString();
        password = etPass.getText().toString();
        name = Name.getText().toString();
        age = Age.getText().toString();
        city = City.getText().toString();
        country = Country.getText().toString();

        if (email.isEmpty()) {
            etEmail.setError("Email Required");
            return false;
        }
        if (password.isEmpty()){
            etPass.setError("Password Required");
            return false;
        }
        if (name.isEmpty()){
            Name.setError("Name Required");
            return false;
        }
        if (age.isEmpty()) {
            Age.setError("Age Required");
            return false;
        }
        if (city.isEmpty()){
            City.setError("City Required");
            return false;
        }
        if (country.isEmpty()){
            Country.setError("Country Required");
            return false;
        }

        return true;
    }

    private void updateStatus() {
        // TODO: get the current user
        FirebaseUser user = mAuth.getCurrentUser();


        if (user != null) {
            Toast.makeText(SignInActivity.this,
                    "Sign in", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(SignInActivity.this,
                    "Sign Out", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateStatus(String stat) {
        Toast.makeText(SignInActivity.this,
                stat, Toast.LENGTH_SHORT).show();
    }

    private void userProfile(){
        String name = Name.getText().toString();
        String age = Age.getText().toString();
        String city = City.getText().toString();
        String country = Country.getText().toString();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = mAuth.getCurrentUser().getUid();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(profileUpdates);

        DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Person").child(userId);
        Map newPost = new HashMap();
        newPost.put("User_name", name);
        newPost.put("age", age);
        newPost.put("country", country);
        newPost.put("city", city);

        currentUserDB.setValue(newPost);

        DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference().child("Users_type").child(userId);
        Map usersDBTypes = new HashMap();
        usersDBTypes.put("type", "user");
        usersDB.setValue(usersDBTypes);

        startActivity(new Intent(SignInActivity.this, EmployeeMapActivity.class));




    }




    private void createUserAccount() {
        if (!checkFormFields())
            return;

        String email = etEmail.getText().toString();
        String password = etPass.getText().toString();




        // TODO: Create the user account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this,
                                    "User was created", Toast.LENGTH_SHORT).show();
                            userProfile();

                        } else {
                            Toast.makeText(SignInActivity.this,
                                    "Account creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthUserCollisionException){
                            updateStatus("This email is already in use");
                        }
                        else{
                            updateStatus(e.getLocalizedMessage());
                        }
                    }
                });




    }
}
