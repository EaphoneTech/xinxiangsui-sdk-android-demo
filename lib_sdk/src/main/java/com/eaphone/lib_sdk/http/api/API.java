package com.eaphone.lib_sdk.http.api;



public class API {

    public static String debug_path = "https://test.open.xinxiangsui.com.cn/api/";
    public static String relese_path = "https://open.xinxiangsui.com.cn/api/";

    public static String getPath(){
        return relese_path;
//        if(Utils.isAppDebug()){
//            return debug_path;
//        } else{
//            return relese_path;
//        }
    }
    static String GET_TOKEN1 = getPath()+"v1/token";
    static String GET_TOKEN = getPath()+"v1/thirdparty/token";

    static String REPORT_DATA1 = getPath()+"v1/data/serial/";
    static String REPORT_DATA = getPath()+"v1/device/data/last";
}
