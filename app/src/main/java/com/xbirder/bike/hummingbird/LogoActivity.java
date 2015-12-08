package com.xbirder.bike.hummingbird;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.common.widget.MyVideoView;
import com.xbirder.bike.hummingbird.login.LoginActivity;
import com.xbirder.bike.hummingbird.main.MainActivity;
import com.xbirder.bike.hummingbird.register.RegisterActivity;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;


public class LogoActivity extends BaseActivity {

    private View mLoginView;
    private Button mRegisterView;
    private MyVideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        mVideoView = (MyVideoView) findViewById(R.id.logo_videoView);
        mVideoView.setVideoPath("android.resource://com.xbirder.bike.hummingbird/" + R.raw.login);
        //Uri uri = Uri.parse("android.resource://com.xbirder.bike.hummingbird/drawable/login.mp4");
        //Uri uri = Uri.parse("file:///android_asset/login.mp4");
        //mVideoView.setMediaController(new MediaController(LogoActivity.this));
        //mVideoView.setVideoURI(uri);
        mVideoView.start();

       //mVideoView.requestFocus();

        mLoginView = findViewById(R.id.btn_login);
        mRegisterView = (Button) findViewById(R.id.btn_reg);
        mLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityJumpHelper.startActivity(LogoActivity.this, LoginActivity.class);
            }
        });
        mRegisterView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityJumpHelper.startActivity(LogoActivity.this, RegisterActivity.class);
            }
        });

        String userName = AccountManager.sharedInstance().getUser();
        String pass = AccountManager.sharedInstance().getPass();
        if ((userName != null && userName != "") &&
                (pass != null && pass != "")) {
            ActivityJumpHelper.startActivity(this, MainActivity.class);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }
}
