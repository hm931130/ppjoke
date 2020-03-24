package com.beantechs.ppjoke;

import android.app.Application;

import com.beantechs.libnetwork.ApiService;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApiService.init("http://123.56.232.18:8080/serverdemo", null);
    }
}
