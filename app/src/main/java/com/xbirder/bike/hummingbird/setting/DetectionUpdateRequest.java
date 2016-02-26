package com.xbirder.bike.hummingbird.setting;

import com.android.volley.Request;
import com.baidu.core.net.base.HttpJsonRequest;
import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.config.NetworkConfig;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/8/19.
 */
public class DetectionUpdateRequest extends HttpJsonRequest<JSONObject> {

    private String mCount;

    public DetectionUpdateRequest(HttpResponse.Listener<JSONObject> listener) {
        super(listener);
    }

    public void setParam(String count) {
        try {
            this.mCount = URLEncoder.encode(count, "UTF-8");
        } catch (UnsupportedEncodingException exception) {
            this.mCount = count;
        }
    }

    @Override
    protected void onSetParameter(HashMap<String, String> params) {
        params.put("r", NetworkConfig.FIRMWARE_VERSION_ADDRESS);
        params.put("count", mCount);

        //System.out.print("mUserName : " + AccountManager.sharedInstance().getToken());
    }

    @Override
    protected String url() {
        return NetworkConfig.SERVER_ADDRESS_DEV;
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
