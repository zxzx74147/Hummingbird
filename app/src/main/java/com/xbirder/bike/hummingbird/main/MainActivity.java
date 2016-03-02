package com.xbirder.bike.hummingbird.main;

import android.animation.ValueAnimator;
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
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.baidu.core.net.base.HttpResponse;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.xbirder.bike.hummingbird.AccountManager;
import com.xbirder.bike.hummingbird.HuApplication;
import com.xbirder.bike.hummingbird.R;
import com.xbirder.bike.hummingbird.base.BaseActivity;
import com.xbirder.bike.hummingbird.bluetooth.BluetoothLeService;
import com.xbirder.bike.hummingbird.bluetooth.SampleGattAttributes;
import com.xbirder.bike.hummingbird.bluetooth.XBirdBluetoothConfig;
import com.xbirder.bike.hummingbird.config.NetworkConfig;
import com.xbirder.bike.hummingbird.cycling.CyclingRecords;
import com.xbirder.bike.hummingbird.fonts.FontsManager;
import com.xbirder.bike.hummingbird.login.LoginActivity;
import com.xbirder.bike.hummingbird.login.LoginRequest;
import com.xbirder.bike.hummingbird.login.LoginTokenRequest;
import com.xbirder.bike.hummingbird.main.side.WiperSwitch;
import com.xbirder.bike.hummingbird.main.widget.BatteryRollView;
import com.xbirder.bike.hummingbird.main.widget.VelocityRollView;
import com.xbirder.bike.hummingbird.setting.MySetting;
import com.xbirder.bike.hummingbird.setting.SettingActivity;
import com.xbirder.bike.hummingbird.setting.XBirderSelfCheckActivity;
import com.xbirder.bike.hummingbird.skin.SkinConfig;
import com.xbirder.bike.hummingbird.skin.SkinManager;
import com.xbirder.bike.hummingbird.util.ActivityJumpHelper;
import com.xbirder.bike.hummingbird.util.CustomAlertDialog;
import com.xbirder.bike.hummingbird.util.StringHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private static final int REQUEST_ENABLE_BT = 1;

    private TextView mSpeedText;
    private FrameLayout mLeftDrawer;
    private FrameLayout mRightDrawer;
    private ViewGroup.LayoutParams fLp;
    private ViewGroup.LayoutParams RightfLp;
    private RoundedImageView mRoundedImageView;
    private int screenWidth;
    private int screenHeight;
    private int mLockBackWidth;
    private WiperSwitch wiperSwitch;
    //    private TextView mKMText;
    private ImageView mButtonE;
    private ImageView mButtonN;
    private ImageView mButtonS;
    private TextView mTextE;
    private TextView mTextN;
    private TextView mTextS;
    private BatteryRollView mBatteryRollView;
    private VelocityRollView mVelocityRollView;
    private TextView mBatteryView;
    private TextView mBatteryViewUint;
    //private TextView mBatteryShow;
    private DrawerLayout mDrawerLayout;
    private ImageView mSettingView;
    private RelativeLayout mLockBack;
    private View mLightView;
    private RelativeLayout mSideSetting;
    private RelativeLayout mXbirderHelper;
    private RelativeLayout mCyclingRecord;
    private ImageView mConnectBtn;
    private ImageView mLockView;
    private ImageView mLockEnd;
    private boolean isLock = true;
    private boolean mIsUseToken = true;
    private boolean mIsFirstConnect = false;
    private boolean mIsChangeMode = false;
    private boolean mIsReconnect = false;
    private boolean mNeedToast = true;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;
    private Handler mHandler;
    private LeDeviceListAdapter mLeDeviceListAdapter = null;
    private CustomAlertDialog mScanDeviceDialog;

    private int mTotalDistance = 0;
    private int mTotalTime = 0;
    private int mDisEdge = 0;
    private int mTimeEdge = 0;

    private TextView mWeatherText;
    private String mWeatherTextString = null;
    private TextView mUser_name;

    public enum connectionStateEnum {isNull, isScanning, isToScan, isConnecting, isConnected, isDisconnecting};
    public connectionStateEnum mConnectionState = connectionStateEnum.isNull;

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private BitmapDescriptor mCurrentMarker;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    boolean isFirstLoc = true; // 是否首次定位

    private List<LatLng> mBaiduMapPoints = new ArrayList<LatLng>();

    private LatLng lastll;

    private int BaiduMapDistance = 0;
    private boolean isBaiduMapDistanceEnable = false;

    private BDLocation mBDLocation = null;

    private TextView mdc_speed_num;
    private TextView mdc_mileage_num;
    private TextView mdc_power_num;
    private RelativeLayout mpenContent;

    private ImageView mImageView_navigation_back;
    private ImageView mImageView_navigation_baidumap;
    private ImageView mImageView_navigation_backtolocation;

    private ImageView mdc_show;
    private boolean isopenmenu = true;
    private int mpenContentHeight = 0;

    private boolean isFirstIn = true; // 是否首次进入

    private String avatarName;

    private LinearLayout flexible_menu;

    private int mlishiTempDistance = 0;
    private int mTotalTempDistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

        sendCurrentRidingDate();

        if (!initiate()) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported,
                    Toast.LENGTH_SHORT).show();
            return;
        }

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
            HuApplication.sharedInstance().XBirdBluetoothManager().setBluetoothLeService(((BluetoothLeService.LocalBinder) service).getService());
            if (!HuApplication.sharedInstance().XBirdBluetoothManager().getBluetoothLeService().initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            HuApplication.sharedInstance().XBirdBluetoothManager().getBluetoothLeService().connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            HuApplication.sharedInstance().XBirdBluetoothManager().setBluetoothLeService(null);
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                mIsReconnect = false;
                AccountManager.sharedInstance().setConnectBluetooth(mDeviceName);
                onConectionStateChange(connectionStateEnum.isConnected);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                mIsFirstConnect = false;
                HuApplication.sharedInstance().XBirdBluetoothManager().setIsConnect(false);
                onConectionStateChange(connectionStateEnum.isDisconnecting);
                if (mIsReconnect == false) {
                    mHandler.postDelayed(mDisonnectingOverTimeRunnable, 1000);
                }
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(HuApplication.sharedInstance().XBirdBluetoothManager().getBluetoothLeService().getSupportedGattServices());
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
                HuApplication.sharedInstance().XBirdBluetoothManager().setCurrentService(gattService);
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();

                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    uuid = gattCharacteristic.getUuid().toString();
                    if (uuid.contains(SampleGattAttributes.XBIRD_CHARACTERISTIC)) {
                        HuApplication.sharedInstance().XBirdBluetoothManager().setCurrentCharacteristic(gattCharacteristic);
                        setCharacteristicProperty();
                    }
                }
            }
        }
    }

    private void writeConnectInfo(boolean isUseToken) {
        byte[] value = new byte[20];
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
        value[19] = (byte) (XBirdBluetoothConfig.END & 0xFF);
        for (int i = 0; i < total.length(); i++) {
            value[i + 2] = (byte) (totalBytes[i] & 0xFF - 0x30);
        }

        HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
    }

    private void writeLightInfo(boolean isOpen) {
        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.LIGHT, (byte) 0x00, XBirdBluetoothConfig.END};
        if (isOpen) {
            value[2] = (byte) 0x01;
        }
        HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
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
        HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
    }

    private void writeLock(boolean isLock) {
        byte[] value = {XBirdBluetoothConfig.PREFIX, XBirdBluetoothConfig.LOCK, (byte) 0x00, XBirdBluetoothConfig.END};
        if (isLock) {
            value[2] = (byte) 0x01;
        }
        HuApplication.sharedInstance().XBirdBluetoothManager().sendToBluetooth(value);
    }

    private Runnable mChangingModeRunnable = new Runnable() {

        @Override
        public void run() {
            mIsChangeMode = false;
        }
    };

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
                            HuApplication.sharedInstance().XBirdBluetoothManager().getBluetoothLeService().disconnect();
//                            mIsReconnect = true;
                            mHandler.postDelayed(mDisonnectingOverTimeRunnable, 1000);

                            mConnectionState = connectionStateEnum.isDisconnecting;
                            onConectionStateChange(mConnectionState);
                        }
                    } else if (bytes[2] == XBirdBluetoothConfig.CONNECT_ERROR) {
                        toast("锁车失败");
                    }
                    break;
                case XBirdBluetoothConfig.INFO:
                    int mode = bytes[2];
                    HuApplication.sharedInstance().XBirdBluetoothManager().setIsConnect(true);
                    if (mIsFirstConnect == false) {
                        mIsFirstConnect = true;
                        if (mode != StatusConfig.CURRENT_MODE) {
                            setMode(StatusConfig.CURRENT_MODE, true);
                        }
                    }
                    if (mode != StatusConfig.CURRENT_MODE && !mIsChangeMode) {
                        setMode(StatusConfig.CURRENT_MODE, false);
                    }
                    int battery = bytes[4];
                    int speed = bytes[5];
                    setBattery(battery);
                    setSpeed(speed);

                    int tempDistance = 0;
                    for (int i = 6; i < 10; i++) {
                        tempDistance = 256 * tempDistance + bytes[i];
                    }

                    int tempTime = 0;
                    for (int i = 14; i < 18; i++) {
                        tempTime = 256 * tempTime + bytes[i];
                    }

                    //int OffLineModeEnable = bytes[11];
//                    Log.d("test2", "0"+bytes[11]);
//
//                    if(bytes[11] == (byte)0x01 ){
//                        Log.d("test3", "1");
//                        if(!AccountManager.sharedInstance().getOffLineMode().equals("1")) {
//                            AccountManager.sharedInstance().setOffLineMode("1");
//                            Log.d("test1", "1");
//                        }
//                    }else if(bytes[11] == (byte)0x00){
//                        Log.d("test3", "2");
//                        if(!AccountManager.sharedInstance().getOffLineMode().equals("0")) {
//                            AccountManager.sharedInstance().setOffLineMode("0");
//                            Log.d("test1", "0");
//                        }
//                    }

                    int bikeCurrentVersion = bytes[18];
                    if(!AccountManager.sharedInstance().getBikeCurrentVersion().equals(""+bikeCurrentVersion)) {
                        AccountManager.sharedInstance().setBikeCurrentVersion("" + bikeCurrentVersion);
                    }

                    mTotalTempDistance = tempDistance;
                    if(isBaiduMapDistanceEnable){
                        int mBaiduMapDistance = BaiduMapDistance/100;
                        tempDistance = mTotalDistance + mBaiduMapDistance;
                        BaiduMapDistance = BaiduMapDistance - mBaiduMapDistance*100;
                    }else{
                        //tempDistance = mTotalDistance + tempDistance - mlishiTempDistance;
                        tempDistance = tempDistance - mlishiTempDistance;
                    }

                    setStoreRidingData(tempDistance, tempTime);
                    break;
                default:
                    break;
            }
        }
    }

    private void setStoreRidingData(int dis, int time) {
        if (dis == mTotalDistance) {
            return;
        }

        String storeDate = AccountManager.sharedInstance().getStoreDate();
        final String currentDate = getTodayString();
        String storeDis = AccountManager.sharedInstance().getStoreDistance();
        String storeTime = AccountManager.sharedInstance().getStoreRuntime();

        //if (storeDate == null || storeDate == "") {
        if (storeDate == null || storeDate.length() <= 0) {
            AccountManager.sharedInstance().setStoreDate(currentDate);
            AccountManager.sharedInstance().setStoreDistance("0");
            AccountManager.sharedInstance().setStoreRuntime("0");
            storeDate = currentDate;
        }

        if (!currentDate.equals(storeDate)) {
            //send to server
            RidingRequest request = new RidingRequest(new HttpResponse.Listener<JSONObject>() {
                @Override
                public void onResponse(HttpResponse<JSONObject> response) {
                    if (response.isSuccess()) {
                        try {
                            if (response.result.getString("error").equals("0")) {
                                AccountManager.sharedInstance().setStoreDate(currentDate);
                                AccountManager.sharedInstance().setStoreDistance("0");
                                AccountManager.sharedInstance().setStoreRuntime("0");
                            } else {
                                toast("失败");
                            }
                        } catch (Exception e) {

                        }
                    }
                }
            });
            String token = AccountManager.sharedInstance().getToken();
            request.setParam(storeDate, storeDis, storeTime, token);
            sendRequest(request);
        }

        int localDisInt = Integer.parseInt(storeDis);
        int localTimeInt = Integer.parseInt(storeTime);

        int tempDisEdge = dis - localDisInt - mDisEdge;
        if (Math.abs(tempDisEdge) > 1) {
            mDisEdge = dis - localDisInt;
            mTimeEdge = time - localTimeInt;
        }
        mTotalDistance = dis - mDisEdge;
        if (mTotalDistance < 0) {
            mTotalDistance = 0;
        }
        mTotalTime = time - mTimeEdge;
        if (mTotalTime < 0) {
            mTotalTime = 0;
        }

        AccountManager.sharedInstance().setStoreDistance(String.valueOf(mTotalDistance));
//        if(isBaiduMapDistanceEnable){
//            AccountManager.sharedInstance().setStoreDistance(String.valueOf(BaiduMapDistance));
//        }else{
////            //这里的1是100米
////            if(mTotalDistance > 5){
////                isBaiduMapDistanceEnable = true;
////                BaiduMapDistance = mTotalDistance;
////            }
//            AccountManager.sharedInstance().setStoreDistance(String.valueOf(mTotalDistance));
//        }
        AccountManager.sharedInstance().setStoreRuntime(String.valueOf(mTotalTime));
    }

    private void sendCurrentRidingDate () {
        String storeDate = AccountManager.sharedInstance().getStoreDate();
        String storeDis = AccountManager.sharedInstance().getStoreDistance();
        String storeTime = AccountManager.sharedInstance().getStoreRuntime();

        int storeDisInt = Integer.parseInt(storeDis) * 100;
        storeDis = String.valueOf(storeDisInt);

        //if (storeDate == null || storeDate == "") {
        if (storeDate == null || storeDate.length() <= 0) {
                return;
        }

        if (storeDisInt >= 1) {
            //send to server
            RidingRequest request = new RidingRequest(new HttpResponse.Listener<JSONObject>() {
                @Override
                public void onResponse(HttpResponse<JSONObject> response) {
                    if (response.isSuccess()) {
                        try {
                            if (response.result.getString("error").equals("0")) {
                                AccountManager.sharedInstance().setStoreDate("");
                                AccountManager.sharedInstance().setStoreDistance("0");
                                AccountManager.sharedInstance().setStoreRuntime("0");
                            } else {
                                toast("失败");
                            }
                        } catch (Exception e) {

                        }
                    }
                }
            });
            String token = AccountManager.sharedInstance().getToken();
            request.setParam(storeDate, storeDis, storeTime, token);
            sendRequest(request);
        }
    }

    private String getTodayString () {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        String todayStr = sdf.format(cal.getTime());
        return todayStr;
    }

    private void setCharacteristicProperty() {
        if (HuApplication.sharedInstance().XBirdBluetoothManager().getCurrentCharacteristic() == null)
            return;
        final int charaProp = HuApplication.sharedInstance().XBirdBluetoothManager().getCurrentCharacteristic().getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            HuApplication.sharedInstance().XBirdBluetoothManager().setNotifyCharacteristic(HuApplication.sharedInstance().XBirdBluetoothManager().getCurrentCharacteristic());
            HuApplication.sharedInstance().XBirdBluetoothManager().getBluetoothLeService().setCharacteristicNotification(
                    HuApplication.sharedInstance().XBirdBluetoothManager().getCurrentCharacteristic(), true);
        }
        writeConnectInfo(mIsUseToken);
    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    @Override
    protected void initView() {
        super.initView();
        if(isFirstIn) {
            isFirstIn = false;

            setContentView(R.layout.activity_main);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenWidth = dm.widthPixels;//屏幕的宽
            screenHeight = dm.heightPixels;//屏幕的高
            //System.out.println("screenWidth : " + screenWidth + "screenHeight : " + screenHeight);//screenWidth : 720
            mRoundedImageView = (RoundedImageView) findViewById(R.id.head);

            avatarName = AccountManager.sharedInstance().getAvatarName();
            if (avatarName != null && avatarName.length() > 0) {
                avatarName = avatarName.substring(avatarName.lastIndexOf("/") + 1, avatarName.length());
                String picPath = Environment.getExternalStorageDirectory() + "/xbird/pic";
                File picfile = new File(picPath, avatarName);
                if (picfile.exists()) {
                    Bitmap bitmap = decodeUriAsBitmap(Uri.fromFile(picfile));
                    Bitmap roundBitMap = MySetting.getRoundedCornerBitmap(bitmap, 1.0f);
                    mRoundedImageView.setImageBitmap(roundBitMap);
                }
            }
            mSpeedText = (TextView) findViewById(R.id.speed_num);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mLeftDrawer = (FrameLayout) findViewById(R.id.left_drawer);
            int i = (int) (screenWidth / 1.2);
            fLp = mLeftDrawer.getLayoutParams();
            fLp.width = i;
            mLeftDrawer.setLayoutParams(fLp);
            mSettingView = (ImageView) findViewById(R.id.main_setting);
            mLockBack = (RelativeLayout) findViewById(R.id.rl_lock);
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            mLockBack.measure(w, h);
            mLockBackWidth = mLockBack.getMeasuredWidth();
            RelativeLayout.LayoutParams mLockBackPam = (RelativeLayout.LayoutParams) mLockBack.getLayoutParams();
            int y = (int) (i / 1.2);
            mLockBackPam.width = y;
            mLockBackPam.setMargins((i - mLockBackWidth) / 2, (int) (screenHeight / 7.2), (i - mLockBackWidth) / 2, 0);
//        System.out.println("i : " + i);//600
//        System.out.println("y : " + y);//500
            mLockBack.setLayoutParams(mLockBackPam);
            mLockView = (ImageView) findViewById(R.id.lock_top);
            mLockEnd = (ImageView) findViewById(R.id.lock_end);
            mLightView = findViewById(R.id.main_light);
            mSideSetting = (RelativeLayout) findViewById(R.id.setting_layout);
            mXbirderHelper = (RelativeLayout) findViewById(R.id.xbirdr_helper);
            mCyclingRecord = (RelativeLayout) findViewById(R.id.low_cycling_records);

            mUser_name = (TextView) findViewById(R.id.user_name);
            String muserName = AccountManager.sharedInstance().getUsername();
            if (muserName != null && muserName.length() > 0) {
                mUser_name.setText(muserName);
            }


            mpenContent = (RelativeLayout) findViewById(R.id.pencontent);
            mdc_speed_num = (TextView) findViewById(R.id.dc_speed_num);
            mdc_mileage_num = (TextView) findViewById(R.id.dc_mileage_num);
            mdc_power_num = (TextView) findViewById(R.id.dc_power_num);
            mImageView_navigation_back = (ImageView) findViewById(R.id.imageView_navigation_back);
            mImageView_navigation_back.setOnClickListener(mOnClickListener);

            mImageView_navigation_baidumap = (ImageView) findViewById(R.id.imageView_navigation_baidumap);
            mImageView_navigation_baidumap.setOnClickListener(mOnClickListener);

            mImageView_navigation_backtolocation = (ImageView) findViewById(R.id.imageView_navigation_backtolocation);
            mImageView_navigation_backtolocation.setOnClickListener(mOnClickListener);

            mdc_show = (ImageView) findViewById(R.id.dc_show);
            mdc_show.setOnClickListener(mOnClickListener);

//         avatarName = AccountManager.sharedInstance().getAvatarName();
//        if(avatarName != null && avatarName.length()>0){
//            avatarName = avatarName.substring(avatarName.lastIndexOf("/")+1,avatarName.length());
//            String picPath = Environment.getExternalStorageDirectory()+"/xbird/pic";
//            File picfile = new File(picPath,avatarName);
//            if (picfile.exists()) {
//                Bitmap bitmap = decodeUriAsBitmap(Uri.fromFile(picfile));
//                Bitmap roundBitMap = MySetting.getRoundedCornerBitmap(bitmap, 1.0f);
//                mRoundedImageView.setImageBitmap(roundBitMap);
//            }
//        }
//
//        mSpeedText = (TextView) findViewById(R.id.speed_num);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mLeftDrawer = (FrameLayout) findViewById(R.id.left_drawer);
//        int i = (int) (screenWidth / 1.2);
//        fLp = mLeftDrawer.getLayoutParams();
//        fLp.width = i;
//        mLeftDrawer.setLayoutParams(fLp);
//        mSettingView = findViewById(R.id.main_setting);
//        mLockBack = (RelativeLayout) findViewById(R.id.rl_lock);
//        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        mLockBack.measure(w, h);
//        mLockBackWidth = mLockBack.getMeasuredWidth();
//        RelativeLayout.LayoutParams mLockBackPam = (RelativeLayout.LayoutParams) mLockBack.getLayoutParams();
//        int y = (int) (i / 1.2);
//        mLockBackPam.width = y;
//        mLockBackPam.setMargins((i - mLockBackWidth) / 2, (int) (screenHeight / 7.2), (i - mLockBackWidth) / 2, 0);
////        System.out.println("i : " + i);//600
////        System.out.println("y : " + y);//500
//        mLockBack.setLayoutParams(mLockBackPam);
//        mLockView = (ImageView) findViewById(R.id.lock_top);
//        mLockEnd = (ImageView) findViewById(R.id.lock_end);
//        mLightView = findViewById(R.id.main_light);
//        mSideSetting = (RelativeLayout) findViewById(R.id.setting_layout);
//        mXbirderHelper = (RelativeLayout) findViewById(R.id.xbirdr_helper);
//        mCyclingRecord = (RelativeLayout) findViewById(R.id.low_cycling_records);
//        mKMText = (TextView) findViewById(R.id.km_text);
            //mBatteryShow = (TextView) findViewById(R.id.battery_show);
            mButtonE = (ImageView) findViewById(R.id.mode_e);
            mButtonN = (ImageView) findViewById(R.id.mode_n);
            mButtonS = (ImageView) findViewById(R.id.mode_s);
            mTextE = (TextView) findViewById(R.id.mode_e_text);
            mTextN = (TextView) findViewById(R.id.mode_n_text);
            mTextS = (TextView) findViewById(R.id.mode_s_text);
            mBatteryRollView = (BatteryRollView) findViewById(R.id.roll_view);
            mVelocityRollView = (VelocityRollView) findViewById(R.id.velocity_view);
            mBatteryView = (TextView) findViewById(R.id.battery_num);
            mBatteryViewUint = (TextView) findViewById(R.id.battery_num_unit);

            mSpeedText.setIncludeFontPadding(false);

            //mWeatherText = (TextView) findViewById(R.id.weather_text);
//        mUser_name = (TextView) findViewById(R.id.user_name);
//        String muserName = AccountManager.sharedInstance().getUsername();
//        if(muserName != null && muserName.length() > 0){
//            mUser_name.setText(muserName);
//        }

            mLockBack.setOnClickListener(mOnClickListener);
            mButtonE.setOnClickListener(mOnClickListener);
            mButtonN.setOnClickListener(mOnClickListener);
            mButtonS.setOnClickListener(mOnClickListener);
            mLockView.setOnClickListener(mOnClickListener);
            mSettingView.setOnClickListener(mOnClickListener);
            mLightView.setOnClickListener(mOnClickListener);
            mSideSetting.setOnClickListener(mOnClickListener);
            mRoundedImageView.setOnClickListener(mOnClickListener);
            mCyclingRecord.setOnClickListener(mOnClickListener);
            mXbirderHelper.setOnClickListener(mOnClickListener);

            FontsManager.sharedInstance().setSpeedType(mSpeedText);
            FontsManager.sharedInstance().setBatteryType(mBatteryView);

//        mpenContent = (RelativeLayout) findViewById( R.id.pencontent );
//        mdc_speed_num = (TextView) findViewById( R.id.dc_speed_num );
//        mdc_mileage_num = (TextView) findViewById( R.id.dc_mileage_num );
//        mdc_power_num = (TextView) findViewById( R.id.dc_power_num );
//        mImageView_navigation_back = (ImageView)findViewById( R.id.imageView_navigation_back);
//        mImageView_navigation_back.setOnClickListener(mOnClickListener);
//
//        mImageView_navigation_baidumap = (ImageView)findViewById( R.id.imageView_navigation_baidumap);
//        mImageView_navigation_baidumap.setOnClickListener(mOnClickListener);
//
//        mImageView_navigation_backtolocation = (ImageView)findViewById( R.id.imageView_navigation_backtolocation);
//        mImageView_navigation_backtolocation.setOnClickListener(mOnClickListener);
//
//        mdc_show = (ImageView)findViewById(R.id.dc_show);
//        mdc_show.setOnClickListener(mOnClickListener);

            String storeModeStr = AccountManager.sharedInstance().getLastSpeedLevel();
            if (storeModeStr != "") {
                StatusConfig.CURRENT_MODE = Integer.parseInt(storeModeStr);
            }
            setMode(StatusConfig.CURRENT_MODE, false);
            mConnectBtn = (ImageView) findViewById(R.id.connect_bluetooth);
            mConnectBtn.setOnClickListener(mOnClickListener);

            if (mConnectionState == connectionStateEnum.isConnected) {
                onConectionStateChange(connectionStateEnum.isConnected);
            }

            mRightDrawer = (FrameLayout) findViewById(R.id.right_drawer);
            int j = (int) (screenWidth);
            RightfLp = mRightDrawer.getLayoutParams();
            RightfLp.width = j;
            mRightDrawer.setLayoutParams(RightfLp);

            flexible_menu = (LinearLayout) findViewById(R.id.flexible_menu);
            mWeatherText = (TextView) findViewById(R.id.weather_text);
            if (mWeatherTextString != null) {
                mWeatherText.setText(mWeatherTextString);
            }

            // 地图初始化
            mMapView = (MapView) findViewById(R.id.bmapView);
            mBaiduMap = mMapView.getMap();
            //普通地图
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(15).build()));

            mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

            mBaiduMap
                    .setMyLocationConfigeration(new MyLocationConfiguration(
                            mCurrentMode, true, null));
            mMapView.onPause();
            // 定位初始化
            mLocClient = new LocationClient(this);
            mLocClient.registerLocationListener(myListener);
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true); // 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(1000);
            option.setIsNeedAddress(true);
            option.setNeedDeviceDirect(true);
            mLocClient.setLocOption(option);
            mLocClient.start();

//                if(isFirstIn) {
//                    isFirstIn = false;
            ValueAnimator animator = createSpeedStartAnimator(0, 70);
            animator.setDuration(2000);
            animator.start();
            checkNameAndAvatar();
            // }
//        if(isFirstIn) {

            // isFirstIn = false;
//            setBattery(100);
//            setSpeed(0);
//            ValueAnimator animator = createSpeedStartAnimator(0, 70);
//            animator.setDuration(2000);
//            animator.start();

//            // 地图初始化
//            mMapView = (MapView) findViewById(R.id.bmapView);
//            mBaiduMap = mMapView.getMap();
//            //普通地图
//            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//            // 开启定位图层
//            mBaiduMap.setMyLocationEnabled(true);
//            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(15).build()));
//
//            mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
//
//            mBaiduMap
//                    .setMyLocationConfigeration(new MyLocationConfiguration(
//                            mCurrentMode, true, null));
//            mMapView.onPause();
//            // 定位初始化
//            mLocClient = new LocationClient(this);
//            mLocClient.registerLocationListener(myListener);
//            LocationClientOption option = new LocationClientOption();
//            option.setOpenGps(true); // 打开gps
//            option.setCoorType("bd09ll"); // 设置坐标类型
//            option.setScanSpan(1000);
//            option.setIsNeedAddress(true);
//            option.setNeedDeviceDirect(true);
//            mLocClient.setLocOption(option);
            //          mLocClient.start();

            //         checkNameAndAvatar();

            //}

            ActionBarDrawerToggle drawerbar = new ActionBarDrawerToggle(this, mDrawerLayout, null, R.string.ok, R.string.back) {

                //菜单打开

                @Override

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    if (drawerView == mLeftDrawer) {

                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mRightDrawer);
                    } else if (drawerView == mRightDrawer) {
                        mMapView.onResume();
                        mLocClient.start();
                        isBaiduMapDistanceEnable = true;

                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mLeftDrawer);
                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, mRightDrawer);
                    }


//                if (mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
//                    Log.d("isLeftDrawerOpen", "yes");
//
//
//                }else{
//                    Log.d("isLeftDrawerOpen", "no");
//                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
////                    TranslateAnimation mv = new TranslateAnimation(0,0,0,-200);
////                    mv.setDuration(3000);
////                    mpenContent.startAnimation(mv);
//
//                }
                }

                // 菜单关闭
                @Override
                public void onDrawerClosed(View drawerView) {
                    if (drawerView == mRightDrawer) {
                        mMapView.onPause();
                        mLocClient.stop();
                        isBaiduMapDistanceEnable = false;
                        mlishiTempDistance = mTotalTempDistance;

                    }
                    super.onDrawerClosed(drawerView);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }

                @Override
                public void onDrawerStateChanged(int newState) {
                    super.onDrawerStateChanged(newState);

                }
            };
            mDrawerLayout.setDrawerListener(drawerbar);
        }
    }

    private void changeAppThemeMode(boolean isDay){
        RelativeLayout  drawer_main = (RelativeLayout) findViewById(R.id.drawer_main);
        ImageView  model_bg = (ImageView) findViewById(R.id.model_bg);
        RelativeLayout  title = (RelativeLayout) findViewById(R.id.title);
        ImageView  weather_ic_img = (ImageView) findViewById(R.id.weather_ic_img);
        ImageView  weather_line = (ImageView) findViewById(R.id.weather_line);
        Resources resource = (Resources) getBaseContext().getResources();

        if (isDay) {
            drawer_main.setBackgroundResource(R.color.common_background_day);
            mSettingView.setImageResource(R.drawable.icon_setting);
            ColorStateList csl1 = (ColorStateList) resource.getColorStateList(R.color.common_orange_day);
            if (csl1 != null) {
                mSpeedText.setTextColor(csl1);
            }
            mButtonE.setImageResource(R.drawable.icon_e_drawable);
            mButtonN.setImageResource(R.drawable.icon_n_drawable);
            mButtonS.setImageResource(R.drawable.icon_s_drawable);
            model_bg.setImageResource(R.drawable.model);
            title.setBackgroundResource(R.drawable.icon_head_bg);
            weather_ic_img.setImageResource(R.drawable.icon_lc);
            weather_line.setImageResource(R.drawable.line);
        } else {
            drawer_main.setBackgroundResource(R.color.common_background_night);
            mSettingView.setImageResource(R.drawable.icon_setting_night);
            ColorStateList csl1 = (ColorStateList) resource.getColorStateList(R.color.white);
            if (csl1 != null) {
                mSpeedText.setTextColor(csl1);
            }
            mButtonE.setImageResource(R.drawable.icon_e_drawable_night);
            mButtonN.setImageResource(R.drawable.icon_n_drawable_night);
            mButtonS.setImageResource(R.drawable.icon_s_drawable_night);
            model_bg.setImageResource(R.drawable.model_night);
            title.setBackgroundResource(R.color.common_background_night);
            weather_ic_img.setImageResource(R.drawable.icon_lc_night);
            weather_line.setImageResource(R.drawable.line_night);
        }
        setMode(StatusConfig.CURRENT_MODE, false);
    }

    private void baiduMapStart(){
        mLocClient.start();
    }
//    private void baiduMapStop(){
//        mLocClient.stop();
//    }
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mBDLocation = location;
            //Log.d("getCity",location.getCity());
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);


           if(mCurrentMode == MyLocationConfiguration.LocationMode.FOLLOWING){
               mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
               mBaiduMap
                       .setMyLocationConfigeration(new MyLocationConfiguration(
                               mCurrentMode, true, mCurrentMarker));
           }
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);

//                OverlayOptions ooDot2 = new DotOptions().center(ll).radius(13)
//                        .color(0xFFffffff);
////                        .color(0xFF0000FF);0xFFff973a
//                mBaiduMap.addOverlay(ooDot2);
//
//                OverlayOptions ooDot = new DotOptions().center(ll).radius(10)
//                          .color(0xFFff973a);
////                        .color(0xFF0000FF);
//                mBaiduMap.addOverlay(ooDot);
//
//                lastll = ll;

                String cityName = location.getCity();
                if(cityName == null){
                    //mWeatherText.setText("获取城市位置失败！");
                    isFirstLoc = true;
                }else{
                    OverlayOptions ooDot2 = new DotOptions().center(ll).radius(13)
                            .color(0xFFffffff);
//                        .color(0xFF0000FF);0xFFff973a
                    mBaiduMap.addOverlay(ooDot2);

                    OverlayOptions ooDot = new DotOptions().center(ll).radius(10)
                            .color(0xFFff973a);
//                        .color(0xFF0000FF);
                    mBaiduMap.addOverlay(ooDot);

                    lastll = ll;

                    getWeather(cityName.substring(0, cityName.length() - 1));
                    if(!mDrawerLayout.isDrawerOpen(mRightDrawer)){
                        mLocClient.stop();
                    }
                }
            }else{
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());

//                if(mWeatherText.equals(R.string.default_weather)){
//                    String cityName = location.getCity();
//                    if(cityName != null){
//                        getWeather(cityName.substring(0, cityName.length() - 1));
//                    }
//                }

                int pdistance = (int) DistanceUtil.getDistance(ll, lastll);
                if(pdistance < 10) {
                    return;
                }else if(pdistance > 500){
                    lastll = ll;
                    return;
                }else{
                    BaiduMapDistance +=  pdistance;
                }
                List<LatLng> pts = new ArrayList<LatLng>();
                pts.add(lastll);
                pts.add(ll);

                OverlayOptions ooPolyline = new PolylineOptions().width(5)
                        .color(0xFFff973a).points(pts);
                mBaiduMap.addOverlay(ooPolyline);
                lastll = ll;
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
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
                setMode(StatusConfig.MODE_E, true);
            } else if (v == mButtonN) {
                setMode(StatusConfig.MODE_N, true);
            } else if (v == mButtonS) {
                setMode(StatusConfig.MODE_S, true);
            } else if (v == mdc_show) {
                //if(mpenContent.getHeight())
                if(mpenContentHeight == 0){
                    mpenContentHeight = flexible_menu.getHeight();
                }

                if(isopenmenu) {
                    ValueAnimator animator = createDropAnimator(mpenContent,0,-mpenContentHeight);
                    animator.start();
                    isopenmenu = false;
                }else{
                    ValueAnimator animator = createDropAnimator(mpenContent,-mpenContentHeight,0);
                    animator.start();
                    isopenmenu = true;
                }
            } else if (v == mImageView_navigation_back){
                if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
                    mDrawerLayout.closeDrawer(mRightDrawer);
                }
            } else if (v == mImageView_navigation_baidumap) {
                startNavi();
            } else if (v == mImageView_navigation_backtolocation){
                mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                mBaiduMap
                        .setMyLocationConfigeration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
            } else if (v == mCyclingRecord) {
                ActivityJumpHelper.startActivity(MainActivity.this, CyclingRecords.class);
            } else if (v == mSettingView) {
                if (mDrawerLayout.isDrawerOpen(mLeftDrawer)) {
                    mDrawerLayout.closeDrawer(mLeftDrawer);
                } else {
                    mLeftDrawer.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            switch (motionEvent.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    return true;
                                case MotionEvent.ACTION_UP:
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    break;
                            }
                            return false;
                        }
                    });
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            } else if (v == mLockBack || v == mLockView || v == mLockEnd) {
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
                    changeAppThemeMode(false);
                } else {
                    SkinManager.sharedInstance().setSkinMode(SkinConfig.SKIN_MODE_DAY);
                    writeLightInfo(false);
                    changeAppThemeMode(true);
                }
                //initView();
            } else if (v == mSideSetting) {
                ActivityJumpHelper.startActivity(MainActivity.this, SettingActivity.class);
            } else if (v == mXbirderHelper) {
               ActivityJumpHelper.startActivity(MainActivity.this, XBirderSelfCheckActivity.class);
            } else if (v == mConnectBtn) {
                onSearchClick();
            } else if (v == mRoundedImageView) {
                ActivityJumpHelper.startActivity(MainActivity.this, MySetting.class);
            }
        }
    };

    private ValueAnimator createDropAnimator(final ViewGroup view,int start,int end){
        ValueAnimator animator = ValueAnimator.ofInt(start,end);
        animator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener(){

                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator){
                        int value = (Integer) valueAnimator.getAnimatedValue();
//                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//                        layoutParams.height = mdc_show.getHeight()+value;
//                        //android.widget.RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
//                        //params.bottomMargin = value;
//                        //view.setLayoutParams(params);
//                        view.setLayoutParams(layoutParams);
                          //view.setTop(value);
                          view.setY(value);
            }
        });
        return animator;
    }

    private ValueAnimator createSpeedStartAnimator(int start, int end){
        ValueAnimator animator = ValueAnimator.ofInt(start,end);
        animator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int value = (Integer) valueAnimator.getAnimatedValue();
                        if (value > 35) {
                            value = 70 - value;
                        }
                        setSpeed(value);
                    }
                });
        return animator;
    }


    private void lock() {
        mLockEnd.setVisibility(View.INVISIBLE);
        mLockView.setVisibility(View.VISIBLE);
        writeLock(true);
    }

    private void unLock() {
        mLockView.setVisibility(View.INVISIBLE);
        mLockEnd.setVisibility(View.VISIBLE);
        writeLock(false);
    }

    private void setMode(int mode, boolean needSend) {
        StatusConfig.CURRENT_MODE = mode;
        mButtonE.setImageBitmap(getTranBitmap());
        mButtonN.setImageBitmap(getTranBitmap());
        mButtonS.setImageBitmap(getTranBitmap());
        mTextE.setEnabled(false);
        mTextN.setEnabled(false);
        mTextS.setEnabled(false);
        TypedArray ta;
        Drawable drawable;
        int[] attrs;
        int level = mode;
        AccountManager.sharedInstance().setLastSpeedLevel(String.valueOf(level));

        int skinMode = SkinManager.sharedInstance().getSkinMode();
        switch (mode) {
            case StatusConfig.MODE_E:
//                attrs = new int[]{R.attr.btn_e_drawable};
//                ta = this.obtainStyledAttributes(attrs);
//                drawable = ta.getDrawable(0);
//                ta.recycle();

                if (skinMode == SkinConfig.SKIN_MODE_DAY) {
                    mButtonE.setImageResource(R.drawable.icon_e);
                }else{
                    mButtonE.setImageResource(R.drawable.icon_e_night);
                }

               //mButtonE.setImageDrawable(drawable);
                mTextE.setEnabled(true);
                break;
            case StatusConfig.MODE_N:
//                attrs = new int[]{R.attr.btn_n_drawable};
//                ta = this.obtainStyledAttributes(attrs);
//                drawable = ta.getDrawable(0);
//                ta.recycle();
                //mButtonN.setImageDrawable(drawable);
                if (skinMode == SkinConfig.SKIN_MODE_DAY) {
                    mButtonN.setImageResource(R.drawable.icon_n);
                }else{
                    mButtonN.setImageResource(R.drawable.icon_n_night);
                }
                mTextN.setEnabled(true);
                break;
            case StatusConfig.MODE_S:
//                attrs = new int[]{R.attr.btn_s_drawable};
//                ta = this.obtainStyledAttributes(attrs);
//                drawable = ta.getDrawable(0);
//                ta.recycle();
                //mButtonS.setImageDrawable(drawable);
                if (skinMode == SkinConfig.SKIN_MODE_DAY) {
                    mButtonS.setImageResource(R.drawable.icon_s);
                }else{
                    mButtonS.setImageResource(R.drawable.icon_s_night);
                }
                mTextS.setEnabled(true);
                break;
        }
        if (needSend) {
            mIsChangeMode = true;
            writeSpeedInfo(level);
            mHandler.postDelayed(mChangingModeRunnable, 500);
        }
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
                mConnectBtn.setImageResource(R.drawable.search);
                animationDrawable = (AnimationDrawable) mConnectBtn.getDrawable();
                animationDrawable.start();
                break;
            case isConnecting:
//                animationDrawable = (AnimationDrawable) mConnectBtn.getDrawable();
//                animationDrawable.stop();
//                mConnectBtn.setImageResource(R.drawable.search_unable);
                mConnectBtn.setBackgroundResource(R.drawable.search);
                animationDrawable = (AnimationDrawable) mConnectBtn.getDrawable();
                animationDrawable.start();
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

    private void onSearchClick()
    {
        switch (mConnectionState) {
            case isNull:
                mConnectionState=connectionStateEnum.isScanning;
                onConectionStateChange(mConnectionState);
                mHandler.postDelayed(mSearchOverTimeRunnable, 3000);
                scanLeDevice(true);
                break;
            case isToScan:
                mConnectionState=connectionStateEnum.isScanning;
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
                mConnectionState=connectionStateEnum.isScanning;
                onConectionStateChange(mConnectionState);
                mHandler.postDelayed(mSearchOverTimeRunnable, 3000);
                scanLeDevice(true);
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
                HuApplication.sharedInstance().XBirdBluetoothManager().getBluetoothLeService().disconnect();
                mIsReconnect = true;
                mHandler.postDelayed(mDisonnectingOverTimeRunnable, 1000);
                mIsFirstConnect = false;
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
            HuApplication.sharedInstance().XBirdBluetoothManager().getBluetoothLeService().close();
        }
    };

    private Runnable mDisonnectingOverTimeRunnable = new Runnable() {

        @Override
        public void run() {
            if (mConnectionState == connectionStateEnum.isDisconnecting)
                mConnectionState = connectionStateEnum.isToScan;
            onConectionStateChange(mConnectionState);
            HuApplication.sharedInstance().XBirdBluetoothManager().getBluetoothLeService().close();
            mNeedToast = true;
            onSearchClick();
        }
    };

    private Runnable mSearchOverTimeRunnable = new Runnable() {

        @Override
        public void run() {
            if (mConnectionState == connectionStateEnum.isScanning)
                mConnectionState = connectionStateEnum.isToScan;
            if (mLeDeviceListAdapter.getCount() == 0) {
                if (mNeedToast) {
                    toast("搜索不到锋鸟:(");
                    mNeedToast = false;
                }
                scanLeDevice(false);
                onConectionStateChange(connectionStateEnum.isNull);
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        onSearchClick();
                    }
                }, 500);
                //onSearchClick();

            } else if (mLeDeviceListAdapter.getCount() == 1) {
                String lastConnectBluetooth = AccountManager.sharedInstance().getConnectBluetooth();
                BluetoothDevice device = mLeDeviceListAdapter.getDevice(0);
                if (device == null)
                    return;
                mDeviceName = device.getName().toString();
                mDeviceAddress = device.getAddress().toString();

                scanLeDevice(false);
                System.out.println("onListItemClick " + device.getName().toString());

                if (lastConnectBluetooth.equals(mDeviceName)) {
                    scanLeDevice(false);
                    System.out.println("onListItemClick " + device.getName().toString());

                    System.out.println("Device Name:" + device.getName() + "   " + "Device Name:" + device.getAddress());

                    if (HuApplication.sharedInstance().XBirdBluetoothManager().getBluetoothLeService().connect(mDeviceAddress)) {
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
                    //initScanAlert();
                    initScanAlert2();
                }
            } else {
                //initScanAlert();
                initScanAlert2();
            }
        }
    };
    private void initScanAlert2(){
        mScanDeviceDialog = new CustomAlertDialog(mainContext);
        mScanDeviceDialog.showDialog(R.layout.custom_alert_dialog_scan, new CustomAlertDialog.IHintDialog() {
            @Override
            public void onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mScanDeviceDialog.dismissDialog();
                }
            }

            @Override
            public void showWindowDetail(Window window) {
                TextView title = (TextView) window.findViewById(R.id.tv_title);
                ListView scan_listview = (ListView) window.findViewById(R.id.scan_listview);
                scan_listview.setAdapter(mLeDeviceListAdapter);

                scan_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                        if (device == null)
                            return;
                        scanLeDevice(false);
                        //System.out.println("onListItemClick " + device.getName().toString());

                        //System.out.println("Device Name:" + device.getName() + "   " + "Device Name:" + device.getAddress());

                        mDeviceName = device.getName().toString();
                        mDeviceAddress = device.getAddress().toString();

                        if (mDeviceName.equals("No Device Available") && mDeviceAddress.equals("No Address Available")) {
                            mConnectionState = connectionStateEnum.isToScan;
                            onConectionStateChange(mConnectionState);
                        } else {
                            if (HuApplication.sharedInstance().XBirdBluetoothManager().getBluetoothLeService().connect(mDeviceAddress)) {
                                Log.d(TAG, "Connect request success");
                                mConnectionState = connectionStateEnum.isConnecting;
                                onConectionStateChange(mConnectionState);
                                mScanDeviceDialog.dismissDialog();
//                                mHandler.postDelayed(mConnectingOverTimeRunnable, 10000);
                            } else {
                                Log.d(TAG, "Connect request fail");
                                mConnectionState = connectionStateEnum.isToScan;
                                onConectionStateChange(mConnectionState);
                            }
                        }
                    }
                });
            }
        });
    }
//    private void initScanAlert() {
//
//
//        // Initializes and show the scan Device Dialog
//        mScanDeviceDialog = new AlertDialog.Builder(mainContext)
//                .setTitle("搜索锋鸟").setAdapter(mLeDeviceListAdapter, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(which);
//                        if (device == null)
//                            return;
//                        scanLeDevice(false);
//                        System.out.println("onListItemClick " + device.getName().toString());
//
//                        System.out.println("Device Name:" + device.getName() + "   " + "Device Name:" + device.getAddress());
//
//                        mDeviceName = device.getName().toString();
//                        mDeviceAddress = device.getAddress().toString();
//
//                        if (mDeviceName.equals("No Device Available") && mDeviceAddress.equals("No Address Available")) {
//                            mConnectionState = connectionStateEnum.isToScan;
//                            onConectionStateChange(mConnectionState);
//                        } else {
//                            if (HuApplication.sharedInstance().XBirdBluetoothManager().getBluetoothLeService().connect(mDeviceAddress)) {
//                                Log.d(TAG, "Connect request success");
//                                mConnectionState = connectionStateEnum.isConnecting;
//                                onConectionStateChange(mConnectionState);
////                                mHandler.postDelayed(mConnectingOverTimeRunnable, 10000);
//                            } else {
//                                Log.d(TAG, "Connect request fail");
//                                mConnectionState = connectionStateEnum.isToScan;
//                                onConectionStateChange(mConnectionState);
//                            }
//                        }
//                    }
//                })
//                .setOnCancelListener(new DialogInterface.OnCancelListener() {
//
//                    @Override
//                    public void onCancel(DialogInterface arg0) {
//                        System.out.println("mBluetoothAdapter.stopLeScan");
//
//                        mConnectionState = connectionStateEnum.isToScan;
//                        onConectionStateChange(mConnectionState);
//                        mScanDeviceDialog.dismiss();
//
//                        scanLeDevice(false);
//                    }
//                }).create();
//        mScanDeviceDialog.show();
//    }


    private void setSpeed(int speed) {
        mVelocityRollView.setPercent(100 * speed / 35);
        mSpeedText.setText(String.valueOf(speed));

        mdc_speed_num.setText(String.valueOf(speed));

        String mdc_mileage_numStr = AccountManager.sharedInstance().getStoreDistance();
//        int i = Integer.parseInt(mdc_mileage_numStr);
//        float mdc_mileage_numFloat = (float) i/1000;
        float mdc_mileage_numFloat = Float.parseFloat(mdc_mileage_numStr) / 1000.0f;
        DecimalFormat decimalFormat=new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String mdc_mileage_numStr2 = decimalFormat.format(mdc_mileage_numFloat);
        mdc_mileage_num.setText(mdc_mileage_numStr2);
    }

    private void setBattery(int battery) {
        mBatteryRollView.setPercent(battery);
        mBatteryView.setText(String.valueOf(battery));
        if(battery <= 20){
            mBatteryView.setTextColor(getResources().getColor(R.color.battery_percent_low));
            mBatteryViewUint.setTextColor(getResources().getColor(R.color.battery_percent_low));
        }else{
            mBatteryView.setTextColor(getResources().getColor(R.color.battery_percent_high));
            mBatteryViewUint.setTextColor(getResources().getColor(R.color.battery_percent_high));
        }
        mdc_power_num.setText(String.valueOf(battery));
    }

    private void getWeather(final String city){
        if(StringHelper.checkString(city)) {
        WeatherRequest request = new WeatherRequest(new HttpResponse.Listener<JSONObject>() {
                @Override
                public void onResponse(HttpResponse<JSONObject> response) {
                    if (response.isSuccess()) {
                        try {
                            if (response.result.getInt("error") == 0) {
                                //String accessToken = response.result.getJSONObject("user").getString("accessToken");
                                String name = response.result.getJSONObject("data").getString("city");
                                String low = "-";
                                String high = "-";
                                String dayWeather = "";

                                JSONArray datas = response.result.getJSONObject("data").getJSONArray("weather");
                                if (datas != null) {
                                    JSONObject temp = datas.getJSONObject(0);
                                    low = temp.getString("nightTemp");
                                    high = temp.getString("dayTemp");
                                    dayWeather = temp.getString("dayWeather");
                                    mWeatherTextString = name+" "+low+"到"+high+"℃ "+dayWeather;
                                    mWeatherText.setText(mWeatherTextString);
                                }
                            } else {
                                getWeather(city);
                                toast("失败");
                                //mWeatherText.setText("获取天气失败");
                            }
                        } catch (Exception e) {
                            getWeather(city);
                            toast("失败2");
                        }
                    }

                    else{
                        VolleyError tt = response.error;
                        getWeather(city);
                    }

                }
            });
            request.setParam(city);
            sendRequest(request);
        }
    }

    /**
     * 启动百度地图骑行导航(Native)
     *
     */
    public void startNavi() {
        if(mBDLocation==null){
            return;
        }
        LatLng pt1 = new LatLng(mBDLocation.getLatitude(), mBDLocation.getLongitude());
        //LatLng pt2 = new LatLng(mLat2, mLon2);

        // 构建 导航参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(pt1).endPoint(pt1)
                .startName("起点").endName("终点");

        try {
            BaiduMapNavigation.openBaiduMapNavi(para, this);
        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            //showDialog();
        }

    }

//    /**
//     * 启动百度地图Poi周边检索
//     */
//    public void startPoiNearbySearch() {
//        if(mBDLocation==null){
//            return;
//        }
//        //LatLng ptCenter = new LatLng(mLat1, mLon1); // 天安门
//        LatLng ptCenter = new LatLng(mBDLocation.getLatitude(), mBDLocation.getLongitude());
//
//        PoiParaOption para = new PoiParaOption()
//                .key("")
//                .center(ptCenter)
//                .radius(2000);
//
//        try {
//            BaiduMapPoiSearch.openBaiduMapPoiNearbySearch(para, this);
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//
//    }
//    /**
//     * 启动百度地图Poi详情页面
//     */
//    public void startPoiDetails() {
//       // PoiParaOption para = new PoiParaOption().uid("65e1ee886c885190f60e77ff"); // 天安门
//        PoiParaOption para = new PoiParaOption().uid(""); // 天安门
//        try {
//            BaiduMapPoiSearch.openBaiduMapPoiDetialsPage(para, this);
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//
//    }
    private void checkHeadImage() {
        String storePhone = AccountManager.sharedInstance().getUser();
        String storePass = AccountManager.sharedInstance().getPass();
        if (storePhone != null && storePhone != "") {
            if (storePass != null && storePass != "") {
                LoginRequest request = new LoginRequest(new HttpResponse.Listener<JSONObject>() {
                    @Override
                    public void onResponse(HttpResponse<JSONObject> response) {
                        if (response.isSuccess()) {
                            try {
                                if (response.result.getString("error").equals("0")) {
                                    String avatar = response.result.getJSONObject("user").getString("avatar");
                                    if (avatar != null && avatar != "") {
                                        String avatarFileName = avatar.substring(10,avatar.length());
                                        File pictureFileDir = new File(Environment.getExternalStorageDirectory(), "/xbird/pic");
                                        File picFile = new File(pictureFileDir, avatarFileName);
                                        if (picFile.exists()) {

                                        }else{
                                            //需要跟换头像
                                            if (!pictureFileDir.exists()) {
                                                pictureFileDir.mkdirs();
                                            }
                                            if (!picFile.exists()) {
                                                picFile.createNewFile();
                                            }


                                        }

                                    }else{

                                    }

                                    String userName = response.result.getJSONObject("user").getString("userName");
                                    if (userName != null && userName != "") {
                                        if(!userName.equals(AccountManager.sharedInstance().getUsername())){
                                            AccountManager.sharedInstance().setUserName(userName);
                                            //更改显示名称
                                            mUser_name.setText(userName);
                                        }
                                    }else{
                                            //不做更改

                                    }
                                } else {
                                    toast("密码已更改");
                                    ActivityJumpHelper.startActivity(MainActivity.this, LoginActivity.class);
                                    AccountManager.sharedInstance().setPass("");
                                    MainActivity.this.finish();
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                });
                request.setParam(storePhone, storePass);
                sendRequest(request);
            }
        }
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

    private void checkNameAndAvatar(){
        LoginTokenRequest request = new LoginTokenRequest(new HttpResponse.Listener<JSONObject>() {
            @Override
            public void onResponse(HttpResponse<JSONObject> response) {
                if (response.isSuccess()) {
                    try {
                        if (response.result.getString("error").equals("0")) {


                            String userName = response.result.getJSONObject("user").getString("userName");
                            if(userName!=AccountManager.sharedInstance().getUsername()){
                                AccountManager.sharedInstance().setUserName(userName);
                                mUser_name.setText(userName);
                            }

                            String avatar = response.result.getJSONObject("user").getString("avatar");
                            if(!avatar.equals(AccountManager.sharedInstance().getAvatarName()) ){
                                AccountManager.sharedInstance().setAvatarName(avatar);
                                if(avatar!=null&&avatar.length()>0){
                                    avatar = avatar.substring(avatar.lastIndexOf("/") + 1,avatar.length());
                                    filedown(NetworkConfig.SERVER_ADDRESS_AVATAR_DOWN_HEAD + avatar);
                                }
                            }else{
                                //验证是否存在图片avatarName
                                avatar = avatar.substring(avatar.lastIndexOf("/") + 1,avatar.length());
                                String newFilename = Environment.getExternalStorageDirectory() + "/xbird/pic/" + avatar;
                                File file = new File(newFilename);
                                //如果目标文件已经存在，则删除。产生覆盖旧文件的效果
                                if(!file.exists())
                                {
                                    filedown(NetworkConfig.SERVER_ADDRESS_AVATAR_DOWN_HEAD + avatar);
                                }
                            }
                        } else {
                            toast("登陆失败");
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
        String sToken = AccountManager.sharedInstance().getToken();
        if(sToken != null && sToken.length()>0){
            request.setParam(sToken);
            sendRequest(request);
        }


    }

    private void filedown(String urlStr) {
        final String murlStr = urlStr;

        new Thread(new Runnable() {
            @Override
            public void run() {
                //另起线程执行下载，安卓最新sdk规范，网络操作不能再主线程。
                File fileDir = new File(Environment.getExternalStorageDirectory(), "/xbird/pic/");
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                String fileName = murlStr.substring(murlStr.lastIndexOf("/") + 1,murlStr.length());
                String newFilename = Environment.getExternalStorageDirectory() + "/xbird/pic/" + fileName;
                File file = new File(newFilename);
                //如果目标文件已经存在，则删除。产生覆盖旧文件的效果
                if(file.exists())
                {
                    file.delete();
                }
                try {
                    // 构造URL
                    URL url = new URL(murlStr);
                    // 打开连接
                    URLConnection con = url.openConnection();
                    //获得文件的长度
                    int contentLength = con.getContentLength();
                    //System.out.println("长度 :"+contentLength);
                    // 输入流
                    InputStream is = con.getInputStream();
                    // 1K的数据缓冲
                    byte[] bs = new byte[1024];
                    // 读取到的数据长度
                    int len;
                    // 输出的文件流
                    OutputStream os = new FileOutputStream(newFilename);
                    // 开始读取
                    while ((len = is.read(bs)) != -1) {
                        os.write(bs, 0, len);
                    }
                    // 完毕，关闭所有链接
                    os.close();
                    is.close();

                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.sendToTarget();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    avatarName = AccountManager.sharedInstance().getAvatarName();
                    if(avatarName != null && avatarName.length()>0){
                        avatarName = avatarName.substring(avatarName.lastIndexOf("/")+1,avatarName.length());
                        String picPath = Environment.getExternalStorageDirectory()+"/xbird/pic";
                        File picfile = new File(picPath,avatarName);
                        if (picfile.exists()) {
                            Bitmap bitmap = decodeUriAsBitmap(Uri.fromFile(picfile));
                            Bitmap roundBitMap = MySetting.getRoundedCornerBitmap(bitmap, 1.0f);
                            mRoundedImageView.setImageBitmap(roundBitMap);
                        }
                    }
                    break;
            }
        }
    };


    private void resumeAvatarAndName(){
        if(!avatarName.equals(AccountManager.sharedInstance().getAvatarName())) {
            avatarName = AccountManager.sharedInstance().getAvatarName();
            if (avatarName != null && avatarName.length() > 0) {
                avatarName = avatarName.substring(avatarName.lastIndexOf("/") + 1, avatarName.length());
                String picPath = Environment.getExternalStorageDirectory() + "/xbird/pic";
                File picfile = new File(picPath, avatarName);
                if (picfile.exists()) {
                    Bitmap bitmap = decodeUriAsBitmap(Uri.fromFile(picfile));
                    Bitmap roundBitMap = MySetting.getRoundedCornerBitmap(bitmap, 1.0f);
                    mRoundedImageView.setImageBitmap(roundBitMap);
                }
            }
        }

        String muserName = AccountManager.sharedInstance().getUsername();
        if(muserName != null && muserName.length() > 0){
            mUser_name.setText(muserName);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeAvatarAndName();
        mMapView.onResume();
        if (mBluetoothAdapter == null) {
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mConnectionState != connectionStateEnum.isConnected) {
            onSearchClick();
        }


    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//
//        ValueAnimator animator = createSpeedStartAnimator(mpenContent, 0, 70);
//        animator.setDuration(2000);
//        animator.start();
//    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
//        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //.onDestroy();
        mMapView = null;

        super.onDestroy();
        unbindService(mServiceConnection);
        HuApplication.sharedInstance().XBirdBluetoothManager().setBluetoothLeService(null);
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

                        if (mIsReconnect == false) {
                            String lastConnectBluetooth = AccountManager.sharedInstance().getConnectBluetooth();

                            if (lastConnectBluetooth != "" && lastConnectBluetooth.equals(deviceName)) {
                                mHandler.removeCallbacks(mSearchOverTimeRunnable);
                                scanLeDevice(false);
                                mDeviceName = deviceName;
                                mDeviceAddress = device.getAddress().toString();
                                if (HuApplication.sharedInstance().XBirdBluetoothManager().getBluetoothLeService().connect(mDeviceAddress)) {
                                    Log.d(TAG, "Connect request success");
                                    mConnectionState = connectionStateEnum.isConnecting;
                                    onConectionStateChange(mConnectionState);
                                } else {
                                    Log.d(TAG, "Connect request fail");
                                    mConnectionState = connectionStateEnum.isToScan;
                                    onConectionStateChange(mConnectionState);
                                }
                            }
                        }
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
//                viewHolder.deviceAddress = (TextView) view
//                        .findViewById(R.id.device_address);
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
            //viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        //TextView deviceAddress;
    }

    void scanLeDevice(final boolean enable) {
        HuApplication.sharedInstance().XBirdBluetoothManager().setIsConnect(false);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }




}
