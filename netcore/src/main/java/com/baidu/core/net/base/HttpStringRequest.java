package com.baidu.core.net.base;

import com.android.volley.VolleyError;
import com.baidu.asyncTask.Task;

import java.util.concurrent.Callable;

/**
 * Http Request
 * Created by chenrensong on 15/5/19.
 */
public abstract class HttpStringRequest<T> extends HttpRequest<T> {

    public HttpStringRequest(HttpResponse.Listener<T> listener) {
        super(listener);
    }

    @Override
    public void onResponse(final String response) {
        if (isCancel()) {
            return;
        }
        Task.runInBackground(new Callable<T>() {
            @Override
            public T call() throws Exception {
                if (isCancel()) {
                    return null;
                }
                try {
                   deliverResponse(HttpStringRequest.this.onResponseSuccess(response));
                } catch (Exception ex) {
                    onErrorResponse(new VolleyError("HttpStringRequest Parse Error", ex));
                }
                return null;
            }
        });
    }

    public abstract HttpResponse onResponseSuccess(String response) throws Exception;


}
