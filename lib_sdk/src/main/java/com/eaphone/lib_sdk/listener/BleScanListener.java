package com.eaphone.lib_sdk.listener;

import android.bluetooth.BluetoothDevice;

import java.util.List;


/**
 * 获取附近心相随设备回调接口
 * @Author: he lin hua
 * @CreateDate: 2020/9/8 15:43
 * @Version: 1.0
 */
public interface BleScanListener {
    /**
     * 扫描开始
     */
    void onScanStarted();

    /**
     * 扫描到设备
     * @param device  设备
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
