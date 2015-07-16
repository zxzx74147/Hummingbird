package com.xbirder.bike.hummingbird.register;

import android.app.Activity;
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
import com.xbirder.bike.hummingbird.base.BaseFragment;
import com.xbirder.bike.hummingbird.common.widget.TitleBar;
import com.xbirder.bike.hummingbird.login.data.LoginData;
import com.xbirder.bike.hummingbird.main.MainActivity;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * A placeholder fragment containing a simple view.
 */
public class RegisterActivityFragment extends BaseFragment {

    private static final String APPKEY = "7ce25c956c7e";
    private static final String APPSECRET = "a12a69ecf677908cc77955a7dffedb0e";

    private ViewPager mViewPager;
    private ImageAdapter mAdapter;
    private View mStep1;
    private View mStep2;
    private EditText mStep1PhoneNum;
    private Button mStep1Code;
    private TitleBar mTitle;

    private EditText mCodeText;
    private EditText mPassText;
    private EditText mUserNameText;
    private View mDone;
    private String mPhoneNum;
    private Handler mHandler;
    private View mResend;

    public RegisterActivityFragment() {
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_regiester, container, false);
        mViewPager = (ViewPager) mRoot.findViewById(R.id.view_pager);
        mStep1 = inflater.inflate(R.layout.fragment_register_step_1, null);
        mStep2 = inflater.inflate(R.layout.fragment_register_step_2, null);
        mStep1PhoneNum = (EditText) mStep1.findViewById(R.id.reg_phone_num);
        mStep1Code = (Button) mStep1.findViewById(R.id.reg_send_code);
        mTitle = (TitleBar) mRoot.findViewById(R.id.title_bar);
        mDone = mStep2.findViewById(R.id.reg_done);
        mResend = mStep2.findViewById(R.id.resend);
        mUserNameText = (EditText) mStep2.findViewById(R.id.reg_username);
        mCodeText = (EditText) mStep2.findViewById(R.id.reg_code_text);
        mPassText = (EditText) mStep2.findViewById(R.id.reg_pass);
        mAdapter = new ImageAdapter();
        mViewPager.setAdapter(mAdapter);
        mStep1Code.setOnClickListener(mOnClickListener);
        mViewPager.addOnPageChangeListener(mOnPageChangedListener);
        mDone.setOnClickListener(mOnClickListener);
        mResend.setOnClickListener(mOnClickListener);

        mTitle.setBackOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mViewPager.getCurrentItem() == 0) {
                    getActivity().finish();
                } else {
                    mViewPager.setCurrentItem(0);
                }
            }
        });

        return mRoot;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initSMSSDK();
    }

    private ViewPager.OnPageChangeListener mOnPageChangedListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 1) {
                mTitle.setLeftText(R.string.register);
                mTitle.setTitle(R.string.register_account_info);
            } else {
                mTitle.setLeftText(R.string.back);
                mTitle.setTitle(R.string.register_by_phone);

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
                if(isPhoneNumberValid(mPhoneNum)){
                    SMSSDK.getVerificationCode("86", mPhoneNum);
                }else{
                    Toast.makeText(getActivity(),"手机号码不合法",Toast.LENGTH_SHORT);
                }
            } else if (v == mDone) {
                String code = mCodeText.getText().toString();
                SMSSDK.submitVerificationCode("86",mPhoneNum,code);
            }
        }
    };

    private class ImageAdapter extends PagerAdapter {
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

    private void register() {
        String username = mUserNameText.getText().toString();
        String pass = mPassText.getText().toString();
        String phone = mStep1PhoneNum.getText().toString();
        RegisterRequest request = new RegisterRequest(new HttpResponse.Listener<LoginData>() {
            @Override
            public void onResponse(HttpResponse<LoginData> response) {
                if (response.isSuccess()) {
                    AccountManager.sharedInstance().setToken(response.result.user.accessToken);
                    ActivityJumpHelper.startActivity(RegisterActivityFragment.this, MainActivity.class);
                    getActivity().finish();
                }
            }
        });
        request.setParam(phone, username, pass);
        sendRequest(request);
    }


    private void initSMSSDK() {
        SMSSDK.initSDK(getActivity(), APPKEY, APPSECRET);
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(final int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                toast("提交验证码成功");
                                register();
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

    public static boolean isPhoneNumberValid(String phoneNumber) {

        boolean isValid = false;
        /*
         * 可接受的电话格式有：
         */
        String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
        /*
         * 可接受的电话格式有：
         */
        String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);

        Pattern pattern2 = Pattern.compile(expression2);
        Matcher matcher2 = pattern2.matcher(inputStr);
        if (matcher.matches() || matcher2.matches()) {
            isValid = true;
        }
        return isValid;
    }

}
