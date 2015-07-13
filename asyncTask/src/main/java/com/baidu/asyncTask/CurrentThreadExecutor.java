/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;

import java.util.concurrent.Executor;

/**
 * 当前线程执行器(深度为15）
 * Created by chenrensong on 15/02/03.
 */
class CurrentThreadExecutor implements Executor {

    private static final int MAX_DEPTH = 20;

    private static CurrentThreadExecutor mInstance = null;

    /**
     * 包内可见外部不可调用
     *
     * @return
     */
    static CurrentThreadExecutor getInstance() {
        if (mInstance == null) {
            synchronized (CurrentThreadExecutor.class) {
                if (mInstance == null) {
                    mInstance = new CurrentThreadExecutor();
                }
            }
        }
        return mInstance;
    }

    /**
     * 线程变量，每个线程都是独立的
     */
    private ThreadLocal<Integer> mExecutionDepth = new ThreadLocal<Integer>();

    /**
     * Increments the depth.
     *
     * @return the new depth id.
     */
    private int incrementDepth() {
        Integer oldDepth = mExecutionDepth.get();
        if (oldDepth == null) {
            oldDepth = 0;
        }
        int newDepth = oldDepth + 1;
        mExecutionDepth.set(newDepth);
        return newDepth;
    }

    /**
     * Decrements the depth.
     *
     * @return the new depth id.
     */
    private int decrementDepth() {
        Integer oldDepth = mExecutionDepth.get();
        if (oldDepth == null) {
            oldDepth = 0;
        }
        int newDepth = oldDepth - 1;
        if (newDepth == 0) {
            mExecutionDepth.remove();
        } else {
            mExecutionDepth.set(newDepth);
        }
        return newDepth;
    }

    @Override
    public void execute(Runnable r) {
        int depth = incrementDepth();
        try {
            if (depth <= MAX_DEPTH) {
                r.run();
            } else {
                //当前线程执行超过15个就去子线程执行
                ThreadPoolExecutor.getInstance().execute(r);
            }
        } finally {
            decrementDepth();
        }
    }
}
