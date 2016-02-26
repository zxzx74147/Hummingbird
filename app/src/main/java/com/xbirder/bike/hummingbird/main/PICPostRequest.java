package com.xbirder.bike.hummingbird.main;

import com.android.volley.Request;
import com.baidu.core.net.base.HttpFileRequest;
import com.baidu.core.net.base.HttpResponse;
import com.baidu.core.net.base.PostResult;
import com.loopj.android.http.RequestParams;
import com.xbirder.bike.hummingbird.config.NetworkConfig;

import org.json.JSONObject;

/**
 * Created by zhengxin on 2015/7/13.
 */
public class PICPostRequest extends HttpFileRequest {

    private String mCityName;
    public PICPostRequest(HttpResponse.Listener<PostResult> listener) {
        super(listener);
    }

    public void setParam(String cityName){
        mCityName = cityName;

    }

    @Override
    protected RequestParams customBuildParameter() {
        RequestParams params = new RequestParams();
        params.put("r",NetworkConfig.VATAR_ADDRESS);
        return params;
    }

    @Override
    protected String url() {
        return NetworkConfig.SERVER_ADDRESS_DEV;
    }

    @Override
    protected int method() {
        return Request.Method.GET;
    }

    public HttpResponse onResponseSuccess(JSONObject response) throws Exception{
        return HttpResponse.success(this, response);
    }
}
