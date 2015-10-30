package com.xbirder.bike.hummingbird.register;

import com.android.volley.Request;
import com.baidu.core.net.base.HttpJsonRequest;
import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.config.NetworkConfig;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by zhengxin on 2015/7/13.
 */
public class RegisterV2Request extends HttpJsonRequest<JSONObject> {

    private String mPhoneNum;
    private String mPassword;
    private String mUserName;
    private String mCode;
    public RegisterV2Request(HttpResponse.Listener<JSONObject> listener) {
        super(listener);
    }

    public void setParam(String phoneNum,String password,String username,String code){
        this.mPhoneNum = phoneNum;
        this.mPassword = password;
        try {
            this.mUserName = URLEncoder.encode(username, "UTF-8");
        } catch (UnsupportedEncodingException exception) {
            this.mUserName = username;
        }
        this.mCode = code;
    }

    @Override
    protected void onSetParameter(HashMap<String, String> params) {
        String timeStr = String.valueOf(System.currentTimeMillis());
        params.put("r",NetworkConfig.REGISTER_NEW_ADDRESS);
        params.put("phone",mPhoneNum);
        params.put("password",mPassword);
        params.put("userName",mUserName);
        params.put("code", mCode);
        params.put("timestamp", timeStr);
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
