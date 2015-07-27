package com.xbirder.bike.hummingbird;


import com.xbirder.bike.hummingbird.bluetooth.BluetoothLeService;
import com.xbirder.bike.hummingbird.util.SharedPreferenceHelper;

/**
 * Created by zhengxin on 2015/7/10.
 */
public class AccountManager {
    private static AccountManager mInstance;
    private static final String KEY_TOKEN = "xbird_token";
    private static final String KEY_USER = "xbird_user";
    private static final String KEY_PASS = "xbird_pass";
    private String mUser;
    private String mPass;
    private String mToken;

    private AccountManager(){
        mUser = SharedPreferenceHelper.getString(KEY_USER, "");
        mPass = SharedPreferenceHelper.getString(KEY_PASS, "");
        mToken = SharedPreferenceHelper.getString(KEY_TOKEN, "");
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

    public void setPass(String pass) {
        this.mPass = pass;
        SharedPreferenceHelper.saveString(KEY_PASS, mPass);
    }

    public void setUser(String user) {
        this.mUser = user;
        SharedPreferenceHelper.saveString(KEY_USER, mUser);
    }

    public void setToken(String token){
        mToken = token;
        SharedPreferenceHelper.saveString(KEY_TOKEN, mToken);
    }
}
