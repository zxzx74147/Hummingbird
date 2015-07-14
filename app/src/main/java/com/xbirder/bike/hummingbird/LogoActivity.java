package com.xbirder.bike.hummingbird;

import android.os.Bundle;
import android.view.View;

import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.login.LoginActivity;
import com.xbirder.bike.hummingbird.main.MainActivity;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;
import com.xbirder.bike.hummingbird.util.StringHelper;


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
                ActivityJumpHelper.startActivity(LogoActivity.this, LoginActivity.class);
            }
        });
        mRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityJumpHelper.startActivity(LogoActivity.this, MainActivity.class);
            }
        });
        String token = AccountManager.sharedInstance().getToken();
        if(StringHelper.checkString(token)){
            ActivityJumpHelper.startActivity(this,MainActivity.class);
            finish();
        }
    }




}
