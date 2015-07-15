package com.xbirder.bike.hummingbird.register;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.core.net.base.HttpResponse;
import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseFragment;
import com.xbirder.bike.hummingbird.common.widget.TitleBar;
import com.xbirder.bike.hummingbird.login.data.LoginData;
import com.xbirder.bike.hummingbird.main.MainActivity;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class RegisterActivityFragment extends BaseFragment {

    public static final String APP_KEY = "7ce25c956c7e";
    public static final String APP_SECRET = "a12a69ecf677908cc77955a7dffedb0e";

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



    public RegisterActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_regiester, container, false);
        mViewPager = (ViewPager) mRoot.findViewById(R.id.view_pager);
        mStep1 = inflater.inflate(R.layout.fragment_register_step_1,null);
        mStep2 = inflater.inflate(R.layout.fragment_register_step_2,null);
        mStep1PhoneNum = (EditText) mStep1.findViewById(R.id.reg_phone_num);
        mStep1Code = (Button) mStep1.findViewById(R.id.reg_send_code);
        mTitle = (TitleBar) mRoot.findViewById(R.id.title_bar);
        mDone = mStep2.findViewById(R.id.reg_done);
        mUserNameText = (EditText) mStep2.findViewById(R.id.reg_username);
        mCodeText = (EditText) mStep2.findViewById(R.id.reg_code_text);
        mPassText = (EditText) mStep2.findViewById(R.id.reg_pass);
        mAdapter = new ImageAdapter();
        mViewPager.setAdapter(mAdapter);
        mStep1Code.setOnClickListener(mOnClickListener);
        mViewPager.addOnPageChangeListener(mOnPageChangedListener);
        mDone.setOnClickListener(mOnClickListener);
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

    private ViewPager.OnPageChangeListener mOnPageChangedListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(position == 1){
                mTitle.setLeftText(R.string.register);
                mTitle.setTitle(R.string.register_account_info);
            }else{
                mTitle.setLeftText(R.string.back);
                mTitle.setTitle(R.string.register_by_phone);

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == mStep1Code){
                mViewPager.setCurrentItem(1);
            }else if(v == mDone){
                register();
            }
        }
    };

    private class ImageAdapter extends PagerAdapter{
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
            if(position == 0){
                view = mStep1;
            }else{
                view = mStep2;
            }
            container.addView(view);
            return view;
        }
    }

    private void register(){
        String username = mUserNameText.getText().toString();
        String pass = mPassText.getText().toString();
        String phone = mStep1PhoneNum.getText().toString();
        RegisterRequest request = new RegisterRequest(new HttpResponse.Listener<LoginData>() {
            @Override
            public void onResponse(HttpResponse<LoginData> response) {
                if(response.isSuccess()){
                    AccountManager.sharedInstance().setToken(response.result.user.accessToken);
                    ActivityJumpHelper.startActivity(RegisterActivityFragment.this, MainActivity.class);
                    getActivity().finish();
                }
            }
        });
        request.setParam(phone,username,pass);
        sendRequest(request);
    }
}
