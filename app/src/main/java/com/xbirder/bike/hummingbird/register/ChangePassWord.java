package com.xbirder.bike.hummingbird.register;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.login.LoginActivity;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;
import com.xbirder.bike.hummingbird.util.StringHelper;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/8/19.
 */
public class ChangePassWord extends BaseActivity {

    private EditText et_old_pwd;
    private EditText et_new_pwd;
    private EditText et_center_new_pwd;
    private Button btn_succeed;
    private String storePass;
    private String mOldPwd;
    private String mNewPwd;
    private String mCenterNewPwd;
    private ChangeReuest changeRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        et_old_pwd = (EditText) findViewById(R.id.et_old_pwd);
        et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
        et_center_new_pwd = (EditText) findViewById(R.id.et_center_new_pwd);
        btn_succeed = (Button) findViewById(R.id.btn_succeed);
        btn_succeed.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener= new View.OnClickListener() {
        public void onClick(View v) {
            if (v == btn_succeed) {
                storePass = AccountManager.sharedInstance().getPass();//获取首选项里的密码.
                mOldPwd = et_old_pwd.getText().toString();//获取输入的密码
                //System.out.println("storePass ："+ storePass);
                mNewPwd = et_new_pwd.getText().toString();//获取新密码
                mCenterNewPwd = et_center_new_pwd.getText().toString();//获取再次输入的新密码
                if (!storePass.equals(mOldPwd)) {
                    toast("旧密码输入有误");
                    return;
                }
                if (mNewPwd.length() != 6) {
                    toast("新密码只支持六位数字");
                    return;
                }
                if (!mCenterNewPwd.equals(mNewPwd)) {
                    toast("两次密码输入不一致,请重新输入");
                    return;
                }
                if (mNewPwd.equals(mOldPwd)) {
                    toast("新密码与旧密码相同,请重新输入");
                    return;
                }

                if (StringHelper.checkString(mNewPwd)) {
                    final String mUser = AccountManager.sharedInstance().getUser();//获取帐号
                    changeRequest = new ChangeReuest(new HttpResponse.Listener<JSONObject>() {
                        @Override
                        public void onResponse(HttpResponse<JSONObject> response) {
                            if (response.isSuccess()) {
                                try {
                                    if (response.result.getString("error").equals("0")) {
                                        toast("修改成功");
                                        AccountManager.sharedInstance().setUser(mUser);
                                        AccountManager.sharedInstance().setPass(et_new_pwd.getText().toString());//设置新密码
                                        ActivityJumpHelper.startActivity(ChangePassWord.this, LoginActivity.class);
                                        finish();
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }
                    });
                    changeRequest.setParam(mUser, mOldPwd, mNewPwd);
                    sendRequest(changeRequest);
                }
            }
        }
    };
}
