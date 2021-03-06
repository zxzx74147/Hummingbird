package com.xbirder.bike.hummingbird;


import com.xbirder.bike.hummingbird.bluetooth.BluetoothLeService;
import com.xbirder.bike.hummingbird.bluetooth.XBirdBluetoothConfig;
import com.xbirder.bike.hummingbird.util.SharedPreferenceHelper;

/**
 * Created by zhengxin on 2015/7/10.
 */
public class AccountManager {
    private static AccountManager mInstance;
    private static final String KEY_TOKEN = "xbird_token";
    private static final String KEY_USER = "xbird_user";
    private static final String KEY_PASS = "xbird_pass";
    private static final String KEY_FINAL_TOKEN = "xbird_final_token";
    private static final String KEY_CONNECT_BLUETOOTH = "xbird_bluetooth";
    private static final String KEY_USER_NAME = "xbird_username";
    private static final String KEY_SEX = "xbird_sex";
    private static final String KEY_LAST_SPEED_LEVEL = "last_speed_level";
    private static final String KEY_STORE_DATE = "store_date";
    private static final String KEY_STORE_DISTANCE = "store_distance";
    private static final String KEY_STORE_RUNTIME = "store_runtime";
    private String mUser;
    private String mPass;
    private String mToken;
    private String mUserName;
    private String mSex;
    private String mLastSpeedLevel;
    private String mFinalToken;
    private String mStoreDate;
    private String mStoreDistance;
    private String mStoreRuntime;


    public String getStoreDate() {
        return mStoreDate;
    }

    public void setStoreDate(String storeDate) {
        this.mStoreDate = storeDate;
        SharedPreferenceHelper.saveString(KEY_STORE_DATE + mUser, this.mStoreDate);
    }

    public String getStoreDistance() {
        return mStoreDistance;
    }

    public void setStoreDistance(String storeDistance) {
        this.mStoreDistance = storeDistance;
        SharedPreferenceHelper.saveString(KEY_STORE_DISTANCE + mUser, this.mStoreDistance);
    }

    public String getStoreRuntime() {
        return mStoreRuntime;
    }

    public void setStoreRuntime(String storeRuntime) {
        this.mStoreRuntime = storeRuntime;
        SharedPreferenceHelper.saveString(KEY_STORE_RUNTIME + mUser, this.mStoreRuntime);
    }

    public String getConnectBluetooth() {
        return mConnectBluetooth;
    }

    public void setConnectBluetooth(String connectBluetooth) {
        this.mConnectBluetooth = connectBluetooth;
        SharedPreferenceHelper.saveString(KEY_CONNECT_BLUETOOTH, connectBluetooth);
    }

    private String mConnectBluetooth;

    private AccountManager(){
        mUser = SharedPreferenceHelper.getString(KEY_USER, "");
        mPass = SharedPreferenceHelper.getString(KEY_PASS, "");
        mToken = SharedPreferenceHelper.getString(KEY_TOKEN, "");
        mFinalToken = SharedPreferenceHelper.getString(mUser, "");
        mUserName = SharedPreferenceHelper.getString(KEY_USER_NAME, "");
        mConnectBluetooth = SharedPreferenceHelper.getString(KEY_CONNECT_BLUETOOTH, "");
        mLastSpeedLevel = SharedPreferenceHelper.getString(KEY_LAST_SPEED_LEVEL + mUser, "");
        mStoreDate = SharedPreferenceHelper.getString(KEY_STORE_DATE + mUser, "");
        mStoreDistance = SharedPreferenceHelper.getString(KEY_STORE_DISTANCE + mUser, "0");
        mStoreRuntime = SharedPreferenceHelper.getString(KEY_STORE_RUNTIME + mUser, "0");
    }
    public static AccountManager sharedInstance(){
        if(mInstance == null){
            mInstance = new AccountManager();
        }
        return mInstance;
    }

    public String getToken(){
        return mToken;
    }

    public String getUser() {
        return mUser;
    }

    public String getPass() {
        return mPass;
    }

    public String getUsername(){
        return mUserName;
    }

    public String getSex(){
        return mSex;
    }

    public void setPass(String pass) {
        this.mPass = pass;
        SharedPreferenceHelper.saveString(KEY_PASS, mPass);
    }

    public void setUser(String user) {
        this.mUser = user;
        SharedPreferenceHelper.saveString(KEY_USER, mUser);
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
        SharedPreferenceHelper.saveString(KEY_USER_NAME, mUserName);
    }

    public void setSex(String sex) {
        this.mSex = sex;
        SharedPreferenceHelper.saveString(KEY_SEX, mSex);
    }

    public void setToken(String token){
        mToken = token;
        calFinalToken();
        SharedPreferenceHelper.saveString(KEY_TOKEN, mToken);
        System.out.println("mToken : " + mToken);
    }

    public void calFinalToken() {
        StringBuffer tokenStr = new StringBuffer(6);
        if (mToken.length() < 6) {
            return;
        }
        char [] cList = mToken.toCharArray();
        for (int i = 0; i < 6; i++) {
            int asciiCode = cList[i]; // 65
            int finalChar = asciiCode % 10;
            tokenStr.append(finalChar);
        }
        setFinalToken(tokenStr.toString());
    }

    public String getFinalToken() {
        return mFinalToken;
    }

    public void setFinalToken(String finalToken) {
        mFinalToken = finalToken;
        SharedPreferenceHelper.saveString(getUser(), mFinalToken);
    }

    public String getLastSpeedLevel() {
        return mLastSpeedLevel;
    }

    public void setLastSpeedLevel(String lastSpeedLevel) {
        this.mLastSpeedLevel = lastSpeedLevel;
        SharedPreferenceHelper.saveString(KEY_LAST_SPEED_LEVEL + mUser, this.mLastSpeedLevel);
    }

}
