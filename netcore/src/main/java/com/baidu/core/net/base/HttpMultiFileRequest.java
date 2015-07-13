package com.baidu.core.net.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.apache.http.Header;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.baidu.asyncTask.CommonAsyncTask;
import com.baidu.asyncTask.Continuation;
import com.baidu.asyncTask.Task;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * 这个会有多次回调
 * Created by chenrensong on 15/5/30.
 */
public abstract class HttpMultiFileRequest extends HttpRequestBase {

    protected HttpResponse.Listener<List<PostResult>> mListener;

    private SyncHttpClient mClient = new SyncHttpClient();

    protected abstract List<RequestParams> customBuildParameter();

    private PostAsyncTask mPostAsyncTask;

    public HttpMultiFileRequest(HttpResponse.Listener<List<PostResult>> listener) {
        mListener = listener;
    }

    private Object mTag;

    void send(boolean isLoadCache) {
        send();
    }

    void send() {
        Task.runInBackground(new Callable<List<RequestParams>>() {
            @Override
            public List<RequestParams> call() throws Exception {
                List<RequestParams> multipartEntityBuilder = customBuildParameter();
                for (RequestParams entityBuilder : multipartEntityBuilder) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    HttpManager.paramsBuild(map);
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        entityBuilder.add(entry.getKey(), entry.getValue());
                    }
                }
                return multipartEntityBuilder;
            }
        }).continueWith(new Continuation<List<RequestParams>, Objects>() {
            @Override
            public Objects then(Task<List<RequestParams>> task) throws Exception {
                HttpManager.sCurrentRequests.add(HttpMultiFileRequest.this);
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

    @Override
    public void cancel() {
        if (mPostAsyncTask != null && !mPostAsyncTask.isCancelled()) {
            mPostAsyncTask.cancel();
            mPostAsyncTask = null;
        }
        mListener = null;
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


    private class PostAsyncTask extends CommonAsyncTask<String, PostResult, Integer> {
        private List<RequestParams> mParams;
        private List<PostResult> results = new ArrayList<PostResult>();
        private int mCount = 0;

        public PostAsyncTask(List<RequestParams> multipartEntityBuilders) {
            mParams = multipartEntityBuilders;
        }

        @Override
        protected Integer doInBackground(String... params) {
            if (mParams != null) {
                for (RequestParams param : mParams) {
                    try {
                        RequestHandle handle = mClient.post(url(), param, new TextHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                                super.onSuccess(statusCode, headers, responseBody);
                                PostResult postResult = new PostResult();
                                postResult.result = responseBody;
                                postResult.statusCode = statusCode;
                                results.add(postResult);
                                mCount++;
                            }

                            @Override
                            public void onFailure(String responseBody, Throwable error) {
                                PostResult postResult = new PostResult();
                                postResult.exception = error;
                                results.add(postResult);
                            }
                        });

                    } catch (Exception ex) {
                        PostResult postResult = new PostResult();
                        postResult.exception = ex;
                        postResult.result = null;
                        results.add(postResult);
                    }
                }
            } else {
                PostResult postResult = new PostResult();
                postResult.exception = new Exception("MultipartEntityBuilder is Null");
                results.add(postResult);
                mCount = 0;
            }
            return mCount;
        }

        @Override
        protected void onPostExecute(Integer count) {
            HttpManager.finish(HttpMultiFileRequest.this);
            boolean isSuccess = false;
            if (count == mParams.size()) {
                isSuccess = true;
            }
            if (mListener != null) {
                if (isSuccess) {
                    mListener.onResponse(HttpResponse.success(HttpMultiFileRequest.this, results));
                } else {
                    HttpResponse response = HttpResponse.error(HttpMultiFileRequest.this, new VolleyError(
                            ("count is " + mParams.size() + "success " + count)));
                    mListener.onResponse(response);
                }
            }

        }

    }
}
