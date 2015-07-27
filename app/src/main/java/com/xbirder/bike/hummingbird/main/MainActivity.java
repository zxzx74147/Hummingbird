package com.xbirder.bike.hummingbird.main;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.bluetooth.BluetoothLeService;
import com.xbirder.bike.hummingbird.bluetooth.SampleGattAttributes;
import com.xbirder.bike.hummingbird.bluetooth.XBirdBluetoothConfig;
import com.xbirder.bike.hummingbird.fonts.FontsManager;
import com.xbirder.bike.hummingbird.main.widget.BatteryRollView;
import com.xbirder.bike.hummingbird.setting.SettingActivity;
import com.xbirder.bike.hummingbird.skin.SkinConfig;
import com.xbirder.bike.hummingbird.skin.SkinManager;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity {
    private final static String TAG = "MainActivity";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private BluetoothGattService mCurrentService;
    private BluetoothGattCharacteristic mCurrentCharacteristic;

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
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
//                updateConnectionState(R.string.connected);
//                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
//                updateConnectionState(R.string.disconnected);
//                invalidateOptionsMenu();
//                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                byte[] bytes = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                read(bytes);
            }
        }
    };

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            if (uuid.contains(SampleGattAttributes.XBIRD_UUID)) {
                mCurrentService = gattService;
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();

                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    uuid = gattCharacteristic.getUuid().toString();
                    if (uuid.contains(SampleGattAttributes.XBIRD_CHARACTERISTIC)) {
                        mCurrentCharacteristic = gattCharacteristic;
                        setCharacteristicProperty();
                    }
                }
            }
        }
    }

    private void writeConnectInfo() {
        byte[] value = new byte[23];
        String username = AccountManager.sharedInstance().getUser().trim();
        String pass = AccountManager.sharedInstance().getPass().trim();
        String total = username.concat(pass);
        byte[] totalBytes = total.getBytes();
        value[0] = (byte) (XBirdBluetoothConfig.PREFIX & 0xFF);
        value[1] = (byte) (XBirdBluetoothConfig.CONNECT & 0xFF);
        value[22] = (byte) (XBirdBluetoothConfig.END & 0xFF);
        for (int i = 0; i < total.length(); i++) {
            value[i + 2] = (byte) (totalBytes[i] & 0xFF - 0x30);
        }

        sendToBluetooth(value);
    }

    private void writeLightInfo(boolean isOpen) {
        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.LIGHT, (byte)0x00, XBirdBluetoothConfig.END};
        if (isOpen) {
            value[2] = (byte)0x01;
        }
        sendToBluetooth(value);
    }

    private void writeSpeedInfo(int speedLevel) {
        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.SPEED, (byte)0x01, XBirdBluetoothConfig.END};
        if (speedLevel == 1) {
            value[2] = (byte)0x01;
        } else if (speedLevel == 2) {
            value[2] = (byte)0x02;
        } else if (speedLevel == 3) {
            value[2] = (byte)0x03;
        }
        sendToBluetooth(value);
    }

    private void writeLock(boolean isLock) {
        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.LIGHT, (byte)0x00, XBirdBluetoothConfig.END};
        if (isLock) {
            value[2] = (byte)0x01;
        }
        sendToBluetooth(value);
    }

    private void sendToBluetooth(byte[] bytes) {
//        mCurrentCharacteristic.setValue(XBirdBluetoothConfig.PREFIX,
//                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        if (mCurrentCharacteristic != null) {
            mCurrentCharacteristic.setValue(bytes);

            mBluetoothLeService.setCharacteristicNotification(mCurrentCharacteristic, true);
            mBluetoothLeService.writeCharacteristic(mCurrentCharacteristic);
        }
    }

    private void write(String str) {
//        byte[] value = new byte[20];
//        value[0] = (byte) 0x00;
//        if(str != null && str.length() > 0){
//            //write string
//            WriteBytes = str.getBytes();
//        }
////        else if(editTextNumEditText.getText().length() > 0){
////            WriteBytes= hex2byte(editTextNumEditText.getText().toString().getBytes());
////        }
//        mCurrentCharacteristic.setValue(value[0],
//                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
//        mCurrentCharacteristic.setValue(WriteBytes);
//
//        mBluetoothLeService.setCharacteristicNotification(mCurrentCharacteristic, true);
    }

    private void read(byte[] bytes) {
        if (bytes == null || bytes.length < 3) return;
        if (bytes[0] == XBirdBluetoothConfig.PREFIX && bytes[bytes.length-1] == XBirdBluetoothConfig.END) {
            switch (bytes[1]) {
                case XBirdBluetoothConfig.ERROR:
                    toast("出错啦亲");
                    break;
                case XBirdBluetoothConfig.INFO:
                    int battery = bytes[4];
                    int speed = bytes[5];
                    setBattery(battery);
                    setSpeed(speed);
                    break;
                default:
                    break;
            }
        }
    }

    private void setCharacteristicProperty() {
        if (mCurrentCharacteristic == null) return;
        final int charaProp = mCurrentCharacteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = mCurrentCharacteristic;
            mBluetoothLeService.setCharacteristicNotification(
                    mCurrentCharacteristic, true);
        }
        writeConnectInfo();
    }

    public static String bin2hex(String bin) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer sb = new StringBuffer("");
        byte[] bs = bin.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
        }
        return sb.toString();
    }
    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        b = null;
        return b2;
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
//        setBattery(80);
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
                    writeLightInfo(true);
                }else{
                    SkinManager.sharedInstance().setSkinMode(SkinConfig.SKIN_MODE_DAY);
                    writeLightInfo(false);
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
        writeLock(true);
    }

    private void unLock(){
        mLockView.setImageResource(R.drawable.lock_red);
        mLockView.setScaleType(ImageView.ScaleType.FIT_END);
        writeLock(false);
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
        int level = 1;
        switch (mode){
            case StatusConfig.MODE_E:
                attrs = new int[] { R.attr.btn_e_drawable};
                ta = this.obtainStyledAttributes(attrs);
                drawable = ta.getDrawable(0);
                ta.recycle();
                mButtonE.setImageDrawable(drawable);
                mTextE.setEnabled(true);
                level = 1;
                break;
            case StatusConfig.MODE_N:
                attrs = new int[] { R.attr.btn_n_drawable};
                ta = this.obtainStyledAttributes(attrs);
                drawable = ta.getDrawable(0);
                ta.recycle();
                mButtonN.setImageDrawable(drawable);
                mTextN.setEnabled(true);
                level = 2;
                break;
            case StatusConfig.MODE_S:
                attrs = new int[] { R.attr.btn_s_drawable};
                ta = this.obtainStyledAttributes(attrs);
                drawable = ta.getDrawable(0);
                ta.recycle();
                mButtonS.setImageDrawable(drawable);
                mTextS.setEnabled(true);
                level = 3;
                break;
        }
        writeSpeedInfo(level);
    }


    private void setSpeed(int speed){
        mSpeedText.setText(String.valueOf(speed));
    }

    private void setBattery(int battery){
        mBatteryRollView.setPercent(battery);
        mBatteryView.setText(String.valueOf(battery) + "%");
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

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

}
