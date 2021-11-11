package com.eaphone.lib_sdk.listener;

import java.util.List;

/**
 * 获取实时波形数据回调接口
 * @Author: he lin hua
 * @CreateDate: 2020/9/8 15:43
 * @Version: 1.0
 */
public interface EcgDataResultListener {

    /**
     * 实时心率
     * @param heart_rate 实时心率
     */
    void onEcgCuntResult(int heart_rate);

    /**
     *实时波行数据返回
     * @param ecgData ecg波形数据
     * @param ppgData ppg波形数据
     */
    void onDataResult(long time, List<Integer> ecgData, List<Integer> ppgData);

    /**
     * ppg数据信号状态
     * @param ppg_status ppg信号质量 1=好 or -1=质量差
     */
    void onPPGStatusResult(int ppg_status);

    /**
     * ecg数据信号状态
     * @param ecg_status ecg信号质量 1=好 or -1=质量差
     */
    void onECGStatusResult(int ecg_status);

    /**
     * 设备落座状态
     * @param isDown true="落座" or fals="离坐"
     */
    void onDeviceStatus(boolean isDown);

    /**
     * 获取实时波形数据失败
     * @param result  失败原因
     */
    void onError(String result);

    /**
     * 连接成功
     * @param isNewDevice 设备类型 true：支持双波形 or false：不支持,仅ECG
     */
    void onConnetSucceed(boolean isNewDevice);


}

