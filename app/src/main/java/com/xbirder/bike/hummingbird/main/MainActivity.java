package com.xbirder.bike.hummingbird.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.main.widget.BatterySpeedView;

public class MainActivity extends BaseActivity {

    private TextView mSpeedText;
    private View mButtonE;
    private View mButtonN;
    private View mButtonS;
    private BatterySpeedView mBatterySpeedView;
    private TextView mBatteryView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpeedText = (TextView) findViewById(R.id.speed_num);
        mButtonE = findViewById(R.id.mode_e);
        mButtonN = findViewById(R.id.mode_n);
        mButtonS = findViewById(R.id.mode_s);
        mBatterySpeedView = (BatterySpeedView) findViewById(R.id.roll_view);
        mBatteryView = (TextView) findViewById(R.id.battery_num);
        mSpeedText.getPaint().setStrokeWidth(1);
        mSpeedText.setIncludeFontPadding(false);
        mButtonE.setOnClickListener(mOnClickListener);
        mButtonN.setOnClickListener(mOnClickListener);
        mButtonS.setOnClickListener(mOnClickListener);
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
        mBatterySpeedView.setPercent(battery);
        mBatteryView.setText(String.valueOf(battery));
    }

}
