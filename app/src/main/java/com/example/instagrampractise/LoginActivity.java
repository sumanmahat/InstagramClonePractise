package com.example.instagrampractise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagrampractise.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;

    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private TextView mPleaseWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPleaseWait = (TextView) findViewById(R.id.pleaseWait);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mContext = LoginActivity.this;
        Log.d(TAG, "onCreate: started.");

        mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        setupFirebaseAuth();
        init();

    }

    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking string if null");
        if (string.equals("")){
            return true;
        }else {
            return false;
        }
    }

        /*
 ///////////////////////////FIREBASE ///////////////////////////////
 */

        private void init(){
            //inilitizing button for login
            Button btnLogin = (Button) findViewById(R.id.btn_login);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: attempting to login in");
                    String email = mEmail.getText().toString();
                    String password = mPassword.getText().toString();

                    if (isStringNull(email) && isStringNull(password)){
                        Toast.makeText(mContext, "Fields cannot empty", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mPleaseWait.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.VISIBLE);

                        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                if (!task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed),
                                            Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                    mPleaseWait.setVisibility(View.GONE);
                                }else {
                                    try {
                                        if (user.isEmailVerified()){
                                            Intent intent= new Intent(getApplicationContext(),HomeActivity.class);
                                            startActivity(intent);

                                        }else {
                                            Toast.makeText(mContext, "Email is not verified..Please check your email", Toast.LENGTH_SHORT).show();
                                            mProgressBar.setVisibility(View.GONE);
                                            mPleaseWait.setVisibility(View.GONE);
                                            mAuth.signOut();
                                        }

                                    }catch (NullPointerException ex){
                                        Log.d(TAG, "onComplete: NullPointerException: " + ex.getMessage());
                                    } Log.d(TAG, "signInWithEmail: successful login");
                                    Toast.makeText(LoginActivity.this, getString(R.string.auth_success),
                                            Toast.LENGTH_SHORT).show();
                                    mProgressBar.setVisibility(View.GONE);
                                    mPleaseWait.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });
            TextView linkSignUp = (TextView) findViewById(R.id.link_signup);
            linkSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: navigating to register screen");
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });

                /*
         If the user is logged in then navigate to HomeActivity and call 'finish()'
          */
            if (mAuth.getCurrentUser() != null){
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }


    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //check if user is log in
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: signed in" + user.getUid());
                }else {
                    Log.d(TAG, "onAuthStateChanged: signout");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListner != null){
            mAuth.removeAuthStateListener(mAuthListner);
        }
    }
}
