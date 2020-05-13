package com.example.instagrampractise.utils;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseMethod {
    private static final String TAG = "FirebaseMethod";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context mContext;
    private String userId;

    public FirebaseMethod(Context mContext) {
        this.mContext = mContext;
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() !=null){
            userId = mAuth.getCurrentUser().getUid();
        }
    }
}
