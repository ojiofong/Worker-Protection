package com.ojiofong.toxins.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ojiofong.toxins.R;
import com.ojiofong.toxins.arduino.SoundReceiver;
import com.sefford.circularprogressdrawable.CircularProgressDrawable;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ojiofong on 9/16/16.
 * ..
 */

public class DetectionActivity extends AppCompatActivity {

    private static final String TAG = DetectionActivity.class.getSimpleName();
    CircularProgressDrawable drawable;
    ImageView ivDrawable;
    TextView statusText;

    private SoundReceiver mSoundReceiver;
    boolean transmitting = false;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if (0 < msg.what) {
                statusText.setText(R.string.device_connect);
            } else {
                if (msg.getData() != null) {
                    String s = msg.getData().getString("Text");
                    if(s != null){
                        statusText.setText("Transmitting \n" + s);
                    }
                    transmitting = true;
                }
            }
        }
    };

    Animator currentAnimation;

//    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
//                int state = intent.getIntExtra("state", -1);
//                switch (state) {
//                    case 0:
//                        Log.d(TAG, "Headset is unplugged");
//                        break;
//                    case 1:
//                        Log.d(TAG, "Headset is plugged");
//                        break;
//                    default:
//                        Log.d(TAG, "I have no idea what the headset state is");
//                }
//            }
//        }
//    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_inputdetection);
        hookUpListeners();
        setupToolbar();

//        if (Const.HEADSET_PLUGGED.equals(getIntent().getStringExtra(Const.HEADSET_PLUGGED))){
//            startMockSpin();
//        }

        //mTextView.setText("ready when you are :)");

        mSoundReceiver = new SoundReceiver(mHandler);
        mSoundReceiver.start();

    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detect hazards");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
//
//    private void registerBroadcast() {
//        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
//        registerReceiver(myReceiver, filter);
//    }
//
//    private void unRegisterBroadcast() {
//        unregisterReceiver(myReceiver);
//    }

    private void hookUpListeners() {
        statusText = (TextView) findViewById(R.id.statusText);
        statusText.setMovementMethod(new ScrollingMovementMethod());

        statusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMockSpin();
            }
        });

        ivDrawable = (ImageView) findViewById(R.id.iv_drawable);
        ivDrawable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMockSpin();
            }
        });

        drawable = new CircularProgressDrawable.Builder()
                .setRingWidth(getResources().getDimensionPixelSize(R.dimen.drawable_ring_size))
                .setOutlineColor(getResources().getColor(android.R.color.darker_gray))
                .setRingColor(getResources().getColor(android.R.color.holo_green_light))
                .setCenterColor(getResources().getColor(R.color.colorPrimary))
                .create();
        //ivDrawable.setImageDrawable(drawable);
    }

    /**
     * Style 1 animation will simulate a indeterminate loading while taking advantage of the inner
     * circle to provide a progress sense
     *
     * @return Animation
     */
    private Animator prepareStyle1Animation() {
        AnimatorSet animation = new AnimatorSet();

        final long mDuration = 6600;

        final Animator indeterminateAnimation = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.PROGRESS_PROPERTY, 0, mDuration);
        indeterminateAnimation.setDuration(mDuration);

        Animator innerCircleAnimation = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.CIRCLE_SCALE_PROPERTY, 0f, 0.99f);
        innerCircleAnimation.setDuration(mDuration);
        innerCircleAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                drawable.setIndeterminate(true);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                indeterminateAnimation.end();
                drawable.setIndeterminate(false);
                drawable.setProgress(0);
                //updateDone();
                startMockSpin();
            }
        });

        animation.playTogether(innerCircleAnimation, indeterminateAnimation);
        return animation;
    }

    /**
     * Style 2 animation will fill the outer ring while applying a color effect from red to green
     *
     * @return Animation
     */
    private Animator prepareStyleAnimationFillRingWithColor() {
        AnimatorSet animation = new AnimatorSet();

        ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.PROGRESS_PROPERTY,
                0f, 1f);
        progressAnimation.setDuration(3600);
        progressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator colorAnimator = ObjectAnimator.ofInt(drawable, CircularProgressDrawable.RING_COLOR_PROPERTY,
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_green_light));
        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.setDuration(3600);

        animation.playTogether(progressAnimation, colorAnimator);
        return animation;
    }

    private void updateDone() {
        statusText.setText("Test Passed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcast();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                postM2x();
//            }
//        }).start();
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


    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d(TAG, "Headset is unplugged");
                        // showToast("Unplugged");
                        statusText.setText(R.string.device_connect);
                        transmitting = false;
                        stopMockSpin();
                        break;
                    case 1:
                        Log.d(TAG, "Headset is plugged");
                        startMockSpin();
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


    private void startMockSpin() {
        //statusText.setText("Transmitting");
        ivDrawable.setVisibility(View.VISIBLE);
        ivDrawable.setImageDrawable(drawable);
        currentAnimation = prepareStyle1Animation();
        currentAnimation.start();
    }

    String postWeb() throws IOException {

        //  String url = "http://ojiofong.com/test/welcome.php";

        String url = "http://requestb.in/1a4120f1";

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("name", "Mary Doe")
                .add("email", "maryjizzle@email.com")
                .build();


        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private void stopMockSpin() {
        statusText.setText(R.string.device_connect);
        ivDrawable.setVisibility(View.VISIBLE);
        ivDrawable.setImageResource(R.drawable.connect_screen_trimmed);
    }

//    M2XClient client = new M2XClient("2b56fc8b943562e4c68990d0e809c3c2");
//    M2XDevice device = client.device("cc0302ea7cdbd874b8b61770ca86ce3a");
//
//
//    private void postM2x(){
//
//        try {
//            M2XResponse response = client.createDevice(M2XClient.jsonSerialize(new HashMap<String, Object>()
//            {{
//                put("name", "My Device");
//                put("visibility", "private");
//            }}));
//
//            String deviceId = response.json().getString("id");
//            M2XDevice device = client.device(deviceId);
//
//            M2XStream stream = device.stream("mystream");
//            stream.createOrUpdate("{\"type\":\"numeric\",\"unit\":{\"label\":\"points\",\"symbol\":\"pt\"}}");
//
//            stream.updateValue(M2XClient.jsonSerialize(new HashMap<String, Object>()
//            {{
//                put("value", 10);
//            }}));
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }



}
