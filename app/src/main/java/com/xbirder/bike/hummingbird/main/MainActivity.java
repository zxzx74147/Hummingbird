package com.xbirder.bike.hummingbird.main;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.fonts.FontsManager;
import com.xbirder.bike.hummingbird.main.widget.BatteryRollView;

public class MainActivity extends BaseActivity {

    private TextView mSpeedText;
//    private TextView mKMText;
    private View mButtonE;
    private View mButtonN;
    private View mButtonS;
    private BatteryRollView mBatteryRollView;
    private TextView mBatteryView;
    private TextView mBatteryShow;
    private DrawerLayout mDrawerLayout;
    private View mSettingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(){
        super.initView();
        setContentView(R.layout.activity_main_2);
        mSpeedText = (TextView) findViewById(R.id.speed_num);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSettingView = findViewById(R.id.main_setting);
//        mKMText = (TextView) findViewById(R.id.km_text);
        mBatteryShow = (TextView) findViewById(R.id.battery_show);
        mButtonE = findViewById(R.id.mode_e);
        mButtonN = findViewById(R.id.mode_n);
        mButtonS = findViewById(R.id.mode_s);
        mBatteryRollView = (BatteryRollView) findViewById(R.id.roll_view);
        mBatteryView = (TextView) findViewById(R.id.battery_num);
        mSpeedText.setIncludeFontPadding(false);
        mButtonE.setOnClickListener(mOnClickListener);
        mButtonN.setOnClickListener(mOnClickListener);
        mButtonS.setOnClickListener(mOnClickListener);
        mSettingView.setOnClickListener(mOnClickListener);
        FontsManager.sharedInstance().setSpeedType(mSpeedText);
        FontsManager.sharedInstance().setSpeedType(mBatteryView);
        FontsManager.sharedInstance().setSpeedKMType(mBatteryShow);
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
            }
        }
    };

    private void setMode(int mode){
        mButtonE.setVisibility(View.INVISIBLE);
        mButtonN.setVisibility(View.INVISIBLE);
        mButtonS.setVisibility(View.INVISIBLE);
        switch (mode){
            case StatusConfig.MODE_E:
                mButtonE.setVisibility(View.VISIBLE);
                break;
            case StatusConfig.MODE_N:
                mButtonN.setVisibility(View.VISIBLE);
                break;
            case StatusConfig.MODE_S:
                mButtonS.setVisibility(View.VISIBLE);
                break;
        }
        //TODO 发送蓝牙指令
    }

    private void setSpeed(int speed){
        mSpeedText.setText(String.valueOf(speed));
    }

    private void setBattery(int battery){
        mBatteryRollView.setPercent(battery);
        mBatteryView.setText(String.valueOf(battery));
    }

}
