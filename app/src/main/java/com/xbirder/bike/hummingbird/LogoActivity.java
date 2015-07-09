package com.xbirder.bike.hummingbird;

import android.os.Bundle;
import android.view.View;

import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.register.RegisterActivity;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;


public class LogoActivity extends BaseActivity {

    private View mLoginView;
    private View mRegisterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        mLoginView = findViewById(R.id.btn_login);
        mRegisterView = findViewById(R.id.btn_reg);
        mLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityJumpHelper.startActivity(LogoActivity.this, RegisterActivity.class);
            }
        });
        mRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityJumpHelper.startActivity(LogoActivity.this, RegisterActivity.class);
            }
        });
    }




}
