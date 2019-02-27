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

public class SignInOption extends AppCompatActivity
        implements View.OnClickListener{
    private final String TAG = "FB_LOGIN";

    // TODO: Add Auth members
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private EditText etPass;
    private EditText etEmail;

    /**
     * Standard Activity lifecycle methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_option);

        // Set up click handlers and view item references

        findViewById(R.id.btnUser).setOnClickListener(this);
        findViewById(R.id.btnCompany).setOnClickListener(this);
        findViewById(R.id.goBackLoginbtn).setOnClickListener(this);
        findViewById(R.id.instagramImage).setOnClickListener(this);



    }

    /**
     * When the Activity starts and stops, the app needs to connect and
     * disconnect the AuthListener
     */
    @Override
    public void onStart() {
        super.onStart();
        // TODO: add the AuthListener


    }

    @Override
    public void onStop() {
        super.onStop();
        // TODO: Remove the AuthListener


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCompany:
                updateStatus("btnCompany pressed");
                startActivity(new Intent(SignInOption.this,SignInCompanyActivity.class));
                break;
            case R.id.btnUser:
                updateStatus("btnUser pressed");
                startActivity(new Intent(SignInOption.this,SignInActivity.class));
                break;
            case R.id.goBackLoginbtn:
                updateStatus("btnUser pressed");
                startActivity(new Intent(SignInOption.this,LogInActivity.class));
                break;

        }
    }




    private void updateStatus(String stat) {

        Log.d(TAG, "Status:" + stat );
    }






}
