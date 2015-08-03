package com.xbirder.bike.hummingbird.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static boolean isPhoneNumberValid(String phoneNumber) {

        boolean isValid = false;
        /*
         * 可接受的电话格式有：
         */
        String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
        /*
         * 可接受的电话格式有：
         */
        String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);

        Pattern pattern2 = Pattern.compile(expression2);
        Matcher matcher2 = pattern2.matcher(inputStr);
        if (matcher.matches() || matcher2.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
