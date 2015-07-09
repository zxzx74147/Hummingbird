package com.xbirder.bike.hummingbird.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

/**
 * Created by zhengxin on 2015/7/9.
 */
public class ActivityJumpHelper {

    public static void startActivity(Activity mActivity ,Class<? extends Activity> mClass){
        Intent intent = new Intent(mActivity,mClass);
        mActivity.startActivity(intent);
    }

    public static void startActivityForResule(Activity mActivity ,Class<? extends Activity> mClass,int requestCode){
        Intent intent = new Intent(mActivity,mClass);
        mActivity.startActivityForResult(intent, requestCode);
    }

    public static void startActivity(Fragment mFragment ,Class<? extends Activity> mClass){
        Intent intent = new Intent(mFragment.getActivity(),mClass);
        mFragment.startActivity(intent);
    }

    public static void startActivityForResult(Fragment mFragment ,Class<? extends Activity> mClass,int requestCode){
        Intent intent = new Intent(mFragment.getActivity(),mClass);
        mFragment.startActivityForResult(intent, requestCode);
    }
}
