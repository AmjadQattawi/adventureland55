package com.example.adventureland;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
