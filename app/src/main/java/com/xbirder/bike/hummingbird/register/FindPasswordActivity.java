package com.xbirder.bike.hummingbird.register;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.common.widget.TitleBar;
import com.xbirder.bike.hummingbird.login.widget.CountDownButton;
import com.xbirder.bike.hummingbird.util.StringHelper;

import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class FindPasswordActivity extends BaseActivity {

    private ViewPager mViewPager;
    private RegisterAdapter mAdapter;
    private View mStep1;
    private View mStep2;
    private EditText mStep1PhoneNum;
    private Button mStep1Code;
    private TitleBar mTitle;

    private EditText mCodeText;
    private EditText mPassText;
    private EditText mPassText2;
    private View mDone;
    private String mPhoneNum;
    private Handler mHandler;
    private CountDownButton mResend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTitle = (TitleBar) findViewById(R.id.title_bar);
        mStep1 = LayoutInflater.from(this).inflate(R.layout.fragment_find_password_1, null);
        mStep1PhoneNum = (EditText) mStep1.findViewById(R.id.reg_phone_num);
        mStep1Code = (Button) mStep1.findViewById(R.id.reg_send_code);

        mStep2 = LayoutInflater.from(this).inflate(R.layout.fragment_find_password_step_2, null);
        mPassText = (EditText) mStep2.findViewById(R.id.find_pass);
        mPassText2 = (EditText) mStep2.findViewById(R.id.find_pass_check);
        mDone = mStep2.findViewById(R.id.reg_done);
        mResend = (CountDownButton) mStep2.findViewById(R.id.resend);
        mCodeText = (EditText) mStep2.findViewById(R.id.reg_code_text);
        mAdapter = new RegisterAdapter();
        mViewPager.setAdapter(mAdapter);

        mStep1Code.setOnClickListener(mOnClickListener);
        mViewPager.addOnPageChangeListener(mOnPageChangedListener);
        mDone.setOnClickListener(mOnClickListener);
        mResend.setOnClickListener(mOnClickListener);
        initSMSSDK();
    }

    private void changePassword(){
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(new HttpResponse.Listener<JSONObject>() {
            @Override
            public void onResponse(HttpResponse<JSONObject> response) {
                finish();
            }
        });

        sendRequest(resetPasswordRequest);
    }

    private void requestVCode(){
        mPhoneNum = mStep1PhoneNum.getText().toString();
        if(!StringHelper.checkString(mPhoneNum)){
            return;
        }
        RequestVCodeRequest request = new RequestVCodeRequest(new HttpResponse.Listener<JSONObject>() {
            @Override
            public void onResponse(HttpResponse<JSONObject> response) {
                if(response.isSuccess()){
                    mViewPager.setCurrentItem(1);
                }else{
                    toast(response.error.toString());
                }
            }
        });
        request.setParam(mPhoneNum);
        sendRequest(request);
    }

    private void checkVCode(){
        String vCode = mCodeText.getText().toString();
        if(!StringHelper.checkString(vCode)){
            return;
        }
        VerfifyVCodeRequest request = new VerfifyVCodeRequest(new HttpResponse.Listener<JSONObject>() {
            @Override
            public void onResponse(HttpResponse<JSONObject> response) {
                changePassword();
            }
        });
        request.setParam(mPhoneNum,vCode);
        sendRequest(request);
    }

    private ViewPager.OnPageChangeListener mOnPageChangedListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 1) {
                mTitle.setLeftText(R.string.find_password);
                mTitle.setTitle(R.string.reset_password);
            } else {
                mTitle.setLeftText(R.string.login);
                mTitle.setTitle(R.string.find_password);

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    //只支持中国大陆
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mStep1Code || v == mResend) {
                mPhoneNum = mStep1PhoneNum.getText().toString();
                if(StringHelper.isPhoneNumberValid(mPhoneNum)){
                    SMSSDK.getVerificationCode("86", mPhoneNum);
                }else{
                    Toast.makeText(FindPasswordActivity.this, "手机号码不合法", Toast.LENGTH_SHORT);
                }
                mResend.startCountDown(System.currentTimeMillis()+60000);
            } else if (v == mDone) {

            } else if (v == mResend){
                if(mResend.isCountDown()){
                    return;
                }
                mPhoneNum = mStep1PhoneNum.getText().toString();
                if(StringHelper.isPhoneNumberValid(mPhoneNum)){
                    SMSSDK.getVerificationCode("86", mPhoneNum);
                }else{
                    Toast.makeText(FindPasswordActivity.this, "手机号码不合法", Toast.LENGTH_SHORT);
                }
                mResend.startCountDown(System.currentTimeMillis()+60000);
            }
        }
    };


    private class RegisterAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public void setPrimaryItem(android.view.ViewGroup container, int position, java.lang.Object object) {
            super.setPrimaryItem(container, position, object);

        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view;
            if (position == 0) {
                view = mStep1;
            } else {
                view = mStep2;
            }
            container.addView(view);
            return view;
        }
    }

    private void initSMSSDK() {
        SMSSDK.initSDK(this, SMSConfig.APPKEY, SMSConfig.APPSECRET);
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(final int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                changePassword();
                            }
                        });

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mViewPager.setCurrentItem(1);
                            }
                        });

                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else if(result == SMSSDK.RESULT_ERROR){
                    mHandler.post(new Runnable() {
                                      @Override
                                      public void run() {
                                          if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                                              toast("提交验证码失败");
                                          } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                              toast("获取验证码失败");

                                          }
                                      }
                                  }
                    );
                }else{
                    ((Throwable) data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }
}
