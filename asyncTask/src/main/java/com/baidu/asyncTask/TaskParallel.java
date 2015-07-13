/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;


import java.security.InvalidParameterException;


/**
 * Task 并行化配置
 * Created by chenrensong on 15/02/03.
 */
public class TaskParallel {

    /**
     * Async Parallel 类型
     */
    public enum ParallelType {
        /**
         * 多个任务并行
         */
        MAX_PARALLEL,

        /**
         * 两个任务并行
         */
        TWO_PARALLEL,

        /**
         * 三个任务并行
         */
        THREE_PARALLEL,

        /**
         * 四个任务并行
         */
        FOUR_PARALLEL,

        /**
         * 自定义个数，需要配置count参数，如果不配置，为串行
         */
        CUSTOM_PARALLEL,

        /**
         * 串行
         */
        SERIAL

    }


    private CommonUniqueId mAsyncTaskParallelTag = null;
    private TaskParallel.ParallelType mAsyncTaskParallelType = ParallelType.MAX_PARALLEL;
    private int mExecutorCount = 1;

    public TaskParallel(TaskParallel.ParallelType type, CommonUniqueId tag) {
        if (tag == null) {
            throw new InvalidParameterException("TaskParallel parameter null");
        }
        mAsyncTaskParallelType = type;
        mAsyncTaskParallelTag = tag;
    }

    public TaskParallel(CommonUniqueId tag, int executorCount) {
        if (tag == null) {
            throw new InvalidParameterException("TaskParallel parameter null");
        }
        mAsyncTaskParallelType = ParallelType.CUSTOM_PARALLEL;
        mExecutorCount = executorCount;
        mAsyncTaskParallelTag = tag;
    }

    public int getExecutorCount() {
        return mExecutorCount;
    }

    public int getTag() {
        return mAsyncTaskParallelTag.getId();
    }

    public TaskParallel.ParallelType getType() {
        return mAsyncTaskParallelType;
    }


}
