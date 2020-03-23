package com.beantechs.libnetwork;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.beantechs.libcommon.AppGlobals;

public class JsonCallback<T> {
    private Handler mainHandler = new Handler(Looper.myLooper());

    public void onSuccess(ApiResponse<T> response) {
    }

    public void onError(final ApiResponse<T> response) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AppGlobals.getApplication(), response.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onCacheSuccess(ApiResponse<T> response) {


    }
}
