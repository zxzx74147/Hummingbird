package com.xbirder.bike.hummingbird.fonts;

import android.graphics.Typeface;
import android.widget.TextView;

import com.xbirder.bike.hummingbird.HuApplication;

/**
 * Created by zhengxin on 15/7/14.
 */
public class FontsManager {

    private Typeface mSpeedType;
    private Typeface mBatteryType;
    private Typeface mSpeedKMType;


    private static FontsManager mInstance = null;
    private FontsManager(){

    }

    public static  FontsManager sharedInstance(){
        if(mInstance == null){
            mInstance = new FontsManager();
        }
        return mInstance;
    }

    public void setSpeedKMType(TextView textView){
        try {
            if(mSpeedKMType == null) {
                mSpeedKMType = Typeface.createFromAsset(HuApplication.sharedInstance().getAssets(), "fonts/raleway.ttf");
            }
            textView.setTypeface(mSpeedKMType);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setSpeedType(TextView textView){
        try {
            if(mSpeedType == null) {
               // mSpeedType = Typeface.createFromAsset(HuApplication.sharedInstance().getAssets(), "fonts/HelveticaNeueLTPro-UltLt.otf");
                mSpeedType = Typeface.createFromAsset(HuApplication.sharedInstance().getAssets(), "fonts/DINCond-Bold.otf");
            }
            textView.setTypeface(mSpeedType);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setSpeedThickType(TextView textView){
        try {
            if(mSpeedType == null) {
                mSpeedType = Typeface.createFromAsset(HuApplication.sharedInstance().getAssets(), "fonts/Helvetica.ttf");
            }
            textView.setTypeface(mSpeedType);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setBatteryType(TextView textView){
        try {
            if(mBatteryType == null) {
                mBatteryType = Typeface.createFromAsset(HuApplication.sharedInstance().getAssets(), "fonts/HelveticaNeue-Thin.otf");
            }
            textView.setTypeface(mBatteryType);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
