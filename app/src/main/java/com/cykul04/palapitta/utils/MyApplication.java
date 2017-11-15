package com.cykul04.palapitta.utils;

import android.app.Application;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Prefs.initPrefs(this);
    }
}
