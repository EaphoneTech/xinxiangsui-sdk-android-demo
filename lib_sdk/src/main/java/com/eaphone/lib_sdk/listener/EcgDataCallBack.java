package com.eaphone.lib_sdk.listener;


import com.eaphone.lib_sdk.common.EcgReportData;

/**
 * 初始报告
 */
public interface EcgDataCallBack {

    /**
     * 获取成功
     * @param data EcgReportData
     */
    void onSucceed(EcgReportData data);

    /**
     * 初始化失败
     * @param errcode  错误码
     * @param message  失败原因
     */
    void onError(String errcode, String message);
}
