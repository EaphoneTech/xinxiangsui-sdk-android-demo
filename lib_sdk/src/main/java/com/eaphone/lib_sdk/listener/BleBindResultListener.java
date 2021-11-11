package com.eaphone.lib_sdk.listener;

/**
 * 蓝牙配网回调接口
 * @Author: he lin hua
 * @CreateDate: 2020/9/8 15:43
 * @Version: 1.0
 */
public interface BleBindResultListener {
    /**
     * 绑定开始
     */
    void onBindStarted();

    /**
     * 绑定成功
     */
    void onBindSucceed();

    /**
     * 绑定失败
     * @param result  失败原因
     */
    void onBindError(String result);
}
