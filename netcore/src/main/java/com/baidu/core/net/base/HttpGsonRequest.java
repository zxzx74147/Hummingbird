package com.baidu.core.net.base;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.baidu.asyncTask.Task;

import java.util.concurrent.Callable;

/**
 * Http Gson Request
 * Created by chenrensong on 15/5/19.
 */
public abstract class HttpGsonRequest<T> extends HttpRequest<T> {

    public HttpGsonRequest(HttpResponse.Listener<T> listener) {
        super(listener);
    }


    @Override
    public void onResponse(final String response) {
        if (isCancel()) {
            return;
        }
        Task.runInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if (isCancel()) {
                    return null;
                }
                try {
                    deliverResponse(onResponseSuccess(response));
                } catch (Exception ex) {
                    onErrorResponse(new VolleyError("HttpGsonRequest Parse Error", ex));
                }
                return null;
            }
        });
    }

    public HttpResponse onResponseSuccess(String response) throws Exception {
        T data = JSON.parseObject(response, getTClass());
        return HttpResponse.success(this, data);
    }


}
