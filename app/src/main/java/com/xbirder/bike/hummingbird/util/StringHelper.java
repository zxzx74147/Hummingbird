package com.xbirder.bike.hummingbird.util;

/**
 * Created by zhengxin on 2015/7/9.
 */
public class StringHelper {

    public static boolean checkString(String input){
        if(input == null || input.length() == 0){
            return false;
        }
        return true;
    }
}
