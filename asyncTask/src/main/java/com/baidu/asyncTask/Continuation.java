/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;

/**
 * 当一个任务完成后会执行此功能
 * Created by chenrensong on 15/02/04.
 *
 * @param <TResult>             上个任务的result
 * @param <TContinuationResult> 当前任务的result
 */
public interface Continuation<TResult, TContinuationResult> {
    TContinuationResult then(Task<TResult> task) throws Exception;
}
