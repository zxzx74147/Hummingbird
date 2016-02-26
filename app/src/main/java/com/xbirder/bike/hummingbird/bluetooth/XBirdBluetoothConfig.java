package com.xbirder.bike.hummingbird.bluetooth;

/**
 * Created by xiangtingting on 15/7/23.
 */
public class XBirdBluetoothConfig {
    public static final byte PREFIX = (byte)170;
    public static final byte END = (byte)0x55;

    public static final byte CONNECT = (byte)0x01;
    public static final byte ERROR = (byte)0x02;
    public static final byte INFO = (byte)0x03;
    public static final byte LIGHT = (byte)0x04;
    public static final byte SPEED = (byte)0x05;
    public static final byte LOCK = (byte)0x06;
    public static final byte CHANGE_PASS = (byte)0x07;
    public static final byte RESET = (byte)0x09;

    public static final byte CONNECT_ERROR = (byte)0x01;
    public static final byte LOCK_ERROR = (byte)0x02;

    public static final byte DETECTION_START = (byte)0x0C;
    public static final byte DETECTION_DATA = (byte)0xC8;
    public static final byte DETECTION_END = (byte)0x0D;
    public static final byte DETECTION_OK = (byte)0x00;
    public static final byte DETECTION_ERROR = (byte)0x01;

    public static final byte CHARGING_SHIELD = (byte)0x0E;
    public static final byte OFF_LINE_MODE = (byte)0x0B;

    public static final byte XBIRD_SELF_CHECK = (byte)0x0A;


}
