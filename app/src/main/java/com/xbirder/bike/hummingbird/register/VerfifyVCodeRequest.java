package com.xbirder.bike.hummingbird.register;

import com.android.volley.Request;
import com.baidu.core.net.base.HttpJsonRequest;
import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.config.NetworkConfig;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by zhengxin on 15/8/3.
 */
public class VerfifyVCodeRequest extends HttpJsonRequest<JSONObject> {

    private String mPhoneNum;
    private String mVcode;
    public VerfifyVCodeRequest(HttpResponse.Listener<JSONObject> listener) {
        super(listener);
    }

    public void setParam(String phoneNum,String vcode){
        this.mPhoneNum = phoneNum;
        this.mVcode = vcode;
    }

    @Override
    protected void onSetParameter(HashMap<String, String> params) {
        params.put("r", NetworkConfig.LOGIN_ADDRESS);
        params.put("phone",mPhoneNum);
        params.put("code",mVcode);
    }

    @Override
    protected String url() {
        return NetworkConfig.SERVER_ADDRESS;
    }

    @Override
    protected int method() {
        return Request.Method.POST;
    }

    public HttpResponse onResponseSuccess(JSONObject response) throws Exception{
        return HttpResponse.success(this, response);
    }
}
