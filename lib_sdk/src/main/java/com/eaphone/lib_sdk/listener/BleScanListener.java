package com.eaphone.lib_sdk.listener;

import android.bluetooth.BluetoothDevice;

import java.util.List;


/**
 * 获取附近心相随设备回调接口
 */
public interface BleScanListener {
    /**
     * 扫描开始
     */
    void onScanStarted();

    /**
     * 扫描到设备
     * @param device  BluetoothDevice设备
     */
    void onLeScan(BluetoothDevice device);


    /**
     * 扫描结束
     * @param scanResultList  扫描结束返回总设备列表
     */
    void onScanFinished(List<BluetoothDevice> scanResultList);


    /**
     * 扫描失败
     * @param result  失败原因
     */
    void onError(String result);
}
