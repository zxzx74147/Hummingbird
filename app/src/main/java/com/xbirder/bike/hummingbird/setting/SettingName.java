package com.xbirder.bike.hummingbird.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.login.LoginActivity;
import com.xbirder.bike.hummingbird.main.MainActivity;
import com.xbirder.bike.hummingbird.register.ChangePassWord;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/8/22.
 */
public class SettingName extends BaseActivity {

    private EditText new_name;
    private Button btn_succeeds;
    private String et_new_name;
    private ChangeUserNameReuest changeUserNameReuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_name);
        new_name = (EditText) findViewById(R.id.new_name);
        btn_succeeds = (Button) findViewById(R.id.btn_succeeds);

        btn_succeeds.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btn_succeeds) {
                et_new_name = new_name.getText().toString();
                if (et_new_name.equals("")) {
                    toast("用户名不能为空,请重新输入");
                } else {
                    changeUserNameReuest = new ChangeUserNameReuest(new HttpResponse.Listener<JSONObject>() {
                        @Override
                        public void onResponse(HttpResponse<JSONObject> response) {
                            if (response.isSuccess()) {
                                try {
                                    if (response.result.getString("error").equals("0")) {
                                        AccountManager.sharedInstance().setUserName(et_new_name);
                                        Intent data = new Intent();
                                        data.putExtra("str", et_new_name);
                                        setResult(20, data);
                                        finish();
                                        toast("修改成功");
                                    } else {
                                        if (response.result.getString("error").equals("1") || response.result.getString("error").equals("2")) {
                                                toast("用户名重名,请重新修改");
                                        }
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }
                    });
                    changeUserNameReuest.setParam(et_new_name);
                    System.out.print("et_new_name : " + et_new_name);
                    sendRequest(changeUserNameReuest);
                }
            }
        }
    };
}
