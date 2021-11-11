package com.eaphone.lib_sdk.ble;

import android.content.Context;
import android.location.LocationManager;

/**
 *工具类
 */
public class EaphoneCommUtils {
    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public static boolean isLocServiceEnable(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager == null){
            return false;
        }
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    /**
     * 根据蓝牙名称获心相随取设备号
     * @param name 设备名
     * @return 设备号
     */
    public static String getSerialNumber(String name){
        if(name == null){
            return null;
        }
        String serialnumber;
        if(getStrCount(name) == 2){
            int indexs2 = name.lastIndexOf(".");
            serialnumber = name.substring(6, indexs2);
        }else if(getStrCount(name) == 3){
            int indexs2 = name.lastIndexOf(".");
            serialnumber = name.substring(9, indexs2);
        } else{
            serialnumber = name.substring(9);
        }
        return serialnumber;
    }


    private static int getStrCount(String name){
        int count = 0;
        for(int i = 0 ; i < name.length(); i++){
            if(name.substring(i, i+1).equals(".")){
                count++;
            }
        }
        return count;
    }

    public static String bytes2HexString(byte[] b){
        return   new String (b);
    }

    /**
     * 将两个字节拼接还原成有符号的整型数据
     数据域中的数据都按小端存储，示例：数据0x1234，则Byte0为0x34,Byte1为0x12
     * @param byte1 byte1
     * @param byte2 byte2
     * @return
     */
    public static int pinJie2ByteToInt(byte byte1, byte byte2) {
        int result = byte2;
        result = (result << 8) | (0x00FF & byte1);
        return result;
    }
}
