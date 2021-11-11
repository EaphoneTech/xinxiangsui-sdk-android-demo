package com.eaphone.lib_sdk.listener;


/**
 * 蓝牙连接回调接口
 * @Author: he lin hua
 * @CreateDate: 2020/9/8 15:43
 * @Version: 1.0
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
