package com.beantechs.libnetwork;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.beantechs.libcommon.AppGlobals;

public class JsonCallback<T> {

    public void onSuccess(ApiResponse<T> response) {
    }

    public void onError(final ApiResponse<T> response) {
    }

    public void onCacheSuccess(ApiResponse<T> response) {


    }
}
