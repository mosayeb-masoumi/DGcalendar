package com.example.coroutine;

import android.app.Application;

import calendar.util.Util;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Util.initUtils(getApplicationContext());
    }
}
