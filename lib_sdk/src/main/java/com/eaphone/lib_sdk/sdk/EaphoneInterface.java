package com.eaphone.lib_sdk.sdk;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import com.eaphone.lib_sdk.ble.BLEManager;
import com.eaphone.lib_sdk.http.api.ApiTools;
import com.eaphone.lib_sdk.listener.*;

/**
 * EaphoneSDK对外暴露接口
 */
public class EaphoneInterface {

    /**
     * debug模式
     */
    private static boolean DEBUG_MODE = true;

    /**
     * 初始化SDK
     * @param context Context
     * @param app_id 开发平台申请的app_id
     * @param app_secret 开发平台申请的app_secret
     * @param listener InitResultListener
     */
    public static void init(Context context, String app_id, String app_secret, InitResultListener listener){
        ApiTools.setToken(app_id, app_secret, listener);
    }

    /**
     *  获取附近设备列表
     * @param context Context
     * @param listener BleScanListener
     */
    public static void getBindDevices(Context context, BleScanListener listener){
        BLEManager.getInstance(context).getDeviceList(listener);
    }

    /**
     * 停止扫描附近设备
     * @param context Context
     */
    public static void cancelScanBle(Context context){
        BLEManager.getInstance(context).scanLeDevice(false);
    }

    /**
     * 设备配网
     * @param context Context
     * @param device 设备
     * @param wifi_name wifi名称
     * @param wifi_password wifi密码
     * @param listener BleBindResultListener
     */
    public static void netBind(Context context, BluetoothDevice device, String wifi_name, String wifi_password, BleBindResultListener listener){
        BLEManager.getInstance(context).bindDevice( device, wifi_name, wifi_password, listener);
    }

    /**
     * 退出设备连接
     * @param context Context
     */
    public static void disConnetDevice(Context context){
        BLEManager.getInstance(context).onColse();
    }

    /**
     *获取设备实时波形数据
     * @param context Context
     * @param device 心相随设备
     * @param listener EcgDataResultListener
     */
    public static void getECGData(Context context, BluetoothDevice device, EcgDataResultListener listener){
        BLEManager.getInstance(context).getEcgData(device, listener);
    }


    public static void getReportData(BluetoothDevice device, EcgDataCallBack callBack){
        ApiTools.getReportData(device, callBack);
    }

    public static boolean getDebugMode() {
        return DEBUG_MODE;
    }

    /**
     *Debug模式，显示日志
     * @param is_show_log true or fals
     */
    public static void setDebugMode(boolean is_show_log) {
        EaphoneInterface.DEBUG_MODE = is_show_log;
    }

}
