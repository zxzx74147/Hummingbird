package com.xbirder.bike.hummingbird.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.HuApplication;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.bluetooth.XBirdBluetoothConfig;

public class GeneralOptionsActivity extends AppCompatActivity {
    private ToggleButton chargingShield;
    private ToggleButton offlineMode;
    private ToggleButton screenOftenBright;
//    private PowerManager pm;
//    private PowerManager.WakeLock wakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_options);
//        pm = (PowerManager) getSystemService(POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "xbird_Lock");


        chargingShield = (ToggleButton) findViewById(R.id.charging_shield);
        chargingShield.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
//                if (isChecked) {
//                    //
//                    setChargingShield(isChecked);
//                } else {
//                    //
//                }
                setChargingShield(isChecked);
            }
        });
        offlineMode = (ToggleButton) findViewById(R.id.offlineMode);

//        if(AccountManager.sharedInstance().getOffLineMode().equals("1")) {
//            Log.d("test","1");
//            offlineMode.setChecked(true);
//        }else{
//            Log.d("test","0");
//        }

        offlineMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
//                if (isChecked) {
//                    //
//                } else {
//                    //
//                }
                setOfflineMode(isChecked);
            }
        });

        screenOftenBright = (ToggleButton) findViewById(R.id.screenOftenBright);
        if(AccountManager.sharedInstance().getScreenBright().equals("yes")){
            screenOftenBright.setChecked(true);
        }
        screenOftenBright.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    //wakeLock.acquire();
                    getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    AccountManager.sharedInstance().setScreenBright("yes");
                } else {
                    //wakeLock.release();
                    getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    AccountManager.sharedInstance().setScreenBright("no");
                }
            }
        });
    }

    private void setChargingShield(boolean isOpen) {
        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.CHARGING_SHIELD, (byte) 0x00, XBirdBluetoothConfig.END};
        if (isOpen) {
            value[2] = (byte) 0x01;
        }
        HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
    }
    private void setOfflineMode(boolean isOpen) {
        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.OFF_LINE_MODE, (byte) 0x00, XBirdBluetoothConfig.END};
        if (isOpen) {
            value[2] = (byte) 0x01;
        }
        HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
    }

}
