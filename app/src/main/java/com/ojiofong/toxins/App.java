package com.ojiofong.toxins;

import android.app.Application;
import android.content.Intent;

import com.ojiofong.toxins.service.BackgroundService;

/**
 * Created by ojiofong on 9/16/16.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init(){
        startService(new Intent(this, BackgroundService.class));
    }
}
