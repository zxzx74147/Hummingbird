package com.xbirder.bike.hummingbird.login;

import android.os.Bundle;
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
import com.xbirder.bike.hummingbird.common.widget.TitleBar;
import com.xbirder.bike.hummingbird.login.data.LoginData;
import com.xbirder.bike.hummingbird.main.MainActivity;
import com.xbirder.bike.hummingbird.register.RegisterActivity;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;
import com.xbirder.bike.hummingbird.util.StringHelper;


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
        mPassword = (EditText) root.findViewById(R.id.login_password);
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
                if(StringHelper.checkString(phone)&& StringHelper.checkString(password)) {
                    LoginRequest request = new LoginRequest(new HttpResponse.Listener<LoginData>() {
                        @Override
                        public void onResponse(HttpResponse<LoginData> response) {
                            if (response.isSuccess()) {
                                AccountManager.sharedInstance().setToken(response.result.user.accessToken);
                                ActivityJumpHelper.startActivity(LoginFragment.this, MainActivity.class);
                                getActivity().finish();
                            } else {
                                Toast.makeText(getActivity(), response.error.getMessage().toString(), Toast.LENGTH_SHORT);
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
