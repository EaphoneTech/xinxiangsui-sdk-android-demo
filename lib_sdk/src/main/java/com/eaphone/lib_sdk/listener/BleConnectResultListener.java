package com.eaphone.lib_sdk.listener;


/**
 * 蓝牙连接回调接口
 */
public interface BleConnectResultListener {

    /**
     * ble连接成功
     */
    void onConnectSucceed();

    /**
     * ble连接失败
     * @param result  失败原因
     */
    void onConnectError(String result);
}
