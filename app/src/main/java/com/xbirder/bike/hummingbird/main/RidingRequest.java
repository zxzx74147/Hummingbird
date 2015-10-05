package com.xbirder.bike.hummingbird.main;

import com.android.volley.Request;
import com.baidu.core.net.base.HttpJsonRequest;
import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.config.NetworkConfig;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by zhengxin on 2015/7/13.
 */
public class RidingRequest extends HttpJsonRequest<JSONObject> {

    private String mToken;
    private String mDate;
    private String mDistance;
    private String mTime;
    public RidingRequest(HttpResponse.Listener<JSONObject> listener) {
        super(listener);
    }

    public void setParam(String date,String distance, String time, String token){
        mToken = token;
        mDate = date;
        mDistance = distance;
        mTime = time;
    }

    @Override
    protected void onSetParameter(HashMap<String, String> params) {
        params.put("r",NetworkConfig.ADD_RECORD);
        params.put("token",mToken);
        params.put("duration",mTime);
        params.put("distance",mDistance);
        params.put("date",mDate);
    }

    @Override
    protected String url() {
        return NetworkConfig.SERVER_ADDRESS;
    }

    @Override
    protected int method() {
        return Request.Method.GET;
    }

    public HttpResponse onResponseSuccess(JSONObject response) throws Exception{
        return HttpResponse.success(this, response);
    }
}
