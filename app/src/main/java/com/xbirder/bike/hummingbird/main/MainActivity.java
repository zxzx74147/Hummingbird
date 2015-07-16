package com.xbirder.bike.hummingbird.main;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.fonts.FontsManager;
import com.xbirder.bike.hummingbird.main.widget.BatteryRollView;
import com.xbirder.bike.hummingbird.setting.SettingActivity;
import com.xbirder.bike.hummingbird.skin.SkinConfig;
import com.xbirder.bike.hummingbird.skin.SkinManager;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;

public class MainActivity extends BaseActivity {

    private TextView mSpeedText;
//    private TextView mKMText;
    private ImageView mButtonE;
    private ImageView mButtonN;
    private ImageView mButtonS;
    private TextView mTextE;
    private TextView mTextN;
    private TextView mTextS;
    private BatteryRollView mBatteryRollView;
    private TextView mBatteryView;
    private TextView mBatteryShow;
    private DrawerLayout mDrawerLayout;
    private View mSettingView;
    private View mLightView;
    private View mSideSetting;
    private ImageView mLockView;
    private boolean isLock = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(){
        super.initView();
        setContentView(R.layout.activity_main);
        mSpeedText = (TextView) findViewById(R.id.speed_num);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSettingView = findViewById(R.id.main_setting);
        mLockView = (ImageView) findViewById(R.id.lock_top);
        mLightView = findViewById(R.id.main_light);
        mSideSetting = findViewById(R.id.setting_layout);
//        mKMText = (TextView) findViewById(R.id.km_text);
        mBatteryShow = (TextView) findViewById(R.id.battery_show);
        mButtonE = (ImageView) findViewById(R.id.mode_e);
        mButtonN = (ImageView) findViewById(R.id.mode_n);
        mButtonS = (ImageView) findViewById(R.id.mode_s);
        mTextE = (TextView) findViewById(R.id.mode_e_text);
        mTextN = (TextView) findViewById(R.id.mode_n_text);
        mTextS = (TextView) findViewById(R.id.mode_s_text);
        mBatteryRollView = (BatteryRollView) findViewById(R.id.roll_view);
        mBatteryView = (TextView) findViewById(R.id.battery_num);
        mSpeedText.setIncludeFontPadding(false);
        mButtonE.setOnClickListener(mOnClickListener);
        mButtonN.setOnClickListener(mOnClickListener);
        mButtonS.setOnClickListener(mOnClickListener);
        mLockView.setOnClickListener(mOnClickListener);
        mSettingView.setOnClickListener(mOnClickListener);
        mLightView.setOnClickListener(mOnClickListener);
        mSideSetting.setOnClickListener(mOnClickListener);
        FontsManager.sharedInstance().setSpeedType(mSpeedText);
        FontsManager.sharedInstance().setSpeedType(mBatteryView);
        FontsManager.sharedInstance().setSpeedKMType(mBatteryShow);
        setBattery(35);
        setMode(StatusConfig.MODE_E);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == mButtonE){
                setMode(StatusConfig.MODE_E);
            }else if(v == mButtonN){
                setMode(StatusConfig.MODE_N);
            }else if(v == mButtonS){
                setMode(StatusConfig.MODE_S);
            }else if(v == mSettingView){
                if(!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }else{
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
            }else if(v == mLockView){
                if(isLock){
                    unLock();
                }else{
                    lock();
                }
                isLock = !isLock;
            }else if(v == mLightView){
                int mode = SkinManager.sharedInstance().getSkinMode();
                if(mode == SkinConfig.SKIN_MODE_DAY){
                    SkinManager.sharedInstance().setSkinMode(SkinConfig.SKIN_MODE_NIGHT);
                }else{
                    SkinManager.sharedInstance().setSkinMode(SkinConfig.SKIN_MODE_DAY);
                }
                initView();
            }else if(v == mSideSetting){
                ActivityJumpHelper.startActivity(MainActivity.this, SettingActivity.class);
            }
        }
    };

    private void lock(){
        mLockView.setImageResource(R.drawable.lock_green);
        mLockView.setScaleType(ImageView.ScaleType.FIT_START);
        //TODO 发送蓝牙指令
    }

    private void unLock(){
        mLockView.setImageResource(R.drawable.lock_red);
        mLockView.setScaleType(ImageView.ScaleType.FIT_END);
        //TODO 发送蓝牙指令
    }

    private void setMode(int mode){
        mButtonE.setImageBitmap(getTranBitmap());
        mButtonN.setImageBitmap(getTranBitmap());
        mButtonS.setImageBitmap(getTranBitmap());
        mTextE.setEnabled(false);
        mTextN.setEnabled(false);
        mTextS.setEnabled(false);
        TypedArray ta;
        Drawable drawable;
        int[] attrs;
        switch (mode){
            case StatusConfig.MODE_E:
                attrs = new int[] { R.attr.btn_e_drawable};
                ta = this.obtainStyledAttributes(attrs);
                drawable = ta.getDrawable(0);
                ta.recycle();
                mButtonE.setImageDrawable(drawable);
                mTextE.setEnabled(true);
                break;
            case StatusConfig.MODE_N:
                attrs = new int[] { R.attr.btn_n_drawable};
                ta = this.obtainStyledAttributes(attrs);
                drawable = ta.getDrawable(0);
                ta.recycle();
                mButtonN.setImageDrawable(drawable);
                mTextN.setEnabled(true);
                break;
            case StatusConfig.MODE_S:
                attrs = new int[] { R.attr.btn_s_drawable};
                ta = this.obtainStyledAttributes(attrs);
                drawable = ta.getDrawable(0);
                ta.recycle();
                mButtonS.setImageDrawable(drawable);
                mTextS.setEnabled(true);
                break;
        }
        //TODO 发送蓝牙指令
    }


    private void setSpeed(int speed){
        mSpeedText.setText(String.valueOf(speed));
    }

    private void setBattery(int battery){
        mBatteryRollView.setPercent(battery);
        mBatteryView.setText(String.valueOf(battery)+"%");
    }

    Bitmap mTran;
    private Bitmap getTranBitmap(){
        if(mTran == null) {
            mTran  = Bitmap.createBitmap(296, 296, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mTran);
            canvas.drawARGB(0,0,0,0);
        }
        return mTran;
    }

}
