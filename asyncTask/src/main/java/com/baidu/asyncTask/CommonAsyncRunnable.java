/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;

import java.security.InvalidParameterException;

/**
 * 所有的Task都必须使用Task Runnable
 * Created by chenrensong on 15/02/05.
 */
abstract class CommonAsyncRunnable implements Runnable {
    private CommonAsyncFutureTask<?> mAsyncTaskFuture = null;

    public CommonAsyncRunnable(CommonAsyncFutureTask<?> task) {
        if (task == null || task.getTask() == null) {
            throw new InvalidParameterException("parameter is null");
        }
        mAsyncTaskFuture = task;
    }

    public void runTask() {
        try {
            mAsyncTaskFuture.run();
        } catch (OutOfMemoryError oom) {
            //内存不足需要回收
        }
    }

    public void cancelTask() {
        mAsyncTaskFuture.cancelTask();
    }

    public boolean isCancelled() {
        return mAsyncTaskFuture.isCancelled();
    }

    public CommonAsyncTask<?, ?, ?> getTask() {
        return mAsyncTaskFuture.getTask();
    }

    public int getPriority() {
        return mAsyncTaskFuture.getTask().getPriority();
    }

    /**
     * 設置是否爲超時
     *
     * @param isTimeout
     */
    public void setTimeout(boolean isTimeout) {
        mAsyncTaskFuture.getTask().setTimeout(isTimeout);
    }

    public boolean IsTimeout() {
        return mAsyncTaskFuture.getTask().isTimeout();
    }

    public int getTag() {
        return mAsyncTaskFuture.getTask().getTag();
    }

    public int getParallelTag() {
        if (mAsyncTaskFuture.getTask().getParallel() != null) {
            return mAsyncTaskFuture.getTask().getParallel().getTag();
        } else {
            return 0;
        }
    }

    public String getKey() {
        return mAsyncTaskFuture.getTask().getKey();
    }

    public TaskParallel.ParallelType getParallelType() {
        if (mAsyncTaskFuture.getTask().getParallel() != null) {
            return mAsyncTaskFuture.getTask().getParallel().getType();
        } else {
            return TaskParallel.ParallelType.MAX_PARALLEL;
        }
    }

    /**
     * 获取执行者个数
     *
     * @return
     */
    public int getExecutorCount() {
        if (mAsyncTaskFuture.getTask().getParallel() != null) {
            return mAsyncTaskFuture.getTask().getParallel().getExecutorCount();
        } else {
            return 1;
        }
    }

}
