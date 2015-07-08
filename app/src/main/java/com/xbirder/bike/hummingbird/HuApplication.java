package com.xbirder.bike.hummingbird;

import android.app.Application;

/**
 * Created by zhengxin on 15/7/6.
 */
public class HuApplication extends Application {

    private static HuApplication mInstance;

    public void onCreate(){
        super.onCreate();
        mInstance = this;
    }

    public static  HuApplication sharedInstance(){
        return mInstance;
    }
}
