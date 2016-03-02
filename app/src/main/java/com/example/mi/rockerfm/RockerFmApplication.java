package com.example.mi.rockerfm;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by qin on 2016/2/17.
 */
public class RockerFmApplication extends Application {
    @Override
    public void onCreate() {
        Fresco.initialize(this);
        super.onCreate();
    }

}
