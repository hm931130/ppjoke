package com.beantechs.ppjoke;

import android.app.Application;

import com.beantechs.libnetwork.ApiService;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApiService.init("http://admintest.rongzw.com/api/", null);
    }
}
