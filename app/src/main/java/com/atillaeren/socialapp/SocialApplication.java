package com.atillaeren.socialapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class SocialApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //activate firebase offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //after set active, previously uploaded information can also be viewed offline
    }
}
