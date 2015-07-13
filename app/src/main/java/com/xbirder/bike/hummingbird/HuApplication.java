package com.xbirder.bike.hummingbird;

import android.app.Application;

import com.baidu.core.net.base.HttpManager;

/**
 * Created by zhengxin on 15/7/6.
 */
public class HuApplication extends Application {

    private static HuApplication mInstance;

    public void onCreate(){
        super.onCreate();
        mInstance = this;
        init();
    }

    private void init() {
        HttpManager.init(this);
    }

    public static  HuApplication sharedInstance(){
        return mInstance;
    }
}
