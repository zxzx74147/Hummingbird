package com.baidu.core.net.base;

import android.text.TextUtils;
import android.text.format.DateUtils;

import com.android.volley.*;
import com.baidu.asyncTask.Task;
import com.baidu.cache.BdCacheService;
import com.baidu.cache.BdKVCache;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * Http Request
 * Created by chenrensong on 15/5/22.
 */
public abstract class HttpRequest<T> extends HttpRequestBase {

    public static final String COMMON_SPACE = "HttpRequest";

    public static final int COMMON_MAX = 50;


    protected HttpResponse.Listener<T> mListener;

    private Request mProxy;

    private RetryPolicy mRetryPolicy;

    private Object mTag;

    private BdKVCache<String> mCommonCache;

    private String mCacheUrl;

    private boolean mIsLoadCache;

    public void onErrorResponse(VolleyError error) {
        HttpResponse<T> response = HttpResponse.error(this, error);
        deliverResponse(response);
    }

    public abstract void onResponse(String response);

    public long expiredTimeInMills() {
        return DateUtils.MINUTE_IN_MILLIS;
    }

    public HttpRequest(HttpResponse.Listener<T> listener) {
        mListener = listener;
    }


    public void cleanCache() {
        if (mCacheUrl != null && !"".equals(mCacheUrl)) {
            getCache().remove(mCacheUrl);
        }
    }

    public Object getTag() {
        return mTag;
    }

    public BdKVCache<String> getCache() {
        if (mCommonCache != null) {
            return mCommonCache;
        }
        BdCacheService service = BdCacheService.sharedInstance();
        mCommonCache = service.getAndStartTextCache(COMMON_SPACE,
                BdCacheService.CacheStorage.SQLite_CACHE_PER_TABLE,
                BdCacheService.CacheEvictPolicy.LRU_ON_INSERT, COMMON_MAX);
        return mCommonCache;
    }


    /**
     * 发送请求</br>
     */
    void send() {
        this.send(false);
    }

    void send(boolean isLoadCache) {
        mIsLoadCache = isLoadCache;
        final int method = method();
        String url = url();
        HashMap<String, String> parameter = new HashMap<String, String>();
        onSetParameter(parameter);
        mCacheUrl = null;
        mCacheUrl = url + "?" + HttpManager.parseParams(parameter);
        /**添加通用参数**/
        HttpManager.paramsBuild(parameter);
        final String requestBody = HttpManager.parseParams(parameter);
        if (method == Request.Method.GET) {
            if (url.contains("?")) {
                url += "&" + requestBody;
            } else {
                url += "?" + requestBody;
            }
        }
//        System.out.println("===============" + url);
        final String requestUrl = url;
        if (isLoadCache) {
            getCache().asyncGet(mCacheUrl, new BdKVCache.BdCacheGetCallback<String>() {
                @Override
                public void onItemGet(String key, String value) {
                    if (TextUtils.isEmpty(value)) {
                        addRequestQueueInternal(method, requestUrl, requestBody);
                        return;
                    }
                    System.out.println("===============used cache...");
                    onResponse(value);
                }
            });
            return;
        }
        if (method == Request.Method.GET) {
            addRequestQueueInternal(method, requestUrl, requestBody);
        }else {
            addRequestQueueInternal(method, requestUrl, requestBody);
        }
    }


    private Response.Listener<String> mResponseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            if (isCancel()) {
                return;
            }
            HttpManager.finish(HttpRequest.this);
            if (mIsLoadCache) {
                if (!TextUtils.isEmpty(mCacheUrl)) {
                    getCache().asyncSet(mCacheUrl, response, expiredTimeInMills());
                }
            }
            HttpRequest.this.onResponse(response);
        }
    };

    private Response.ErrorListener mResponseErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (isCancel()) {
                return;
            }
            HttpManager.finish(HttpRequest.this);
            HttpRequest.this.onErrorResponse(error);
        }
    };

    private void addRequestQueueInternal(int method, String url, String requestBody) {
        mProxy = new HttpRequestProxy(method, url, requestBody, mResponseListener, mResponseErrorListener);
        if (mRetryPolicy != null) {
            mProxy.setRetryPolicy(mRetryPolicy);
        } else {
            mProxy.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 1.0F));
        }
        if (mTag != null) {
            mProxy.setTag(mTag);
        }
        HttpManager.addRequest(mProxy);
        HttpManager.sCurrentRequests.add(this);
    }


    /**
     * 设置参数
     *
     * @param params
     */
    protected abstract void onSetParameter(HashMap<String, String> params);

    /**
     * Deliver Response
     *
     * @param response
     */
    protected void deliverResponse(final HttpResponse<T> response) {
        if (mListener != null) {
            Task.run(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    if (mListener != null) {
                        mListener.onResponse(response);
                    }
                    return null;
                }
            }, Task.UI_EXECUTOR);

        }
    }

    public boolean isCancel() {
        if (mProxy == null || mProxy.isCanceled()) {
            return true;
        }
        return false;
    }

    /**
     * 取消操作
     */
    public void cancel() {
        mResponseListener = null;
        mResponseErrorListener = null;
        mListener = null;
        if (mProxy != null) {
            mProxy.cancel();
            mProxy = null;
        }
    }

    /**
     * 设置tag
     *
     * @param tag
     * @return
     */
    public void setTag(Object tag) {
        this.mTag = tag;
    }

    /**
     * 设置重试和超时时间
     * request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
     *
     * @param retryPolicy
     * @return
     */
    public HttpRequest<T> setRetryPolicy(RetryPolicy retryPolicy) {
        this.mRetryPolicy = retryPolicy;
        return this;
    }


    /**
     * 获取T Class
     *
     * @return
     */
    public Class<T> getTClass() {
        Class<T> entityClazz = null;
        Type t = getClass().getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            entityClazz = (Class<T>) p[0];
        }
        return entityClazz;
    }
}
