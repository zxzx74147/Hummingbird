package com.xbirder.bike.hummingbird.base;

import android.support.v7.app.AppCompatActivity;

import com.baidu.asyncTask.CommonUniqueId;
import com.baidu.core.net.base.HttpManager;
import com.baidu.core.net.base.HttpRequestBase;
import com.xbirder.bike.hummingbird.skin.SkinConfig;
import com.xbirder.bike.hummingbird.skin.SkinManager;

/**
 * Created by zhengxin on 15/7/6.
 */
public class BaseActivity extends AppCompatActivity {

    protected int mSkinMode = SkinConfig.SKIN_MODE_DAY;

    private CommonUniqueId mUniqueId = CommonUniqueId.gen();

    @Override
    protected void onResume() {
        super.onResume();
        if(mSkinMode != SkinManager.sharedInstance().getSkinMode()){
            initView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void initView(){
        mSkinMode = SkinManager.sharedInstance().getSkinMode();
    }

    protected void initData(){

    }

    protected void sendRequest(HttpRequestBase request, boolean isLoadCache) {
        HttpManager.sendRequest(mUniqueId, request, isLoadCache);
    }

    protected void sendRequest(HttpRequestBase request) {
        sendRequest(request, false);
    }
}
