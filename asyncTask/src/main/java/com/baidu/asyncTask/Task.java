/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务执行器(类似.net await async)
 * Created by chenrensong on 15/02/04.
 */
public class Task<TResult> {

    /**
     * 后台执行器
     */
    public static final Executor BACKGROUND_EXECUTOR = ThreadPoolExecutor.getInstance();

    /**
     * UI执行器
     */
    public static final Executor UI_EXECUTOR = UIThreadExecutor.getInstance();

    /**
     * 立刻执行器（不需要排队）
     */
    public static final Executor IMMEDIATE_EXECUTOR = ImmediateExecutor.getInstance();

    /**
     * 当前线程执行
     */
    private static final Executor CURRENT_EXECUTOR = CurrentThreadExecutor.getInstance();

    private final Object lock = new Object();
    private boolean mComplete;
    private boolean mCancelled;
    private TResult mResult;
    private Exception mError;
    private List<Continuation<TResult, Void>> mContinuations;

    private Task() {
        mContinuations = new ArrayList<Continuation<TResult, Void>>();
    }


    public static <TResult> Task<TResult>.TaskCompletionSource create() {
        Task<TResult> task = new Task<TResult>();
        return task.new TaskCompletionSource();
    }

    /**
     * 等待任务完成
     *
     * @throws InterruptedException
     */
    public void waitForCompletion() throws InterruptedException {
        synchronized (lock) {
            while (!isCompleted()) {
                lock.wait();
            }
        }
    }

    /**
     * 获取任务是否完成
     *
     * @return
     */
    public boolean isCompleted() {
        synchronized (lock) {
            return mComplete;
        }
    }


    /**
     * 获取任务是否取消
     *
     * @return
     */
    public boolean isCancelled() {
        synchronized (lock) {
            return mCancelled;
        }
    }


    /**
     * 获取任务是否Faulted
     *
     * @return
     */
    public boolean isFaulted() {
        synchronized (lock) {
            return mError != null;
        }
    }

    /**
     * 获取任务结果
     *
     * @return
     */
    public TResult getResult() {
        synchronized (lock) {
            return mResult;
        }
    }

    /**
     * 获取错误
     *
     * @return
     */
    public Exception getError() {
        synchronized (lock) {
            return mError;
        }
    }


    public static <TResult> Task<TResult> forResult(TResult value) {
        Task<TResult>.TaskCompletionSource tcs = Task.create();
        tcs.setResult(value);
        return tcs.getTask();
    }

    public static <TResult> Task<TResult> forError(Exception error) {
        Task<TResult>.TaskCompletionSource tcs = Task.create();
        tcs.setError(error);
        return tcs.getTask();
    }

    public static <TResult> Task<TResult> cancelled() {
        Task<TResult>.TaskCompletionSource tcs = Task.create();
        tcs.setCancelled();
        return tcs.getTask();
    }

    public <TOut> Task<TOut> cast() {
        @SuppressWarnings("unchecked")
        Task<TOut> task = (Task<TOut>) this;
        return task;
    }

    public Task<Void> makeVoid() {
        return this.continueWithTask(new Continuation<TResult, Task<Void>>() {
            @Override
            public Task<Void> then(Task<TResult> task) throws Exception {
                if (task.isCancelled()) {
                    return Task.cancelled();
                }
                if (task.isFaulted()) {
                    return Task.forError(task.getError());
                }
                return Task.forResult(null);
            }
        });
    }

    /**
     * 执行在后台线程
     *
     * @param callable
     * @param <TResult>
     * @return
     */
    public static <TResult> Task<TResult> runInBackground(Callable<TResult> callable) {
        return run(callable, BACKGROUND_EXECUTOR);
    }

    public static <TResult> Task<TResult> run(final Callable<TResult> callable, Executor executor) {
        final Task<TResult>.TaskCompletionSource tcs = Task.create();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    tcs.setResult(callable.call());
                } catch (Exception e) {
                    tcs.setError(e);
                }
            }
        });
        return tcs.getTask();
    }

    public static <TResult> Task<TResult> run(final Callable<TResult> callable) {
        return run(callable, CURRENT_EXECUTOR);
    }


    public static <TResult> Task<List<TResult>> whenAllResult(final Collection<? extends Task<TResult>> tasks) {
        return whenAll(tasks).onSuccess(new Continuation<Void, List<TResult>>() {
            @Override
            public List<TResult> then(Task<Void> task) throws Exception {
                if (tasks.size() == 0) {
                    return Collections.emptyList();
                }

                List<TResult> results = new ArrayList<TResult>();
                for (Task<TResult> individualTask : tasks) {
                    results.add(individualTask.getResult());
                }
                return results;
            }
        });
    }


    public static Task<Void> whenAll(Collection<? extends Task<?>> tasks) {
        if (tasks.size() == 0) {
            return Task.forResult(null);
        }

        final Task<Void>.TaskCompletionSource allFinished = Task.create();
        final ArrayList<Exception> causes = new ArrayList<Exception>();
        final Object errorLock = new Object();
        final AtomicInteger count = new AtomicInteger(tasks.size());
        final AtomicBoolean isCancelled = new AtomicBoolean(false);

        for (Task<?> task : tasks) {
            @SuppressWarnings("unchecked")
            Task<Object> t = (Task<Object>) task;
            t.continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) {
                    if (task.isFaulted()) {
                        synchronized (errorLock) {
                            causes.add(task.getError());
                        }
                    }

                    if (task.isCancelled()) {
                        isCancelled.set(true);
                    }

                    if (count.decrementAndGet() == 0) {
                        if (causes.size() != 0) {
                            if (causes.size() == 1) {
                                allFinished.setError(causes.get(0));
                            } else {
                                Exception error = new AggregateException(
                                        String.format("There were %d exceptions.", causes.size()),
                                        causes);
                                allFinished.setError(error);
                            }
                        } else if (isCancelled.get()) {
                            allFinished.setCancelled();
                        } else {
                            allFinished.setResult(null);
                        }
                    }
                    return null;
                }
            });
        }

        return allFinished.getTask();
    }

    public Task<Void> continueWhile(Callable<Boolean> predicate,
                                    Continuation<Void, Task<Void>> continuation) {
        return continueWhile(predicate, continuation, CURRENT_EXECUTOR);
    }

    public Task<Void> continueWhile(final Callable<Boolean> predicate,
                                    final Continuation<Void, Task<Void>> continuation, final Executor executor) {
        final Capture<Continuation<Void, Task<Void>>> predicateContinuation =
                new Capture<Continuation<Void, Task<Void>>>();
        predicateContinuation.set(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                if (predicate.call()) {
                    return Task.<Void>forResult(null).onSuccessTask(continuation, executor)
                            .onSuccessTask(predicateContinuation.get(), executor);
                }
                return Task.forResult(null);
            }
        });
        return makeVoid().continueWithTask(predicateContinuation.get(), executor);
    }

    public <TContinuationResult> Task<TContinuationResult> continueWith(
            final Continuation<TResult, TContinuationResult> continuation, final Executor executor) {
        boolean completed;
        final Task<TContinuationResult>.TaskCompletionSource tcs = Task.create();
        synchronized (lock) {
            completed = this.isCompleted();
            if (!completed) {
                this.mContinuations.add(new Continuation<TResult, Void>() {
                    @Override
                    public Void then(Task<TResult> task) {
                        completeImmediately(tcs, continuation, task, executor);
                        return null;
                    }
                });
            }
        }
        if (completed) {
            completeImmediately(tcs, continuation, this, executor);
        }
        return tcs.getTask();
    }

    public <TContinuationResult> Task<TContinuationResult> continueWith(
            Continuation<TResult, TContinuationResult> continuation) {
        return continueWith(continuation, CURRENT_EXECUTOR);
    }

    public <TContinuationResult> Task<TContinuationResult> continueWithTask(
            final Continuation<TResult, Task<TContinuationResult>> continuation, final Executor executor) {
        boolean completed;
        final Task<TContinuationResult>.TaskCompletionSource tcs = Task.create();
        synchronized (lock) {
            completed = this.isCompleted();
            if (!completed) {
                this.mContinuations.add(new Continuation<TResult, Void>() {
                    @Override
                    public Void then(Task<TResult> task) {
                        completeAfterTask(tcs, continuation, task, executor);
                        return null;
                    }
                });
            }
        }
        if (completed) {
            completeAfterTask(tcs, continuation, this, executor);
        }
        return tcs.getTask();
    }

    public <TContinuationResult> Task<TContinuationResult> continueWithTask(
            Continuation<TResult, Task<TContinuationResult>> continuation) {
        return continueWithTask(continuation, CURRENT_EXECUTOR);
    }

    public <TContinuationResult> Task<TContinuationResult> onSuccess(
            final Continuation<TResult, TContinuationResult> continuation, Executor executor) {
        return continueWithTask(new Continuation<TResult, Task<TContinuationResult>>() {
            @Override
            public Task<TContinuationResult> then(Task<TResult> task) {
                if (task.isFaulted()) {
                    return Task.forError(task.getError());
                } else if (task.isCancelled()) {
                    return Task.cancelled();
                } else {
                    return task.continueWith(continuation);
                }
            }
        }, executor);
    }


    public <TContinuationResult> Task<TContinuationResult> onSuccess(
            final Continuation<TResult, TContinuationResult> continuation) {
        return onSuccess(continuation, CURRENT_EXECUTOR);
    }


    public <TContinuationResult> Task<TContinuationResult> onSuccessTask(
            final Continuation<TResult, Task<TContinuationResult>> continuation, Executor executor) {
        return continueWithTask(new Continuation<TResult, Task<TContinuationResult>>() {
            @Override
            public Task<TContinuationResult> then(Task<TResult> task) {
                if (task.isFaulted()) {
                    return Task.forError(task.getError());
                } else if (task.isCancelled()) {
                    return Task.cancelled();
                } else {
                    return task.continueWithTask(continuation);
                }
            }
        }, executor);
    }


    public <TContinuationResult> Task<TContinuationResult> onSuccessTask(
            final Continuation<TResult, Task<TContinuationResult>> continuation) {
        return onSuccessTask(continuation, CURRENT_EXECUTOR);
    }

    private static <TContinuationResult, TResult> void completeImmediately(
            final Task<TContinuationResult>.TaskCompletionSource tcs,
            final Continuation<TResult, TContinuationResult> continuation, final Task<TResult> task,
            Executor executor) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TContinuationResult result = continuation.then(task);
                    tcs.setResult(result);
                } catch (Exception e) {
                    tcs.setError(e);
                }
            }
        });
    }

    private static <TContinuationResult, TResult> void completeAfterTask(
            final Task<TContinuationResult>.TaskCompletionSource tcs,
            final Continuation<TResult, Task<TContinuationResult>> continuation,
            final Task<TResult> task, final Executor executor) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Task<TContinuationResult> result = continuation.then(task);
                    if (result == null) {
                        tcs.setResult(null);
                    } else {
                        result.continueWith(new Continuation<TContinuationResult, Void>() {
                            @Override
                            public Void then(Task<TContinuationResult> task) {
                                if (task.isCancelled()) {
                                    tcs.setCancelled();
                                } else if (task.isFaulted()) {
                                    tcs.setError(task.getError());
                                } else {
                                    tcs.setResult(task.getResult());
                                }
                                return null;
                            }
                        });
                    }
                } catch (Exception e) {
                    tcs.setError(e);
                }
            }
        });
    }

    private void runContinuations() {
        synchronized (lock) {
            for (Continuation<TResult, ?> continuation : mContinuations) {
                try {
                    continuation.then(this);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            mContinuations = null;
        }
    }

    public class TaskCompletionSource {
        private TaskCompletionSource() {
        }

        public Task<TResult> getTask() {
            return Task.this;
        }

        public boolean trySetCancelled() {
            synchronized (lock) {
                if (mComplete) {
                    return false;
                }
                mComplete = true;
                mCancelled = true;
                lock.notifyAll();
                runContinuations();
                return true;
            }
        }

        public boolean trySetResult(TResult result) {
            synchronized (lock) {
                if (mComplete) {
                    return false;
                }
                mComplete = true;
                Task.this.mResult = result;
                lock.notifyAll();
                runContinuations();
                return true;
            }
        }

        public boolean trySetError(Exception error) {
            synchronized (lock) {
                if (mComplete) {
                    return false;
                }
                mComplete = true;
                Task.this.mError = error;
                lock.notifyAll();
                runContinuations();
                return true;
            }
        }

        public void setCancelled() {
            if (!trySetCancelled()) {
                throw new IllegalStateException("Cannot cancel a completed task.");
            }
        }

        public void setResult(TResult result) {
            if (!trySetResult(result)) {
                throw new IllegalStateException("Cannot set the result of a completed task.");
            }
        }

        public void setError(Exception error) {
            if (!trySetError(error)) {
                throw new IllegalStateException("Cannot set the error on a completed task.");
            }
        }
    }
}
