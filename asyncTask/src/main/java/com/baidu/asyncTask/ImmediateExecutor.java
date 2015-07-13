/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;

import java.util.concurrent.Executor;

/**
 * 立刻执行器（慎用）
 * Created by chenrensong on 15/02/03.
 */
public class ImmediateExecutor implements Executor {

    private static ImmediateExecutor mInstance = null;

    public static ImmediateExecutor getInstance() {
        if (mInstance == null) {
            synchronized (ImmediateExecutor.class) {
                if (mInstance == null) {
                    mInstance = new ImmediateExecutor();
                }
            }
        }
        return mInstance;
    }


    @Override
    public void execute(Runnable r) {
        new Thread(r).start();
    }
}
