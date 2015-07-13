/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 通用后台执行器
 * Created by chenrensong on 15/02/04.
 */
class ThreadPoolExecutor implements Executor {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /* package */ static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    /* package */ static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /* package */ static final long KEEP_ALIVE = 10L;

    private static final Executor THREAD_POOL_EXECUTOR = new java.util.concurrent.ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private static ThreadPoolExecutor mInstance = null;

    public static ThreadPoolExecutor getInstance() {
        if (mInstance == null) {
            synchronized (ThreadPoolExecutor.class) {
                if (mInstance == null) {
                    mInstance = new ThreadPoolExecutor();
                }
            }
        }
        return mInstance;
    }


    @Override
    public void execute(Runnable r) {
        THREAD_POOL_EXECUTOR.execute(r);
    }
}
