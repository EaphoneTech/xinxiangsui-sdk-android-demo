package com.eaphone.lib_sdk.common;


/**
 * SDK返回说明
 */
public class ErrorCode {

    public static final String CODE_INIT_SDK_SUCCESS = "100100";
    public static final String MSG_INIT_SDK_SUCCESS = "初始化SDK成功";
    public static final String CODE_INIT_SDK_FAIL = "100101";
    public static final String MSG_INIT_SDK_FAIL = "SDK未初始化";
    public static final String CODE_ERROR_ANALYZE_SDK_INFO = "100102";
    public static final String MSG_ERROR_ANALYZE_SDK_INFO = "app_key解析失败";
    public static final String CODE_ERROR_INIT_LISTENER_IS_NULL= "100103";
    public static final String MSG_ERROR_INIT_LISTENER_IS_NULL = "InitResultListener is null";
    public static final String CODE_ERROR_APP_ID_IS_NULL= "100104";
    public static final String MSG_ERROR_APP_ID_IS_NULL = "app_id is null";
    public static final String CODE_ERROR_APP_SECRET_IS_NULL= "100105";
    public static final String MSG_ERROR_SECRET_IS_NULL = "app_secret is null";

    public static final String CODE_ERROR_BLE_IS_SHUT= "100200";
    public static final String MSG_ERROR_BLE_IS_SHUT = "蓝牙未打开";
    public static final String CODE_ERROR_GPS_IS_SHUT = "100201";
    public static final String MSG_ERROR_GPS_IS_SHUT = "GPS未打开";
    public static final String CODE_ERROR_NO_PERMISSION_ACCESS_FINE_LOCATION = "100202";
    public static final String MSG_ERROR_NO_PERMISSION_ACCESS_FINE_LOCATION = "没有获取到定位权限 ACCESS_FINE_LOCATION";

    public static final String CODE_ERROR_IS_SCANING = "100008";
    public static final String MSG_ERROR_IS_SCANING = "正在扫描设备中...，请稍后再获取设备列表";

    public static final String CODE_ERROR_DEVICER_IS_NULL= "100010";
    public static final String MSG_ERROR_DEVICER_IS_NULL = "BluetoothDevice为空";
    public static final String CODE_ERROR_WIFI_NAME_IS_NULL= "100011";
    public static final String MSG_ERROR_WIFI_NAME_IS_NULL = "wifi名称为空";
    public static final String CODE_ERROR_WIFI_PASSWORD_IS_NULL= "100012";
    public static final String MSG_ERROR_WIFI_PASSWORD_IS_NULL = "wifi密码为空";
    public static final String CODE_ERROR_CONNNECT_DEVICE_FAIL= "100013";
    public static final String MSG_ERROR_CONNNECT_DEVICE_FAIL = "连接设备失败";
    public static final String CODE_ERROR_DEVICE_IS_DISCONNECT= "100014";
    public static final String MSG_ERROR_DEVICE_IS_DISCONNECT = "设备断开连接";
    public static final String CODE_ERROR_SEND_BAND_FAIL= "100015";
    public static final String MSG_ERROR_SEND_BAND_FAIL = "ble未连接，发送配网请求失败";
    public static final String CODE_ERROR_WIFI_PASSWORD_IS_FAIL= "100016";
    public static final String MSG_ERROR_WIFI_PASSWORD_IS_FAIL = "wifi密码错误,配网失败";
    public static final String CODE_ERROR_BIND_UNKNOWN = "100017";
    public static final String MSG_ERROR_BIND_UNKNOWN = "未知错误,配网失败,错误码：";
    public static final String CODE_ERROR_CONNECT_TIME_OUT = "100018";
    public static final String MSG_ERROR_CONNECT_TIME_OUT = "设备蓝牙连接超时";

    public static final String CODE_ERROR_ECG_REPORT_DATA_IS_NULL = "8004001";
    public static final String MSG_ERROR_ECG_REPORT_DATA_IS_NULL = "数据不存在";
    public static final String CODE_ERROR_ECG_REPORT_DATA_PULL_ING = "8004002";
    public static final String MSG_ERROR_ECG_REPORT_DATA_PULL_ING = "数据上传中";
    public static final String CODE_ERROR_ECG_REPORT_DATA_TIME = "8004003";
    public static final String MSG_ERROR_ECG_REPORT_DATA_DATA_TIME= "数据时长不够";
    public static final String CODE_ERROR_ECG_REPORT_DATA_CHECK_ING = "8004004";
    public static final String MSG_ERROR_ECG_REPORT_DATA_CHECK_ING = "数据分析中";

    public static final String CODE_ERROR_IO_EXCEPTION = "100400";
    public static final String MSG_ERROR_IO_EXCEPTION = "IOException";

}
