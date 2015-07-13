/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;

/**
 * Common Async Task 优先级
 * Created by chenrensong on 15/02/03.
 */
public final class TaskPriority {
    /**
     * 低优先级
     */
    public static final int LOW = 1;
    /**
     * 中优先级
     */
    public static final int MIDDLE = 2;
    /**
     * 高优先级
     */
    public static final int HIGH = 3;
    /**
     * 超高优先级(独立于其他线程之外执行)
     */
    public static final int SUPER_HIGH = 4;
}
