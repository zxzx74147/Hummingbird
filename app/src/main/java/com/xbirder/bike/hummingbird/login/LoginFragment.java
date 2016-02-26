package com.xbirder.bike.hummingbird.login;

import android.os.Bundle;
import android.text.method.DialerKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseFragment;
import com.xbirder.bike.hummingbird.common.widget.TitleBar;
import com.xbirder.bike.hummingbird.main.MainActivity;
import com.xbirder.bike.hummingbird.register.FindPasswordActivity;
import com.xbirder.bike.hummingbird.register.RegisterActivity;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;
import com.xbirder.bike.hummingbird.util.CustomAlertDialog;
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
    private View mFindPassword;
    public LoginFragment() {
    }

    private boolean isSafeLogin = false;
    private CustomAlertDialog detectionUpdateRunCustomAlertDialog;

    private ProgressBar detectionUpdate_progressbar;
    private TextView detectionUpdate_complete_lines;
    private TextView detectionUpdate_total_lines;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_login, container, false);

//        VideoView mVideoView = (VideoView) root.findViewById(R.id.login_videoView);
//        Uri uri = Uri.parse("android.resource://com.xbirder.bike.hummingbird/drawable/login.mp4");
//       // Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/login.mp4");
//        mVideoView.setMediaController(new MediaController(getActivity()));
//        mVideoView.setVideoURI(uri);
//        mVideoView.start();

        mFindPassword = root.findViewById(R.id.find_password);
        mFindPassword.setOnClickListener(mOnClickListener);

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



//        //获取CheckBox实例
//        CheckBox cb = (CheckBox) root.findViewById(R.id.cb);
//        //绑定监听器
//        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//                // TODO Auto-generated method stub
//                isSafeLogin = arg1;
////                if(arg1){
////
////                }else{
////
////                }
//
//            }
//        });
//
//
//
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
                            if(isSafeLogin){


                                return;
                            }

                            ActivityJumpHelper.startActivity(LoginFragment.this, MainActivity.class);
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
                                        String accessToken = response.result.getJSONObject("user").getString("accessToken");
                                        AccountManager.sharedInstance().setToken(accessToken);
                                        String userName = response.result.getJSONObject("user").getString("userName");
                                        AccountManager.sharedInstance().setUserName(userName);
                                        String avatar = response.result.getJSONObject("user").getString("avatar");
                                        AccountManager.sharedInstance().setAvatarName(avatar);

                                        ActivityJumpHelper.startActivity(LoginFragment.this, MainActivity.class);
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
            }else if(v == mFindPassword){
                ActivityJumpHelper.startActivity(LoginFragment.this, FindPasswordActivity.class);
            }
        }
    };


    private void showDetectionUpdateING(){
        detectionUpdateRunCustomAlertDialog = new CustomAlertDialog(getActivity());
        detectionUpdateRunCustomAlertDialog.showDialog(R.layout.custom_alert_dialog_update_run, new CustomAlertDialog.IHintDialog() {
            @Override
            public void onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //detectionUpdateRunCustomAlertDialog.dismissDialog();
                }
            }

            @Override
            public void showWindowDetail(Window window) {
                detectionUpdate_progressbar = (ProgressBar) window.findViewById(R.id.progressbar);
                detectionUpdate_complete_lines = (TextView) window.findViewById(R.id.complete_lines);
                detectionUpdate_total_lines = (TextView) window.findViewById(R.id.total_lines);
            }
        });
    }


}
