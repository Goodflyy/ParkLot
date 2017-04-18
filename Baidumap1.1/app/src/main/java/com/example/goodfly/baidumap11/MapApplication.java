package com.example.goodfly.baidumap11;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by yxy on 2017/4/15.
 */

public class MapApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
    }
}
