package com.xbirder.bike.hummingbird.register;

import com.android.volley.Request;
import com.baidu.core.net.base.HttpJsonRequest;
import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.config.NetworkConfig;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/8/19.
 */
public class ChangeReuest extends HttpJsonRequest<JSONObject> {

    private String mPhoneNum;
    private String oldPassword;
    private String mNewPassword;
    public ChangeReuest(HttpResponse.Listener<JSONObject> listener) {
        super(listener);
    }

    public void setParam(String phoneNum,String oldPassword,String password){
        this.mPhoneNum = phoneNum;
        this.oldPassword = oldPassword;
        this.mNewPassword = password;
    }

    @Override
    protected void onSetParameter(HashMap<String, String> params) {
        params.put("r", NetworkConfig.AMEND_ADDRESS);
        params.put("phone",mPhoneNum);
        params.put("old",oldPassword);
        params.put("new",mNewPassword);
    }

    @Override
    protected String url() {
        return NetworkConfig.SERVER_ADDRESS;
    }

    @Override
    protected int method() {
        return Request.Method.GET;
    }


    @Override
    public HttpResponse onResponseSuccess(JSONObject response) throws Exception {
        return HttpResponse.success(this, response);
    }
}
