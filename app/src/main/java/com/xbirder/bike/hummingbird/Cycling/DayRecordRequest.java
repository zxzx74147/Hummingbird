package com.xbirder.bike.hummingbird.cycling;

import com.android.volley.Request;
import com.baidu.core.net.base.HttpJsonRequest;
import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.config.NetworkConfig;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by zhengxin on 2015/7/13.
 */
public class DayRecordRequest extends HttpJsonRequest<JSONObject> {

    private String mToken;
    private String mStart;
    private String mEnd;
    public DayRecordRequest(HttpResponse.Listener<JSONObject> listener) {
        super(listener);
    }

    public void setParam(String start,String end, String token){
        mToken = token;
        mStart = start;
        mEnd = end;
    }

    @Override
    protected void onSetParameter(HashMap<String, String> params) {
        params.put("r",NetworkConfig.GET_RECORD_DAY);
        params.put("token",mToken);
        params.put("start",mStart);
        params.put("end",mEnd);
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
