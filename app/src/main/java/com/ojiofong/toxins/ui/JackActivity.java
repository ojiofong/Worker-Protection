//Jack by Wolf Paulus is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
package com.ojiofong.toxins.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ojiofong.toxins.R;
import com.ojiofong.toxins.arduino.SoundReceiver;

/**
 * Launcher Activity
 *
 * @author <a href="mailto:wolf@wolfpaulus.com">Wolf Paulus</a>
 */
public class JackActivity extends AppCompatActivity {
    private static final String TAG = JackActivity.class.getSimpleName();
    boolean transmitting = false;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if (0 < msg.what) {
                mTextView.setText(R.string.device_connect);
            } else {
                if (msg.getData() != null) {
                    String s = msg.getData().getString("Text");
                    mTextView.setText(s);
                    transmitting = true;
                }
            }
        }
    };

    private TextView mTextView;
    private SoundReceiver mSoundReceiver;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputdetection);

        mTextView = (TextView) findViewById(R.id.statusText);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        //mTextView.setText("ready when you are :)");

        mSoundReceiver = new SoundReceiver(mHandler);
        mSoundReceiver.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcast();
    }

    @Override
    protected void onPause() {
        mSoundReceiver.stop();
        super.onPause();
        System.runFinalizersOnExit(true);
        unRegisterBroadcast();
        finish();
    }

    @Override
    protected void onDestroy() {
        mSoundReceiver.destroy();
        super.onDestroy();
    }

    @SuppressWarnings("UnusedParameters")
    public void onClick_Toggle(final View view) {
        ToggleButton tb = (ToggleButton) view;
        if (tb.isChecked()) {
            mSoundReceiver.start();
        } else {
            mSoundReceiver.stop();
        }
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d(TAG, "Headset is unplugged");
                        // showToast("Unplugged");
                        mTextView.setText(R.string.device_connect);
                        transmitting = false;
                        break;
                    case 1:
                        Log.d(TAG, "Headset is plugged");
                        // showToast("Plugged");
                        if (transmitting){

                        }
                        break;
                    default:
                        Log.d(TAG, "I have no idea what the headset state is");
                }
            }
        }
    };

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);
    }

    private void unRegisterBroadcast() {
        unregisterReceiver(myReceiver);
    }


}
