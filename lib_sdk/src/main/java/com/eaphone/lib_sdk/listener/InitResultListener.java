package com.eaphone.lib_sdk.listener;


/**
 * sdk初始化回调接口
 */
public interface InitResultListener {

    /**
     * 初始化成功
     */
    void onSucceed();

    /**
     * 初始化失败
     * @param result  失败原因
     */
    void onError(String result);
}
