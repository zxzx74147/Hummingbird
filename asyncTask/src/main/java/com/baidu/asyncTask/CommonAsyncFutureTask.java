/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Task Future （包内可见）
 * Created by chenrensong on 15/02/03.
 *
 * @param <V>
 */
abstract class CommonAsyncFutureTask<V> extends FutureTask<V> {
    private CommonAsyncTask<?, ?, ?> mTask = null;

    public CommonAsyncTask<?, ?, ?> getTask() {
        return mTask;
    }

    public CommonAsyncFutureTask(Callable<V> callable, CommonAsyncTask<?, ?, ?> task) {
        super(callable);
        mTask = task;
    }

    public CommonAsyncFutureTask(Runnable runnable, V result, CommonAsyncTask<?, ?, ?> task) {
        super(runnable, result);
        mTask = task;
    }

    protected abstract void cancelTask();

}
