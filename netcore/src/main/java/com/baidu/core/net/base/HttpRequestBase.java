package com.baidu.core.net.base;

/**
 * Created by chenrensong on 15/5/30.
 */
public abstract class HttpRequestBase {
    /**
     * 默认的Url
     *
     * @return
     */
    protected abstract String url();

    protected abstract int method();

    abstract void send();

    abstract void send(boolean isLoadCache);

    public abstract void setTag(Object tag);

    public abstract Object getTag();

    public abstract void cancel();
}
