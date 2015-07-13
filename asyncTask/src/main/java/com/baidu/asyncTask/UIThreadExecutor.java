/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * UI线程执行器
 * Created by chenrensong on 15/02/04.
 */
class UIThreadExecutor implements Executor {

    private static UIThreadExecutor mInstance = null;

    public static UIThreadExecutor getInstance() {
        if (mInstance == null) {
            synchronized (UIThreadExecutor.class) {
                if (mInstance == null) {
                    mInstance = new UIThreadExecutor();
                }
            }
        }
        return mInstance;
    }

    private Handler mHandler = null;


    @Override
    public void execute(Runnable r) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            if (mHandler == null) {
                mHandler = new Handler(Looper.getMainLooper());
            }
            mHandler.post(r);
        } else {
            r.run();
        }
    }
}
