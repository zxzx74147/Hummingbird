/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 通用 Async Task
 * Created by chenrensong on 15/02/03.
 *
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 */
public abstract class CommonAsyncTask<Params, Progress, Result> {
    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;
    private static final CommonAsyncTaskExecutor mDefaultExecutor = CommonAsyncTaskExecutor.getInstance();
    private static final ImmediateExecutor mImmediateExecutor = ImmediateExecutor.getInstance();

    private static final InternalHandler mInternalHandler = new InternalHandler(Looper.getMainLooper());
    private final WorkerRunnable<Params, Result> mWorker;
    private final CommonAsyncFutureTask<Result> mFuture;

    private volatile Status mStatus = Status.PENDING;

    private int mPriority = TaskPriority.LOW;
    private int mTag = 0;
    private String mKey = null;
    private TaskParallel mParallel = null;
    /**
     * 是否立刻执行
     */
    private boolean mIsImmediate = false;
    private final AtomicBoolean mTaskInvoked = new AtomicBoolean(false);
    private final AtomicBoolean mPreCancelInvoked = new AtomicBoolean(false);
    private boolean mIsTimeout = false;

    public enum Status {
        PENDING, RUNNING, FINISHED,
    }

    public CommonAsyncTask() {
        mWorker = new WorkerRunnable<Params, Result>() {
            @Override
            public Result call() throws Exception {
                if (!mFuture.isCancelled()) {
                    return postResult(doInBackground(mParams));
                } else {
                    return postResult(null);
                }
            }
        };

        mFuture = new CommonAsyncFutureTask<Result>(mWorker, this) {
            @Override
            protected void done() {
                try {
                    final Result result = get();
                    postResult(result);
                } catch (InterruptedException e) {
                    //线程中断异常
                } catch (ExecutionException e) {
                    postResult(null);
                } catch (CancellationException e) {
                    postResult(null);
                } catch (Throwable t) {
                    throw new RuntimeException("An error occured while executing " + "doInBackground()", t);
                }
            }

            @Override
            protected void cancelTask() {
                CommonAsyncTask.this.cancel();
            }
        };
    }

    public synchronized int setPriority(int priority) {
        if (mStatus != Status.PENDING) {
            throw new IllegalStateException("the task is already running");
        }
        int old = mPriority;
        mPriority = priority;
        return old;
    }

    public int getPriority() {
        return mPriority;
    }

    public int getTag() {
        return mTag;
    }

    public synchronized int setTag(CommonUniqueId tag) {
        if (mStatus != Status.PENDING) {
            throw new IllegalStateException("the task is already running");
        }
        int tmp = mTag;
        if (tag != null) {
            mTag = tag.getId();
        }
        return tmp;
    }

    public String getKey() {
        return mKey;
    }

    public synchronized String setKey(String key) {
        if (mStatus != Status.PENDING) {
            throw new IllegalStateException("the task is already running");
        }
        String tmp = mKey;
        mKey = key;
        return tmp;
    }

    public TaskParallel getParallel() {
        return mParallel;
    }

    public synchronized void setParallel(TaskParallel parallel) {
        if (mStatus != Status.PENDING) {
            throw new IllegalStateException("the task is already running");
        }
        mParallel = parallel;
    }

    public boolean isImmediate() {
        return mIsImmediate;
    }

    public synchronized void setIsImmediate(boolean isImmediate) {
        if (mStatus != Status.PENDING) {
            throw new IllegalStateException("the task is already running");
        }
        this.mIsImmediate = isImmediate;
    }

    synchronized void setTimeout(boolean isTimeout) {
        mIsTimeout = isTimeout;
    }

    public boolean isTimeout() {
        return mIsTimeout;
    }

    @SuppressWarnings("unchecked")
    private Result postResult(Result result) {
        if (mTaskInvoked.compareAndSet(false, true)) {
            Message message = mInternalHandler.obtainMessage(MESSAGE_POST_RESULT,
                    new CommonAsyncTaskResult<Result>(this, result));
            message.sendToTarget();
            return result;
        } else {
            return null;
        }
    }

    public final Status getStatus() {
        return mStatus;
    }

    protected abstract Result doInBackground(Params... params);

    public void cancel() {
        cancel(true);
    }

    protected void onPreCancel() {
    }

    protected void onPreExecute() {
    }

    protected void onPostExecute(Result result) {
    }

    protected void onProgressUpdate(Progress... values) {
    }

    protected void onCancelled(Result result) {
        onCancelled();
    }

    protected void onCancelled() {
    }

    public final boolean isCancelled() {
        return mFuture.isCancelled();
    }

    public synchronized final boolean cancel(boolean mayInterruptIfRunning) {
        if (!mIsImmediate) {
            mDefaultExecutor.removeWaitingTask(this);
        }
        boolean ret = mFuture.cancel(mayInterruptIfRunning);
        if (mPreCancelInvoked.compareAndSet(false, true)) {
            onPreCancel();
        }
        return ret;
    }

    public final Result get() throws InterruptedException, ExecutionException {
        return mFuture.get();
    }

    public final Result get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return mFuture.get(timeout, unit);
    }

    public final CommonAsyncTask<Params, Progress, Result> execute(Params... params) {
        Executor executor = mDefaultExecutor;
        if (mFuture.getTask().isImmediate()) {
            executor = mImmediateExecutor;
        }
        return executeOnExecutor(executor, params);
    }

    public synchronized final CommonAsyncTask<Params, Progress, Result> executeOnExecutor(Executor executor, Params... params) {
        if (mStatus != Status.PENDING) {
            switch (mStatus) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task:" + " the task is already running.");
                case FINISHED:
                    throw new IllegalStateException("Cannot execute task:" + " the task has already been executed "
                            + "(a task can be executed only once)");
                default:
                    break;
            }
        }
        mStatus = Status.RUNNING;
        onPreExecute();
        mWorker.mParams = params;
        executor.execute(mFuture);
        return this;
    }

    protected final void publishProgress(Progress... values) {
        if (!isCancelled()) {
            mInternalHandler.obtainMessage(MESSAGE_POST_PROGRESS,
                    new CommonAsyncTaskResult<Progress>(this, values)).sendToTarget();
        }
    }

    private void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
        mStatus = Status.FINISHED;
    }

    private static class InternalHandler extends Handler {
        public InternalHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            CommonAsyncTaskResult result = (CommonAsyncTaskResult) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    // There is only one result
                    result.mTask.finish(result.mData[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }

    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;
    }

    private static class CommonAsyncTaskResult<Data> {
        final CommonAsyncTask mTask;
        final Data[] mData;

        CommonAsyncTaskResult(CommonAsyncTask task, Data... data) {
            mTask = task;
            mData = data;
        }
    }

    public static void removeAllTask(CommonUniqueId tag) {
        mDefaultExecutor.removeAllTask(tag);
    }

    public static void removeAllTask(CommonUniqueId tag, String key) {
        mDefaultExecutor.removeAllTask(tag, key);
    }

    public static void removeAllWaitingTask(CommonUniqueId tag) {
        mDefaultExecutor.removeAllWaitingTask(tag);
    }

    public static void removeAllWaitingTask(CommonUniqueId tag, String key) {
        mDefaultExecutor.removeAllWaitingTask(tag, key);
    }

    public static LinkedList<CommonAsyncTask<?, ?, ?>> searchAllTask(CommonUniqueId tag) {
        return mDefaultExecutor.searchAllTask(tag);
    }

    public static LinkedList<CommonAsyncTask<?, ?, ?>> searchAllTask(CommonUniqueId tag, String key) {
        return mDefaultExecutor.searchAllTask(tag, key);
    }

    public static CommonAsyncTask<?, ?, ?> searchTask(String key) {
        return mDefaultExecutor.searchTask(key);
    }

    public static CommonAsyncTask<?, ?, ?> searchWaitingTask(String key) {
        return mDefaultExecutor.searchWaitingTask(key);
    }

    public static LinkedList<CommonAsyncTask<?, ?, ?>> searchWaitingTask(CommonUniqueId tag) {
        return mDefaultExecutor.searchWaitingTask(tag);
    }

    // 搜索执行中的任务
    public static CommonAsyncTask<?, ?, ?> searchActivTask(String key) {
        return mDefaultExecutor.searchActivTask(key);
    }

    public static int getTaskCount(CommonUniqueId tag) {
        return getTaskCount(null, tag);
    }

    public static int getTaskCount(String key, CommonUniqueId tag) {
        return mDefaultExecutor.getTaskCount(key, tag);
    }
    // end
}
