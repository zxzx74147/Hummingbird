package com.xbirder.bike.hummingbird;


import com.xbirder.bike.hummingbird.util.SharedPreferenceHelper;

/**
 * Created by zhengxin on 2015/7/10.
 */
public class AccountManager {
    private static AccountManager mInstance;
    private static final String KEY_TOKEN = "token";
    private String mToken;

    private AccountManager(){
        mToken = SharedPreferenceHelper.getString(KEY_TOKEN);
    }
    public static AccountManager sharedInstance(){
        if(mInstance == null){
            mInstance = new AccountManager();
        }
        return mInstance;
    }

    public String getToken(){
        return "a";
//        return mToken;
    }

    public void setToken(String token){
        mToken = token;
        SharedPreferenceHelper.saveString(KEY_TOKEN,mToken);
    }
}
