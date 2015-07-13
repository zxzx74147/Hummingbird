package com.baidu.core.net.base;

import com.android.volley.VolleyError;
import com.baidu.asyncTask.Task;
import org.json.JSONObject;

import java.util.concurrent.Callable;

/**
 * Http Request
 * Created by chenrensong on 15/5/19.
 */
public abstract class HttpJsonRequest<T> extends HttpRequest<T> {

    public HttpJsonRequest(HttpResponse.Listener<T> listener) {
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
                    deliverResponse(onResponseSuccess(new JSONObject(response)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    onErrorResponse(new VolleyError("HttpJsonRequest Parse Error", ex));
                }
                return null;
            }
        });
    }


    public abstract HttpResponse onResponseSuccess(JSONObject response) throws Exception;


}
