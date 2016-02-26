package com.xbirder.bike.hummingbird;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;

import com.baidu.mapapi.SDKInitializer;
import com.xbirder.bike.hummingbird.util.SharedPreferenceHelper;


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
        String name = getVersionName(this);
        if(SharedPreferenceHelper.getString("APP_CURRENT_VERSION_NAME", "").equals(name)){
            mIntent = new Intent();
            mIntent.setClass(BeginActivity.this, LogoActivity.class);
        }else{
            SharedPreferenceHelper.saveString("APP_CURRENT_VERSION_NAME", name);
            mIntent = new Intent();
            mIntent.setClass(BeginActivity.this, GuideActivity.class);
        }
//        pm = (PowerManager) getSystemService(POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "xbird_Lock");
//        if(AccountManager.sharedInstance().getScreenBright().equals("yes")){
//            wakeLock.acquire();
//        }
//        mIntent = new Intent();
//        mIntent.setClass(BeginActivity.this, GuideActivity.class);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BeginActivity.this.startActivity(mIntent);
                BeginActivity.this.finish();
            }
        }, 1500);

    }
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }
    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }
    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }
}
