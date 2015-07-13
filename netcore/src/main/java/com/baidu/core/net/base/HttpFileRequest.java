package com.baidu.core.net.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.apache.http.Header;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.baidu.asyncTask.CommonAsyncTask;
import com.baidu.asyncTask.Continuation;
import com.baidu.asyncTask.Task;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import android.text.TextUtils;

/**
 * Created by chenrensong on 15/5/30.
 */
public abstract class HttpFileRequest extends HttpRequestBase {

    protected HttpResponse.Listener<PostResult> mListener;

    private SyncHttpClient mClient = new SyncHttpClient();

    protected abstract RequestParams customBuildParameter();

    private Object mTag;

    private PostAsyncTask mPostAsyncTask;

    public HttpFileRequest(HttpResponse.Listener<PostResult> listener) {
        mListener = listener;
    }

    void send(boolean isLoadCache) {
        send();
    }

    void send() {
        Task.runInBackground(new Callable<RequestParams>() {
            @Override
            public RequestParams call() throws Exception {
                RequestParams multipartEntityBuilder = customBuildParameter();
                HashMap<String, String> map = new HashMap<String, String>();
                HttpManager.paramsBuild(map);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    multipartEntityBuilder.add(entry.getKey(), entry.getValue());
                }
                return multipartEntityBuilder;
            }
        }).continueWith(new Continuation<RequestParams, Objects>() {
            @Override
            public Objects then(Task<RequestParams> task) throws Exception {
                HttpManager.sCurrentRequests.add(HttpFileRequest.this);
                mPostAsyncTask = new PostAsyncTask(task.getResult());
                mPostAsyncTask.execute();
                return null;
            }
        }, Task.UI_EXECUTOR);
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    @Override
    public Object getTag() {
        return mTag;
    }

    /**
     * 修改无效
     *
     * @return
     */
    @Override
    protected int method() {
        return Request.Method.POST;
    }

    @Override
    public void cancel() {
        if (mPostAsyncTask != null && !mPostAsyncTask.isCancelled()) {
            mPostAsyncTask.cancel();
            mPostAsyncTask = null;
        }
        mListener = null;
    }

    private class PostAsyncTask extends CommonAsyncTask<String, PostResult, PostResult> {
        private RequestParams mParams;
        private PostResult results = new PostResult();

        public PostAsyncTask(RequestParams multipartEntityBuilders) {
            mParams = multipartEntityBuilders;
        }

        @Override
        protected PostResult doInBackground(String... params) {
            if (mParams != null) {
                try {
                    mClient.post(url(), mParams, new TextHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                            super.onSuccess(statusCode, headers, responseBody);
                            PostResult postResult = new PostResult();
                            postResult.result = responseBody;
                            postResult.statusCode = statusCode;
                            results = postResult;
                        }

                        @Override
                        public void onFailure(String responseBody, Throwable error) {
                            PostResult postResult = new PostResult();
                            postResult.exception = error;
                            results = postResult;
                        }
                    });
                } catch (Exception ex) {

                }

            } else {
                PostResult postResult = new PostResult();
                postResult.exception = new Exception("MultipartEntityBuilder is Null");
                results = postResult;
            }
            return results;
        }

        @Override
        protected void onPostExecute(PostResult result) {
            boolean isSuccess = !TextUtils.isEmpty(result.result);
            if (mListener != null) {
                if (isSuccess) {
                    mListener.onResponse(HttpResponse.success(HttpFileRequest.this, results));
                } else {
                    HttpResponse response = HttpResponse.error(HttpFileRequest.this, new VolleyError(("fail")));
                    mListener.onResponse(response);
                }
            }

        }

    }
}
