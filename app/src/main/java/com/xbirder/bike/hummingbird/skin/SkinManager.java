package com.xbirder.bike.hummingbird.skin;

import com.xbirder.bike.hummingbird.util.SharedPreferenceHelper;

/**
 * Created by zhengxin on 15/7/6.
 */
public class SkinManager {
    private static SkinManager mInstance;
    private static final String SKIN_KEY = "skin_mode";


    private int mCurrentSkinMode = SkinConfig.SKIN_MODE_DAY;

    private SkinManager(){
        mCurrentSkinMode = SharedPreferenceHelper.getInt(SKIN_KEY,SkinConfig.SKIN_MODE_DAY);
    }

    public static SkinManager sharedInstance(){
        if(mInstance == null){
            mInstance = new SkinManager();
        }

        return mInstance;
    }

    public int getSkinMode(){
        return mCurrentSkinMode;
    }

    public void setSkinMode(int mode){
        mCurrentSkinMode = mode;
        SharedPreferenceHelper.saveInt(SKIN_KEY, mCurrentSkinMode);
    }

}

