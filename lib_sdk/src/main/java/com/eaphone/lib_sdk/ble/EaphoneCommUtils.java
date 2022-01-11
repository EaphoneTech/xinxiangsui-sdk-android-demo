package com.eaphone.lib_sdk.ble;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.location.LocationManager;

import java.text.NumberFormat;

import static android.content.Context.UI_MODE_SERVICE;

/**
 *工具类
 */
public class EaphoneCommUtils {

    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     * @param mContext mContext
     * @return true or fals
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

    static String bytes2HexString(byte[] b){
        return   new String (b);
    }


    static int pinJie2ByteToInt(byte byte1, byte byte2) {
        int result = byte2;
        result = (result << 8) | (0x00FF & byte1);
        return result;
    }
    static int getTemperature(byte[] bytes) {
        return (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) | ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
    }

    /**
     * 设备是否是平板
     * @param context Context
     * @return true or fals
     */
    static boolean isTV(Context context) {
        UiModeManager uiModeManager = (UiModeManager)context.getSystemService(UI_MODE_SERVICE);
        return uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
    }


    /**
     * 将double转为数值，并最多保留num位小数。
     * @param d double
     * @param num 小数个数
     * @return double
     */
    static String double2String(double d, int num) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(num);//保留两位小数
        nf.setGroupingUsed(false);//去掉数值中的千位分隔符

        String temp = nf.format(d);
        if (temp.contains(".")) {
            String s1 = temp.split("\\.")[0];
            String s2 = temp.split("\\.")[1];
            for (int i = s2.length(); i > 0; --i) {
                if (!s2.substring(i - 1, i).equals("0")) {
                    return s1 + "." + s2.substring(0, i);
                }
            }
            return s1;
        }
        return temp;
    }
}
