package com.lemi.interact;

import android.app.Application;

import com.lemi.interact.config.Seeting;

import io.rong.imlib.RongIMClient;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RongIMClient.init(this, Seeting.RONG_APP_ID);
    }
}
