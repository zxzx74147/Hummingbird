/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;

/**
 * 唯一Id生成器
 * Created by chenrensong on 15/02/03.
 */
public class CommonUniqueId {
    private static final int MIN_ID = 1000000;
    private volatile static int mBaseId = 0;
    private int mId = 0;

    private CommonUniqueId() {
    }

    public static synchronized CommonUniqueId gen() {
        if (mBaseId < MIN_ID) {
            mBaseId = MIN_ID;
        }
        CommonUniqueId uniqueId = new CommonUniqueId();
        uniqueId.mId = mBaseId;
        mBaseId++;
        return uniqueId;
    }

    public int getId() {
        return mId;
    }

}