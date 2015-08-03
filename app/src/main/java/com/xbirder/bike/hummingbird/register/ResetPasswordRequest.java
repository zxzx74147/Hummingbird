package com.xbirder.bike.hummingbird.register;

import com.android.volley.Request;
import com.baidu.core.net.base.HttpJsonRequest;
import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.config.NetworkConfig;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by zhengxin on 15/8/3.
 */
public class ResetPasswordRequest extends HttpJsonRequest<JSONObject> {

    private String mPhoneNum;
    private String mPassword;
    public ResetPasswordRequest(HttpResponse.Listener<JSONObject> listener) {
        super(listener);
    }

    public void setParam(String phoneNum,String password){
        this.mPhoneNum = phoneNum;
        this.mPassword = password;
    }

    @Override
    protected void onSetParameter(HashMap<String, String> params) {
        params.put("r", NetworkConfig.RESET_ADDRESS);
        params.put("token", AccountManager.sharedInstance().getToken());
        params.put("phone",mPhoneNum);
        params.put("password",mPassword);
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
