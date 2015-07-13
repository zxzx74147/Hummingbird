package com.xbirder.bike.hummingbird.login;

import com.android.volley.Request;
import com.baidu.core.net.base.HttpGsonRequest;
import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.config.NetworkConfig;
import com.xbirder.bike.hummingbird.login.data.LoginData;

import java.util.HashMap;

/**
 * Created by zhengxin on 2015/7/13.
 */
public class LoginRequest extends HttpGsonRequest<LoginData> {

    private String mPhoneNum;
    private String mPassword;
    public LoginRequest(HttpResponse.Listener<LoginData> listener) {
        super(listener);
    }

    public void setParam(String phoneNum,String password){
        this.mPhoneNum = phoneNum;
        this.mPassword = password;
    }

    @Override
    protected void onSetParameter(HashMap<String, String> params) {
        params.put("r",NetworkConfig.LOGIN_ADDRESS);
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
}
