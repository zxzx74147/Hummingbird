package com.baidu.core.net.base;

/**
 * Created by chenrensong on 15/6/13.
 */
public class HttpLeakCanary {

    /**
     * 忽略HttpRequestProxy内存泄露
     * 由于是异步取消，所以可能存在误报泄露，但是其实不是泄露。
     *
     * @return
     */
    public static String getIgnoredClass() {
        return HttpRequestProxy.class.getName();
    }
}
