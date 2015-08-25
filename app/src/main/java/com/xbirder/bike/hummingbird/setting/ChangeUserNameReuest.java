package com.xbirder.bike.hummingbird.setting;

import com.android.volley.Request;
import com.baidu.core.net.base.HttpJsonRequest;
import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.config.NetworkConfig;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/8/19.
 */
public class ChangeUserNameReuest extends HttpJsonRequest<JSONObject> {

    private String mUserName;
    public ChangeUserNameReuest(HttpResponse.Listener<JSONObject> listener) {
        super(listener);
    }

    public void setParam(String userName){
        this.mUserName = userName;
    }

    @Override
    protected void onSetParameter(HashMap<String, String> params) {
        params.put("r", NetworkConfig.USERNAME_ADDRESS);
        params.put("token", AccountManager.sharedInstance().getToken());

        params.put("userName",mUserName);
        System.out.print("mUserName : " + AccountManager.sharedInstance().getToken());
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
