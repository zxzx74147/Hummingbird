package com.xbirder.bike.hummingbird;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;

import com.baidu.mapapi.SDKInitializer;


/**
 * Created by zhhz on 15/11/10.
 */
public class BeginActivity extends Activity {

    private Handler mHandler = new Handler();
    private Intent mIntent;

    private PowerManager pm;
    //private PowerManager.WakeLock wakeLock;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_begin);
//        pm = (PowerManager) getSystemService(POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "xbird_Lock");
//        if(AccountManager.sharedInstance().getScreenBright().equals("yes")){
//            wakeLock.acquire();
//        }
        mIntent = new Intent();
        mIntent.setClass(BeginActivity.this, GuideActivity.class);
       // mIntent.setClass(BeginActivity.this, LogoActivity.class);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BeginActivity.this.startActivity(mIntent);
                BeginActivity.this.finish();
            }
        }, 1500);




    }


}
