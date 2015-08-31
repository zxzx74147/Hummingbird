package com.xbirder.bike.hummingbird;

import android.app.Application;

import com.baidu.core.net.base.HttpManager;
import com.xbirder.bike.hummingbird.bluetooth.XBirdBluetoothManager;

/**
 * Created by zhengxin on 15/7/6.
 */
public class HuApplication extends Application {

    private static HuApplication mInstance;

    public XBirdBluetoothManager XBirdBluetoothManager() {
        return xBirdBluetoothManager;
    }

    private XBirdBluetoothManager xBirdBluetoothManager;

    public void onCreate(){
        super.onCreate();
        mInstance = this;
        init();
    }

    private void init() {
        HttpManager.init(this);
        xBirdBluetoothManager = new XBirdBluetoothManager();
    }

    public static  HuApplication sharedInstance(){
        return mInstance;
    }
}
