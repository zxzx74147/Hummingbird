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
import com.xbirder.bike.hummingbird.AccountManager;
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
        mResend.setTextString(getResources().getString(R.string.resent));
    }

    private void changePassword(){
        String newPass = mPassText.getText().toString();
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(new HttpResponse.Listener<JSONObject>() {
            @Override
            public void onResponse(HttpResponse<JSONObject> response) {
                finish();
            }
        });
        resetPasswordRequest.setParam(mPhoneNum,newPass);
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
        mResend.startCountDown(System.currentTimeMillis() + 60000);
        mViewPager.setCurrentItem(1);
    }

    private void checkVCode(){
        String vCode = mCodeText.getText().toString();
        if(!StringHelper.checkString(vCode)){
            return;
        }
        VerfifyVCodeRequest request = new VerfifyVCodeRequest(new HttpResponse.Listener<JSONObject>() {
            @Override
            public void onResponse(HttpResponse<JSONObject> response) {
                String token = response.result.optString("token");
                AccountManager.sharedInstance().setToken(token);
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
                if(v == mResend&& mResend.isCountDown()){
                    return;
                }
                mPhoneNum = mStep1PhoneNum.getText().toString();
                if(StringHelper.isPhoneNumberValid(mPhoneNum)){
                    requestVCode();

                }else{
                    Toast.makeText(FindPasswordActivity.this, "手机号码不合法", Toast.LENGTH_SHORT);
                }
                mResend.startCountDown(System.currentTimeMillis()+60000);
            } else if (v == mDone) {
                checkVCode();
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


}
