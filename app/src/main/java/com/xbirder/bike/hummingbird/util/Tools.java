package com.xbirder.bike.hummingbird.util;

import android.os.Environment;

/**
 * Created by Administrator on 2015/8/25.
 */
public class Tools {
    /**
     * 检查是否存在SDCard
     * @return
     */
    public static boolean hasSdcard(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }
}
