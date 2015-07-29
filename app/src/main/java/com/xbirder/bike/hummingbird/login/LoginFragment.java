package com.xbirder.bike.hummingbird.login;

import android.os.Bundle;
import android.text.method.DialerKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseFragment;
import com.xbirder.bike.hummingbird.bluetooth.BluetoothScanActivity;
import com.xbirder.bike.hummingbird.common.widget.TitleBar;
import com.xbirder.bike.hummingbird.login.data.LoginData;
import com.xbirder.bike.hummingbird.main.MainActivity;
import com.xbirder.bike.hummingbird.register.RegisterActivity;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;
import com.xbirder.bike.hummingbird.util.StringHelper;

import org.json.JSONObject;


/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends BaseFragment {

    private EditText mPhoneNum;
    private EditText mPassword;
    private TitleBar mTitleBar;
    private ImageButton mStartButton;
    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_login, container, false);

        mPhoneNum = (EditText) root.findViewById(R.id.login_phone_num);
        String storePhone = AccountManager.sharedInstance().getUser();
        if (storePhone != null && storePhone != "") {
            mPhoneNum.setText(storePhone);
        }

        mPassword = (EditText) root.findViewById(R.id.login_password);
        String storePass = AccountManager.sharedInstance().getPass();
        if (storePass != null && storePass != "") {
            mPassword.setText(storePass);
        }
        mPassword.setKeyListener(DialerKeyListener.getInstance());

        mTitleBar = (TitleBar) root.findViewById(R.id.title_bar);
        mStartButton = (ImageButton) root.findViewById(R.id.btn_start);
        mStartButton.setOnClickListener(mOnClickListener);
        mTitleBar.setRigntOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityJumpHelper.startActivity(LoginFragment.this,RegisterActivity.class);
            }
        });
        return root;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == mStartButton){
                String phone = mPhoneNum.getText().toString();
                String password = mPassword.getText().toString();

                if (password.length() != 6) {
                    toast("密码只支持六位数字");
                    return;
                }

                //无网络情况下，可以直接跳过服务器验证
                String storePhone = AccountManager.sharedInstance().getUser();
                String storePass = AccountManager.sharedInstance().getPass();

                if (storePhone != null && storePhone != "") {
                    if (storePass != null && storePass != "") {
                        if (storePass.equals(password) && storePhone.equals(phone)) {
                            ActivityJumpHelper.startActivity(LoginFragment.this, BluetoothScanActivity.class);
                            getActivity().finish();
                            return;
                        }
                    }
                }

                if(StringHelper.checkString(phone)&& StringHelper.checkString(password)) {
                    LoginRequest request = new LoginRequest(new HttpResponse.Listener<JSONObject>() {
                        @Override
                        public void onResponse(HttpResponse<JSONObject> response) {
                            if (response.isSuccess()) {
                                try {
                                    if (response.result.getString("error").equals("0")) {
                                        AccountManager.sharedInstance().setUser(mPhoneNum.getText().toString());
                                        AccountManager.sharedInstance().setPass(mPassword.getText().toString());
//                          AccountManager.sharedInstance().setToken(response.result.user.accessToken);
                                        ActivityJumpHelper.startActivity(LoginFragment.this, BluetoothScanActivity.class);
                                        getActivity().finish();
                                    } else {
                                        toast("登陆失败");
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }
                    });
                    request.setParam(phone,password);
                    sendRequest(request);
                }
            }
        }
    };
}
