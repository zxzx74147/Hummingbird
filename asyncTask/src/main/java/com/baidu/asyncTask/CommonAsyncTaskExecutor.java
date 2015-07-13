/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.SparseIntArray;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AsyncTak内部执行器
 * Created by chenrensong on 15/02/03.
 */
/* package */ class CommonAsyncTaskExecutor implements Executor {
    private static final String LOG_TAG = "CommonAsyncTaskExecutor";

    private static final int TASK_MAX_TIME_ID = 0x1;
    private static final int TASK_RUN_NEXT_ID = 0x2;

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 256;
    private static final long KEEP_ALIVE = 30L;
    private static final int TASK_MAX_TIME = 3 * 60 * 1000;


    private static CommonAsyncTaskExecutor mInstance = null;

    static CommonAsyncTaskExecutor getInstance() {
        if (mInstance == null) {
            synchronized (CommonAsyncTaskExecutor.class) {
                if (mInstance == null) {
                    mInstance = new CommonAsyncTaskExecutor();
                }
            }
        }
        return mInstance;
    }


    private static final ThreadFactory mThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            String log = "AsyncTask #" + String.valueOf(mCount.getAndIncrement());
            Log.i(LOG_TAG, log);
            return new Thread(r, log);
        }
    };

    private static final BlockingQueue<Runnable> mPoolWorkQueue = new LinkedBlockingQueue<Runnable>();

    /**
     * TODO 考虑使用 ThreadPoolExecutor
     */
    private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            mPoolWorkQueue, mThreadFactory,
            new ThreadPoolExecutor.DiscardPolicy());

    private volatile int mRunningSuperHighTaskCount = 0;
    private volatile int mRunningHighTaskCount = 0;
    private volatile int mRunningMiddleTaskCount = 0;
    private volatile int mRunningLowTaskCount = 0;

    private final SparseIntArray mParallelMap = new SparseIntArray();
    private final LinkedList<CommonAsyncRunnable> mWaitingTasks = new LinkedList<CommonAsyncRunnable>();
    private final LinkedList<CommonAsyncRunnable> mRunningTasks = new LinkedList<CommonAsyncRunnable>();
    private final LinkedList<CommonAsyncRunnable> mTimeOutTasks = new LinkedList<CommonAsyncRunnable>();

    private HandlerThread mHandlerThread = null;
    private Handler mHandler = null;

   private CommonAsyncTaskExecutor() {
        mHandlerThread = new HandlerThread(LOG_TAG);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == TASK_MAX_TIME_ID) {
                    if (msg.obj != null && msg.obj instanceof CommonAsyncRunnable) {
                        taskTimeOut((CommonAsyncRunnable) (msg.obj));
                    }
                } else if (msg.what == TASK_RUN_NEXT_ID) {
                    if (msg.obj != null && msg.obj instanceof CommonAsyncRunnable) {
                        scheduleNext((CommonAsyncRunnable) (msg.obj));
                    }
                }
            }
        };
    }

    @Override
    public String toString() {
        return " WaitingTasks = " + mWaitingTasks.size()
                + " RunningTasks = " + mRunningTasks.size()
                + " TimeOutTasks = " + mTimeOutTasks.size();
    }


    @Override
    public synchronized void execute(Runnable r) {
        if (!(r instanceof CommonAsyncFutureTask)) {
            return;
        }
        CommonAsyncRunnable runnable = new CommonAsyncRunnable((CommonAsyncFutureTask<?>) r) {
            @Override
            public void run() {
                try {
                    try {
                        if (getPriority() == TaskPriority.SUPER_HIGH) {
                            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT - 2);
                        } else if (getPriority() == TaskPriority.HIGH) {
                            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT - 1);
                        } else if (getPriority() == TaskPriority.MIDDLE) {
                            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
                        } else {
                            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                        }
                    } catch (Exception ex) {
                        Log.e(LOG_TAG, ex.getMessage());
                    }
                    runTask();
                } finally {
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(TASK_RUN_NEXT_ID, this), 1);
                }
            }
        };
        insertTask(runnable);
        scheduleNext(null);
    }

    /**
     * 按照优先级插入任务
     *
     * @param runnable
     */
    private synchronized void insertTask(CommonAsyncRunnable runnable) {
        if (runnable == null) {
            return;
        }
        int num = mWaitingTasks.size();
        int index = 0;
        for (index = 0; index < num; index++) {
            if (mWaitingTasks.get(index).getPriority() < runnable.getPriority()) {
                break;
            }
        }
        mWaitingTasks.add(index, runnable);
    }

    /**
     * 任务运行超时
     *
     * @param task
     */
    private synchronized void taskTimeOut(CommonAsyncRunnable task) {
        removeRunningTask(task);
        if (task.isCancelled() == false) {
            task.setTimeout(true);
            mTimeOutTasks.add(task);
            // 终止任务需要时间，所以提前终止线程
            if (mTimeOutTasks.size() > MAXIMUM_POOL_SIZE - CORE_POOL_SIZE * 2) {
                CommonAsyncRunnable runnable = mTimeOutTasks.poll();
                if (runnable != null) {
                    runnable.cancelTask();
                }
            }
        } else {
            Log.e(LOG_TAG, "task TimeOut but it's cancelled()");
        }
        scheduleNext(null);
    }

    /**
     * 删除一个正在运行的任务
     *
     * @param task
     */
    private synchronized void removeRunningTask(CommonAsyncRunnable task) {
        if (task == null) {
            return;
        }
        if (task.IsTimeout() == true) {
            // 如果是超时运行的任务，直接在超时队列中移除
            mTimeOutTasks.remove(task);
        } else {
            // 如果是正在运行的任务，从运行队列删除，并且更新数量
            mRunningTasks.remove(task);
            mHandler.removeMessages(TASK_MAX_TIME_ID, task);
            switch (task.getPriority()) {
                case TaskPriority.SUPER_HIGH:
                    mRunningSuperHighTaskCount--;
                    break;
                case TaskPriority.HIGH:
                    mRunningHighTaskCount--;
                    break;
                case TaskPriority.MIDDLE:
                    mRunningMiddleTaskCount--;
                    break;
                case TaskPriority.LOW:
                    mRunningLowTaskCount--;
                    break;
                default:
                    break;
            }
            int tag = task.getParallelTag();
            if (tag != 0) {
                int num = mParallelMap.get(tag) - 1;
                if (num <= 0) {
                    mParallelMap.delete(tag);
                } else {
                    mParallelMap.put(tag, num);
                }
                if (num < 0) {
                    Log.e(LOG_TAG, "removeTask error < 0");
                }
            }
        }
    }

    /**
     * 移动队列中的一个任务，变为执行态
     *
     * @param task
     */
    private synchronized void executeTask(CommonAsyncRunnable task) {
        if (task == null) {
            return;
        }
        mRunningTasks.add(task);
        mWaitingTasks.remove(task);
        THREAD_POOL_EXECUTOR.execute(task);
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(TASK_MAX_TIME_ID, task), TASK_MAX_TIME);
        switch (task.getPriority()) {
            case TaskPriority.SUPER_HIGH:
                mRunningSuperHighTaskCount++;
                if (mRunningSuperHighTaskCount >= CORE_POOL_SIZE) {
                    Log.e(LOG_TAG, "Super High Task too much count = " + mRunningSuperHighTaskCount);
                }
                break;
            case TaskPriority.HIGH:
                mRunningHighTaskCount++;
                break;
            case TaskPriority.MIDDLE:
                mRunningMiddleTaskCount++;
                break;
            case TaskPriority.LOW:
                mRunningLowTaskCount++;
                break;
            default:
                break;
        }
        int tag = task.getParallelTag();
        if (tag != 0) {
            int num = mParallelMap.get(tag, 0) + 1;
            mParallelMap.put(tag, num);
        }
    }

    /**
     * 任务是否能并发运行
     *
     * @param activeCount
     * @param task
     * @return
     */
    private boolean canParallelExecute(int activeCount, CommonAsyncRunnable task) {
        if (task == null) {
            return false;
        }
        TaskParallel.ParallelType type = task.getParallelType();
        if (type == TaskParallel.ParallelType.SERIAL) {
            if (activeCount < 1) {
                return true;
            }
        } else if (type == TaskParallel.ParallelType.TWO_PARALLEL) {
            if (activeCount < 2) {
                return true;
            }
        } else if (type == TaskParallel.ParallelType.THREE_PARALLEL) {
            if (activeCount < 3) {
                return true;
            }
        } else if (type == TaskParallel.ParallelType.FOUR_PARALLEL) {
            if (activeCount < 4) {
                return true;
            }
        } else if (type == TaskParallel.ParallelType.CUSTOM_PARALLEL) {
            if (activeCount < task.getExecutorCount()) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    protected synchronized void scheduleNext(CommonAsyncRunnable current) {
        removeRunningTask(current);
        // 等待队列是优先级排好序的
        for (int i = 0; i < mWaitingTasks.size(); i++) {
            CommonAsyncRunnable task = mWaitingTasks.get(i);
            int parallelTag = task.getParallelTag();
            switch (task.getPriority()) {
                case TaskPriority.SUPER_HIGH:
                    // super优先级，如果并发度为最大，则立刻执行
                    if (parallelTag == 0) {
                        executeTask(task);
                        return;
                    }
                    break;
                case TaskPriority.HIGH:
                    // 如果遍历到高优先级任务，正在运行的任务数量超过CORE_POOL_SIZE，直接退出循环
                    if (mRunningHighTaskCount + mRunningMiddleTaskCount + mRunningLowTaskCount >= CORE_POOL_SIZE) {
                        return;
                    }
                    break;
                case TaskPriority.MIDDLE:
                    // 如果遍历到中优先级任务，正在运行的任务数量超过CORE_POOL_SIZE-1，直接退出循环
                    if (mRunningHighTaskCount + mRunningMiddleTaskCount + mRunningLowTaskCount >= CORE_POOL_SIZE - 1) {
                        return;
                    }
                    break;
                case TaskPriority.LOW:
                    // 如果遍历到底优先级任务，正在运行的任务数量超过CORE_POOL_SIZE-2，直接退出循环
                    if (mRunningHighTaskCount + mRunningMiddleTaskCount + mRunningLowTaskCount >= CORE_POOL_SIZE - 2) {
                        return;
                    }
                    break;
                default:
                    break;
            }

            int activeCount = mParallelMap.get(parallelTag);
            if (canParallelExecute(activeCount, task)) {
                executeTask(task);
                return;
            }
        }
    }

    public synchronized void removeAllTask(CommonUniqueId tag) {
        removeAllTask(tag, null);
    }

    public synchronized void removeAllTask(CommonUniqueId tag, String key) {
        removeAllWaitingTask(tag, key);
        removeTask(mRunningTasks, false, tag, key);
        removeTask(mTimeOutTasks, false, tag, key);
    }

    public synchronized void removeAllWaitingTask(CommonUniqueId tag) {
        removeAllWaitingTask(tag, null);
    }

    public synchronized void removeAllWaitingTask(CommonUniqueId tag, String key) {
        removeTask(mWaitingTasks, true, tag, key);
    }

    private synchronized void removeTask(LinkedList<CommonAsyncRunnable> tasks,
                                         boolean remove, CommonUniqueId tagData, String key) {
        if (tagData == null) {
            return;
        }
        int tag = tagData.getId();
        Iterator<CommonAsyncRunnable> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            CommonAsyncRunnable next = iterator.next();
            final int tmpTag = next.getTag();
            final String tmpKey = next.getKey();
            if ((key != null && tmpTag == tag && key.equals(tmpKey))
                    || (key == null && tag != 0 && tmpTag == tag)) {
                if (remove == true) {
                    iterator.remove();
                }
                next.cancelTask();
            }
        }
    }

    public synchronized void removeWaitingTask(CommonAsyncTask<?, ?, ?> task) {
        Iterator<CommonAsyncRunnable> iterator = mWaitingTasks.iterator();
        while (iterator.hasNext()) {
            CommonAsyncRunnable next = iterator.next();
            if (next != null && next.getTask() == task) {
                iterator.remove();
                break;
            }
        }
    }

    public int getTaskCount(CommonUniqueId tag) {
        return getTaskCount(null, tag);
    }

    public int getTaskCount(String key, CommonUniqueId tag) {
        return getQueueTaskCount(mWaitingTasks, key, tag)
                + getQueueTaskCount(mRunningTasks, key, tag)
                + getQueueTaskCount(mTimeOutTasks, key, tag);
    }

    private synchronized int getQueueTaskCount(LinkedList<CommonAsyncRunnable> list, String key, CommonUniqueId tagData) {
        if (list == null || tagData == null) {
            return 0;
        }
        int tag = tagData.getId();
        int num = 0;
        Iterator<CommonAsyncRunnable> iterator = list.iterator();
        while (iterator.hasNext()) {
            CommonAsyncRunnable next = iterator.next();
            final int tmpTag = next.getTag();
            final String tmpKey = next.getKey();
            if ((key != null && tmpTag == tag && key.equals(tmpKey))
                    || (key == null && tag != 0 && tmpTag == tag)) {
                if (next.getTask() != null && next.getTask().isCancelled() == false) {
                    num++;
                }
            }
        }
        return num;
    }

    public synchronized CommonAsyncTask<?, ?, ?> searchTask(String key) {
        CommonAsyncTask<?, ?, ?> tmp = null;
        tmp = searchTask(mWaitingTasks, key);
        if (tmp == null) {
            tmp = searchTask(mRunningTasks, key);
        }
        if (tmp == null) {
            tmp = searchTask(mTimeOutTasks, key);
        }
        return tmp;
    }

    public synchronized LinkedList<CommonAsyncTask<?, ?, ?>> searchAllTask(CommonUniqueId tag) {
        return searchAllTask(tag, null);
    }

    public synchronized LinkedList<CommonAsyncTask<?, ?, ?>> searchAllTask(CommonUniqueId tag, String key) {
        LinkedList<CommonAsyncTask<?, ?, ?>> ret = new LinkedList<CommonAsyncTask<?, ?, ?>>();
        LinkedList<CommonAsyncTask<?, ?, ?>> tmp = null;
        tmp = searchAllTask(mWaitingTasks, tag, key);
        if (tmp != null) {
            ret.addAll(tmp);
        }
        tmp = searchAllTask(mRunningTasks, tag, key);
        if (tmp != null) {
            ret.addAll(tmp);
        }
        tmp = searchAllTask(mTimeOutTasks, tag, key);
        if (tmp != null) {
            ret.addAll(tmp);
        }
        return ret;
    }


    // 搜索等待中的任务
    public synchronized CommonAsyncTask<?, ?, ?> searchWaitingTask(String key) {
        return searchTask(mWaitingTasks, key);
    }

    public synchronized LinkedList<CommonAsyncTask<?, ?, ?>> searchWaitingTask(CommonUniqueId tag) {
        LinkedList<CommonAsyncTask<?, ?, ?>> ret = new LinkedList<CommonAsyncTask<?, ?, ?>>();
        LinkedList<CommonAsyncTask<?, ?, ?>> tmp = searchAllTask(mWaitingTasks, tag, null);
        if (tmp != null) {
            ret.addAll(tmp);
        }
        return ret;
    }

    // 搜索执行中的任务
    public synchronized CommonAsyncTask<?, ?, ?> searchActivTask(String key) {
        return searchTask(mRunningTasks, key);
    }

    public synchronized CommonAsyncTask<?, ?, ?> searchTask(
            LinkedList<CommonAsyncRunnable> list, String key) {
        if (list == null || key == null) {
            return null;
        }
        Iterator<CommonAsyncRunnable> iterator = list.iterator();
        while (iterator.hasNext()) {
            CommonAsyncRunnable next = iterator.next();
            final String tmp = next.getKey();
            if (tmp != null && tmp.equals(key)
                    && next.getTask().isCancelled() == false) {
                return next.getTask();
            }
        }
        return null;
    }

    public synchronized LinkedList<CommonAsyncTask<?, ?, ?>> searchAllTask(
            LinkedList<CommonAsyncRunnable> list, CommonUniqueId tagData, String key) {
        if (list == null || tagData == null) {
            return null;
        }
        int tag = tagData.getId();
        LinkedList<CommonAsyncTask<?, ?, ?>> result = new LinkedList<CommonAsyncTask<?, ?, ?>>();
        Iterator<CommonAsyncRunnable> iterator = list.iterator();
        while (iterator.hasNext()) {
            CommonAsyncRunnable next = iterator.next();
            final int tmpTag = next.getTag();
            final String tmpKey = next.getKey();
            if ((key != null && tmpTag == tag && key.equals(tmpKey))
                    || (key == null && tag != 0 && tmpTag == tag)) {
                if (next.getTask() != null && next.getTask().isCancelled() == false) {
                    result.add(next.getTask());
                }
            }
        }
        return result;
    }


}
