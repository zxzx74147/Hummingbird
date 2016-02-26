package com.xbirder.bike.hummingbird.config;

/**
 * Created by zhengxin on 2015/7/13.
 */
public class NetworkConfig {

    public static final String SERVER_ADDRESS = "http://120.26.43.158/xbird/web/index.php";
    //public static final String SERVER_ADDRESS = "http://120.26.43.158/xbird_new_dev/web/index.php";
//    public static final String SERVER_ADDRESS_DEV = "http://120.26.43.158/xbird_new_dev/web/index.php";
//    public static final String SERVER_ADDRESS_DEV_HEAD = "http://120.26.43.158/xbird_new_dev/web";
//    public static final String SERVER_ADDRESS_AVATAR_DOWN_HEAD = "http://120.26.43.158/xbird_new_dev/uploaded/";
    public static final String SERVER_ADDRESS_DEV = "http://120.26.43.158/xbird/web/index.php";
    public static final String SERVER_ADDRESS_DEV_HEAD = "http://120.26.43.158/xbird/web";
    public static final String SERVER_ADDRESS_AVATAR_DOWN_HEAD = "http://120.26.43.158/xbird/uploaded/";

    public static final String REGISTER_ADDRESS = "user/register";
    public static final String LOGIN_ADDRESS = "user/login";
    public static final String RESET_ADDRESS = "user/resetpassword";
    public static final String AMEND_ADDRESS = "user/changepassword";
    public static final String USERNAME_ADDRESS = "user/changeusername";
    public static final String VATAR_ADDRESS = "user/changeavatar";
    public static final String SEX_ADDRESS = "user/changepassword";
    public static final String REQUSET_VCODE = "user/send-verify-code";
    public static final String VERIFY_VCODE = "user/verify";
    public static final String ADD_RECORD = "record/add";
    public static final String GET_RECORD_DAY = "record/get-stat-day";
    public static final String GET_RECORD_MONTH = "record/get-stat-month";
    public static final String CLEAR_RECORD = "record/clear";

    public static final String REQUSET_VCODE_REGISTER = "user/send-reg-verify-code";
    public static final String REGISTER_NEW_ADDRESS = "user/registerv2";


    public static final String WEATHER_ADDRESS = "user/weather";
    public static final String FIRMWARE_VERSION_ADDRESS = "version/latest";

    public static final String FEEDBACK_ADDRESS = "user/feedback";
}
