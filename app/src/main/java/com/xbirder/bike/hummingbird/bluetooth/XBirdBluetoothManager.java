package com.xbirder.bike.hummingbird.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

/**
 * Created by xiangtingting on 15/8/20.
 */
public class XBirdBluetoothManager {

    public BluetoothLeService getBluetoothLeService() {
        return mBluetoothLeService;
    }

    public void setBluetoothLeService(BluetoothLeService mBluetoothLeService) {
        this.mBluetoothLeService = mBluetoothLeService;
    }

    private BluetoothLeService mBluetoothLeService;

    public BluetoothGattCharacteristic getNotifyCharacteristic() {
        return mNotifyCharacteristic;
    }

    public void setNotifyCharacteristic(BluetoothGattCharacteristic mNotifyCharacteristic) {
        this.mNotifyCharacteristic = mNotifyCharacteristic;
    }

    private BluetoothGattCharacteristic mNotifyCharacteristic;

    public BluetoothGattService getCurrentService() {
        return mCurrentService;
    }

    public void setCurrentService(BluetoothGattService mCurrentService) {
        this.mCurrentService = mCurrentService;
    }

    private BluetoothGattService mCurrentService;

    public BluetoothGattCharacteristic getCurrentCharacteristic() {
        return mCurrentCharacteristic;
    }

    public void setCurrentCharacteristic(BluetoothGattCharacteristic mCurrentCharacteristic) {
        this.mCurrentCharacteristic = mCurrentCharacteristic;
    }

    private BluetoothGattCharacteristic mCurrentCharacteristic;

    public static  XBirdBluetoothManager sharedInstance(){
        XBirdBluetoothManager mInstance = null;
        if (mInstance == null) {
            mInstance = new XBirdBluetoothManager();
        }
        return mInstance;
    }

    public void sendToBluetooth(byte[] bytes) {
        if (getCurrentCharacteristic() != null) {
            getCurrentCharacteristic().setValue(bytes);

            getBluetoothLeService().setCharacteristicNotification(getCurrentCharacteristic(), true);
            getBluetoothLeService().writeCharacteristic(getCurrentCharacteristic());
        }
    }
}
