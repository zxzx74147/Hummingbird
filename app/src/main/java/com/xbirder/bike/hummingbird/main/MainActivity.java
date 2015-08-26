package com.xbirder.bike.hummingbird.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.Cycling.CyclingRecords;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.bluetooth.BluetoothLeService;
import com.xbirder.bike.hummingbird.bluetooth.SampleGattAttributes;
import com.xbirder.bike.hummingbird.bluetooth.XBirdBluetoothConfig;
import com.xbirder.bike.hummingbird.bluetooth.XBirdBluetoothManager;
import com.xbirder.bike.hummingbird.fonts.FontsManager;
import com.xbirder.bike.hummingbird.main.side.WiperSwitch;
import com.xbirder.bike.hummingbird.main.widget.BatteryRollView;
import com.xbirder.bike.hummingbird.setting.MySetting;
import com.xbirder.bike.hummingbird.setting.SettingActivity;
import com.xbirder.bike.hummingbird.skin.SkinConfig;
import com.xbirder.bike.hummingbird.skin.SkinManager;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private Context mainContext = this;
    private final static String TAG = "MainActivity";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private String mDeviceName;
    private String mDeviceAddress;

    private boolean mConnected = false;

    private TextView mSpeedText;
    private FrameLayout mLeftDrawer;
    private RoundedImageView mRoundedImageView;
    private int screenWidth;
    private WiperSwitch wiperSwitch;
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
    private RelativeLayout mCyclingRecord;
    private ImageView mConnectBtn;
    private ImageView mLockView;
    private boolean isLock = true;
    private boolean mIsUseToken = true;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;
    private Handler mHandler;
    private LeDeviceListAdapter mLeDeviceListAdapter = null;
    private AlertDialog mScanDeviceDialog;

    public enum connectionStateEnum {isNull, isScanning, isToScan, isConnecting, isConnected, isDisconnecting}

    ;
    public connectionStateEnum mConnectionState = connectionStateEnum.isNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!initiate()) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mHandler = new Handler();

//        initScanAlert();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
    }

    private boolean initiate() {
        // Use this check to determine whether BLE is supported on the device.
        // Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) mainContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            return false;
        }
        return true;
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            XBirdBluetoothManager.sharedInstance().setBluetoothLeService(((BluetoothLeService.LocalBinder) service).getService());
            if (!XBirdBluetoothManager.sharedInstance().getBluetoothLeService().initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            XBirdBluetoothManager.sharedInstance().getBluetoothLeService().connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            XBirdBluetoothManager.sharedInstance().setBluetoothLeService(null);
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                AccountManager.sharedInstance().setConnectBluetooth(mDeviceName);
                onConectionStateChange(connectionStateEnum.isConnected);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                onConectionStateChange(connectionStateEnum.isDisconnecting);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(XBirdBluetoothManager.sharedInstance().getBluetoothLeService().getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
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
                XBirdBluetoothManager.sharedInstance().setCurrentService(gattService);
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();

                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    uuid = gattCharacteristic.getUuid().toString();
                    if (uuid.contains(SampleGattAttributes.XBIRD_CHARACTERISTIC)) {
                        XBirdBluetoothManager.sharedInstance().setCurrentCharacteristic(gattCharacteristic);
                        setCharacteristicProperty();
                    }
                }
            }
        }
    }

    private void writeConnectInfo(boolean isUseToken) {
        byte[] value = new byte[23];
        String username = AccountManager.sharedInstance().getUser().trim();
        String pass = AccountManager.sharedInstance().getPass().trim();
        String token = AccountManager.sharedInstance().getFinalToken().trim();
        String total;
        if (isUseToken) {
            total = username.concat(token);
        } else {
            total = username.concat(pass);
        }
        byte[] totalBytes = total.getBytes();
        value[0] = (byte) (XBirdBluetoothConfig.PREFIX & 0xFF);
        value[1] = (byte) (XBirdBluetoothConfig.CONNECT & 0xFF);
        value[22] = (byte) (XBirdBluetoothConfig.END & 0xFF);
        for (int i = 0; i < total.length(); i++) {
            value[i + 2] = (byte) (totalBytes[i] & 0xFF - 0x30);
        }

        XBirdBluetoothManager.sharedInstance().sendToBluetooth(value);
    }

    private void writeLightInfo(boolean isOpen) {
        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.LIGHT, (byte) 0x00, XBirdBluetoothConfig.END};
        if (isOpen) {
            value[2] = (byte) 0x01;
        }
        XBirdBluetoothManager.sharedInstance().sendToBluetooth(value);
    }

    private void writeSpeedInfo(int speedLevel) {
        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.SPEED, (byte) 0x01, XBirdBluetoothConfig.END};
        if (speedLevel == 1) {
            value[2] = (byte) 0x01;
        } else if (speedLevel == 2) {
            value[2] = (byte) 0x02;
        } else if (speedLevel == 3) {
            value[2] = (byte) 0x03;
        }
        XBirdBluetoothManager.sharedInstance().sendToBluetooth(value);
    }

    private void writeLock(boolean isLock) {
        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.LOCK, (byte) 0x00, XBirdBluetoothConfig.END};
        if (isLock) {
            value[2] = (byte) 0x01;
        }
        XBirdBluetoothManager.sharedInstance().sendToBluetooth(value);
    }


    private void read(byte[] bytes) {
        if (bytes == null || bytes.length < 3) return;
        if (bytes[0] == XBirdBluetoothConfig.PREFIX && bytes[bytes.length - 1] == XBirdBluetoothConfig.END) {
            switch (bytes[1]) {
                case XBirdBluetoothConfig.ERROR:
                    if (bytes[2] == XBirdBluetoothConfig.CONNECT_ERROR) {
                        if (mIsUseToken) {
                            writeConnectInfo(false);
                            mIsUseToken = false;
                        } else {
                            toast("连接锋鸟出错");
                            XBirdBluetoothManager.sharedInstance().getBluetoothLeService().disconnect();
                            mHandler.postDelayed(mDisonnectingOverTimeRunnable, 1000);

                            mConnectionState = connectionStateEnum.isDisconnecting;
                            onConectionStateChange(mConnectionState);
                        }
                    } else if (bytes[2] == XBirdBluetoothConfig.CONNECT_ERROR) {
                        toast("锁车失败");
                    }
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
        if (XBirdBluetoothManager.sharedInstance().getCurrentCharacteristic() == null) return;
        final int charaProp = XBirdBluetoothManager.sharedInstance().getCurrentCharacteristic().getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            XBirdBluetoothManager.sharedInstance().setNotifyCharacteristic(XBirdBluetoothManager.sharedInstance().getCurrentCharacteristic());
            XBirdBluetoothManager.sharedInstance().getBluetoothLeService().setCharacteristicNotification(
                    XBirdBluetoothManager.sharedInstance().getCurrentCharacteristic(), true);
        }
        writeConnectInfo(mIsUseToken);
    }

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_main);
/*        wiperSwitch = (WiperSwitch)findViewById(R.id.wiper_switch);
        wiperSwitch.setImageResource(R.drawable.lock_bg, R.drawable.lock_green);
        wiperSwitch.setOnSwitchStateListener(new WiperSwitch.OnSwitchListener() {
            @Override
            public void onSwitched(boolean isSwitchOn) {
                if (isSwitchOn) {
                    wiperSwitch.setImageResource(R.drawable.lock_bg, R.drawable.lock_red);
                } else {
                    wiperSwitch.setImageResource(R.drawable.lock_bg, R.drawable.lock_green);
                }
            }
        });*/
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;//屏幕的宽
        System.out.println("screenWidth : " + screenWidth);//screenWidth : 1080
        mRoundedImageView = (RoundedImageView) findViewById(R.id.head);
        mSpeedText = (TextView) findViewById(R.id.speed_num);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftDrawer = (FrameLayout) findViewById(R.id.left_drawer);
        int leftWidth = mDrawerLayout.getMeasuredWidth();
        int i = (int) (screenWidth / 1.2);
        ViewGroup.LayoutParams lp = mLeftDrawer.getLayoutParams();
        lp.width = i;
        mLeftDrawer.setLayoutParams(lp);
        mSettingView = findViewById(R.id.main_setting);
        mLockView = (ImageView) findViewById(R.id.lock_top);
        mLightView = findViewById(R.id.main_light);
        mSideSetting = findViewById(R.id.setting_layout);
        mCyclingRecord = (RelativeLayout) findViewById(R.id.low_cycling_records);
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
        mRoundedImageView.setOnClickListener(mOnClickListener);
        mCyclingRecord.setOnClickListener(mOnClickListener);
        FontsManager.sharedInstance().setSpeedType(mSpeedText);
        FontsManager.sharedInstance().setSpeedType(mBatteryView);
        FontsManager.sharedInstance().setSpeedKMType(mBatteryShow);
        setBattery(100);
        setMode(StatusConfig.MODE_E);
        mConnectBtn = (ImageView) findViewById(R.id.connect_bluetooth);
        mConnectBtn.setOnClickListener(mOnClickListener);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            mDrawerLayout.closeDrawer(mLeftDrawer);
        }
        return super.dispatchKeyEvent(event);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mButtonE) {
                setMode(StatusConfig.MODE_E);
            } else if (v == mButtonN) {
                setMode(StatusConfig.MODE_N);
            } else if (v == mButtonS) {
                setMode(StatusConfig.MODE_S);
            } else if (v == mCyclingRecord) {
                ActivityJumpHelper.startActivity(MainActivity.this, CyclingRecords.class);
            } else if (v == mSettingView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                if (mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
                    mDrawerLayout.closeDrawer(mLeftDrawer);
                } else {

                    //System.out.println("leftWidth : " + leftWidth);//System.out﹕ leftWidth : 950
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            } else if (v == mLockView) {
                if (isLock) {
                    unLock();
                } else {
                    lock();
                }
                isLock = !isLock;
            } else if (v == mLightView) {
                int mode = SkinManager.sharedInstance().getSkinMode();
                if (mode == SkinConfig.SKIN_MODE_DAY) {
                    SkinManager.sharedInstance().setSkinMode(SkinConfig.SKIN_MODE_NIGHT);
                    writeLightInfo(true);
                } else {
                    SkinManager.sharedInstance().setSkinMode(SkinConfig.SKIN_MODE_DAY);
                    writeLightInfo(false);
                }
                initView();
            } else if (v == mSideSetting) {
                ActivityJumpHelper.startActivity(MainActivity.this, SettingActivity.class);
            } else if (v == mConnectBtn) {
                onSearchClick();
            } else if (v == mRoundedImageView) {
                ActivityJumpHelper.startActivity(MainActivity.this, MySetting.class);
            }
        }
    };

    private void lock() {
        mLockView.setImageResource(R.drawable.lock_green);
        mLockView.setScaleType(ImageView.ScaleType.FIT_START);
        writeLock(true);
    }

    private void unLock() {
        mLockView.setImageResource(R.drawable.lock_red);
        mLockView.setScaleType(ImageView.ScaleType.FIT_END);
        writeLock(false);
    }

    private void setMode(int mode) {
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
        switch (mode) {
            case StatusConfig.MODE_E:
                attrs = new int[]{R.attr.btn_e_drawable};
                ta = this.obtainStyledAttributes(attrs);
                drawable = ta.getDrawable(0);
                ta.recycle();
                mButtonE.setImageDrawable(drawable);
                mTextE.setEnabled(true);
                level = 1;
                break;
            case StatusConfig.MODE_N:
                attrs = new int[]{R.attr.btn_n_drawable};
                ta = this.obtainStyledAttributes(attrs);
                drawable = ta.getDrawable(0);
                ta.recycle();
                mButtonN.setImageDrawable(drawable);
                mTextN.setEnabled(true);
                level = 2;
                break;
            case StatusConfig.MODE_S:
                attrs = new int[]{R.attr.btn_s_drawable};
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

    public void onConectionStateChange(connectionStateEnum theconnectionStateEnum) {
        mConnectBtn.setImageResource(R.drawable.search);
        AnimationDrawable animationDrawable;

        mConnectionState = theconnectionStateEnum;

        switch (theconnectionStateEnum) {
            case isNull:
                animationDrawable = (AnimationDrawable) mConnectBtn.getDrawable();
                animationDrawable.stop();
                mConnectBtn.setImageResource(R.drawable.search_unable);
                break;
            case isToScan:
                animationDrawable = (AnimationDrawable) mConnectBtn.getDrawable();
                animationDrawable.stop();
                mConnectBtn.setImageResource(R.drawable.search_unable);
                break;
            case isScanning:
//                mConnectBtn.setBackgroundResource(R.drawable.search);
                animationDrawable = (AnimationDrawable) mConnectBtn.getDrawable();
                animationDrawable.start();
                break;
            case isConnecting:
                animationDrawable = (AnimationDrawable) mConnectBtn.getDrawable();
                animationDrawable.stop();
                mConnectBtn.setImageResource(R.drawable.search_unable);
                break;
            case isConnected:
                animationDrawable = (AnimationDrawable) mConnectBtn.getDrawable();
                animationDrawable.stop();
                mConnectBtn.setImageResource(R.drawable.search_enable);
                break;
            case isDisconnecting:
                animationDrawable = (AnimationDrawable) mConnectBtn.getDrawable();
                animationDrawable.stop();
                mConnectBtn.setImageResource(R.drawable.search_unable);
                break;
            default:
                animationDrawable = (AnimationDrawable) mConnectBtn.getDrawable();
                animationDrawable.stop();
                mConnectBtn.setImageResource(R.drawable.search_unable);
                break;
        }
    }

    private void onSearchClick() {
        switch (mConnectionState) {
            case isNull:
                mConnectionState = connectionStateEnum.isScanning;
                onConectionStateChange(mConnectionState);
                mHandler.postDelayed(mSearchOverTimeRunnable, 3000);
                scanLeDevice(true);
                break;
            case isToScan:
                mConnectionState = connectionStateEnum.isScanning;
                onConectionStateChange(mConnectionState);
                mHandler.postDelayed(mSearchOverTimeRunnable, 3000);
                scanLeDevice(true);
                break;
            case isScanning:

                break;

            case isConnecting:

                break;
            case isConnected:
                confirmDisconnect();
                break;
            case isDisconnecting:

                break;

            default:
                break;
        }


    }

    private void confirmDisconnect() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("你确定要断开连接吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                XBirdBluetoothManager.sharedInstance().getBluetoothLeService().disconnect();
                mHandler.postDelayed(mDisonnectingOverTimeRunnable, 1000);

                mConnectionState = connectionStateEnum.isDisconnecting;
                onConectionStateChange(mConnectionState);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }


    private Runnable mConnectingOverTimeRunnable = new Runnable() {

        @Override
        public void run() {
            if (mConnectionState == connectionStateEnum.isConnecting)
                mConnectionState = connectionStateEnum.isToScan;
            onConectionStateChange(mConnectionState);
            XBirdBluetoothManager.sharedInstance().getBluetoothLeService().close();
        }
    };

    private Runnable mDisonnectingOverTimeRunnable = new Runnable() {

        @Override
        public void run() {
            if (mConnectionState == connectionStateEnum.isDisconnecting)
                mConnectionState = connectionStateEnum.isToScan;
            onConectionStateChange(mConnectionState);
            XBirdBluetoothManager.sharedInstance().getBluetoothLeService().close();
        }
    };

    private Runnable mSearchOverTimeRunnable = new Runnable() {

        @Override
        public void run() {
            if (mConnectionState == connectionStateEnum.isScanning)
                mConnectionState = connectionStateEnum.isToScan;
            if (mLeDeviceListAdapter.getCount() == 0) {
                toast("搜索不到锋鸟:(");
                scanLeDevice(false);
                onConectionStateChange(connectionStateEnum.isNull);
            } else if (mLeDeviceListAdapter.getCount() == 1) {
                String lastConnectBluetooth = AccountManager.sharedInstance().getConnectBluetooth();
                BluetoothDevice device = mLeDeviceListAdapter.getDevice(0);
                if (device == null)
                    return;
                mDeviceName = device.getName().toString();
                mDeviceAddress = device.getAddress().toString();

                scanLeDevice(false);
                System.out.println("onListItemClick " + device.getName().toString());

                if (!lastConnectBluetooth.equals(mDeviceName)) {
                    scanLeDevice(false);
                    System.out.println("onListItemClick " + device.getName().toString());

                    System.out.println("Device Name:" + device.getName() + "   " + "Device Name:" + device.getAddress());

                    if (XBirdBluetoothManager.sharedInstance().getBluetoothLeService().connect(mDeviceAddress)) {
                        Log.d(TAG, "Connect request success");
                        mConnectionState = connectionStateEnum.isConnecting;
                        onConectionStateChange(mConnectionState);
//                        mHandler.postDelayed(mConnectingOverTimeRunnable, 10000);
                    } else {
                        Log.d(TAG, "Connect request fail");
                        mConnectionState = connectionStateEnum.isToScan;
                        onConectionStateChange(mConnectionState);
                    }
                } else {
                    initScanAlert();
                }
            } else {
                initScanAlert();
            }
        }
    };

    private void initScanAlert() {
        // Initializes and show the scan Device Dialog
        mScanDeviceDialog = new AlertDialog.Builder(mainContext)
                .setTitle("搜索锋鸟").setAdapter(mLeDeviceListAdapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(which);
                        if (device == null)
                            return;
                        scanLeDevice(false);
                        System.out.println("onListItemClick " + device.getName().toString());

                        System.out.println("Device Name:" + device.getName() + "   " + "Device Name:" + device.getAddress());

                        mDeviceName = device.getName().toString();
                        mDeviceAddress = device.getAddress().toString();

                        if (mDeviceName.equals("No Device Available") && mDeviceAddress.equals("No Address Available")) {
                            mConnectionState = connectionStateEnum.isToScan;
                            onConectionStateChange(mConnectionState);
                        } else {
                            if (XBirdBluetoothManager.sharedInstance().getBluetoothLeService().connect(mDeviceAddress)) {
                                Log.d(TAG, "Connect request success");
                                mConnectionState = connectionStateEnum.isConnecting;
                                onConectionStateChange(mConnectionState);
                                mHandler.postDelayed(mConnectingOverTimeRunnable, 10000);
                            } else {
                                Log.d(TAG, "Connect request fail");
                                mConnectionState = connectionStateEnum.isToScan;
                                onConectionStateChange(mConnectionState);
                            }
                        }
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface arg0) {
                        System.out.println("mBluetoothAdapter.stopLeScan");

                        mConnectionState = connectionStateEnum.isToScan;
                        onConectionStateChange(mConnectionState);
                        mScanDeviceDialog.dismiss();

                        scanLeDevice(false);
                    }
                }).create();
        mScanDeviceDialog.show();
    }


    private void setSpeed(int speed) {
        mSpeedText.setText(String.valueOf(speed));
    }

    private void setBattery(int battery) {
        mBatteryRollView.setPercent(battery);
        mBatteryView.setText(String.valueOf(battery) + "%");
    }

    Bitmap mTran;

    private Bitmap getTranBitmap() {
        if (mTran == null) {
            mTran = Bitmap.createBitmap(296, 296, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mTran);
            canvas.drawARGB(0, 0, 0, 0);
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

        if (mConnectionState == connectionStateEnum.isNull ||
                mConnectionState == connectionStateEnum.isToScan) {
            onSearchClick();
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
        XBirdBluetoothManager.sharedInstance().setBluetoothLeService(null);
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            ((Activity) mainContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("mLeScanCallback onLeScan run ");
                    final String deviceName = device.getName();
                    if (deviceName != null && deviceName.length() > 0 && deviceName.contains("XBIRD")) {
                        mLeDeviceListAdapter.addDevice(device);
                        mLeDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }


    };

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = ((Activity) mainContext).getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view
                        .findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view
                        .findViewById(R.id.device_name);
                System.out.println("mInflator.inflate  getView");
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0 && deviceName.contains("XBIRD"))
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    void scanLeDevice(final boolean enable) {
        if (enable) {
            System.out.println("mBluetoothAdapter.startLeScan");

            if (mLeDeviceListAdapter != null) {
                mLeDeviceListAdapter.clear();
                mLeDeviceListAdapter.notifyDataSetChanged();
            }

            if (!mScanning) {
                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            if (mScanning) {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }
}
