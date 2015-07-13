/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;

/**
 * 用于捕获匿名类的方法变量
 * Created by chenrensong on 15/02/04.
 */
public class Capture<T> {
    private T value;

    public Capture() {
    }

    public Capture(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
