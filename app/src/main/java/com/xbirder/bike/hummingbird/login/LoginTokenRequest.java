package com.xbirder.bike.hummingbird.login;

import com.android.volley.Request;
import com.baidu.core.net.base.HttpJsonRequest;
import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.config.NetworkConfig;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by zhengxin on 2015/7/13.
 */
public class LoginTokenRequest extends HttpJsonRequest<JSONObject> {

    private String mToken;
    public LoginTokenRequest(HttpResponse.Listener<JSONObject> listener) {
        super(listener);
    }

    public void setParam(String token){
        this.mToken = token;
    }

    @Override
    protected void onSetParameter(HashMap<String, String> params) {
        params.put("r",NetworkConfig.LOGIN_ADDRESS);
        params.put("token",mToken);
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
