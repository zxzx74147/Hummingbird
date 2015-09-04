package com.xbirder.bike.hummingbird.setting;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;

/**
 * Created by Administrator on 2015/9/4.
 */
public class XBirderHelp extends BaseActivity{

    private WebView mHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xbirder_help);
        mHelp = (WebView)findViewById(R.id.wv_help);
        mHelp.loadUrl("http://baidu.com");
        mHelp.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
}
