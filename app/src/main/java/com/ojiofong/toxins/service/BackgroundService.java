package com.ojiofong.toxins.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.ojiofong.toxins.ui.InputDetectionActivity;

/**
 * Created by ojiofong on 9/16/16.
 * ..
 */

public class BackgroundService extends NonStopIntentService {

    private static final String TAG = BackgroundService.class.getSimpleName();

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d(TAG, "Headset is unplugged");
                        showToast("Unplugged");
                        break;
                    case 1:
                        Log.d(TAG, "Headset is plugged");
                        showToast("Plugged");
                        startInputDetection();
                        break;
                    default:
                        Log.d(TAG, "I have no idea what the headset state is");
                }
            }
        }
    };

    private void startInputDetection() {
        Intent intent = new Intent(this, InputDetectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public BackgroundService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerBroadcast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);
    }

    private void unRegisterBroadcast() {
        unregisterReceiver(myReceiver);
    }

    private void showToast(String msg){
        Toast.makeText(BackgroundService.this, msg, Toast.LENGTH_SHORT).show();
    }
}
