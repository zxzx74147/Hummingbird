package com.baidu.cache;

import android.app.Application;

/**
 * Created by chenrensong on 15/5/26.
 */
public class BdCacheManger {

    private static Application mApp;

    static Application getApp() {
        return mApp;
    }

    public static void init(Application app) {
        mApp = app;
    }
}
