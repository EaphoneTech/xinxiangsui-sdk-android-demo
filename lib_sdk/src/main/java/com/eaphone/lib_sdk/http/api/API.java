package com.eaphone.lib_sdk.http.api;


import com.eaphone.lib_sdk.utils.Utils;

public class API {

    private static String debug_path = "https://open.xinxiangsui.com.cn/api/";
    private static String relese_path = "https://open.xinxiangsui.com.cn/api/";

    public static String getPath(){
        if(Utils.isAppDebug()){
            return debug_path;
        } else{
            return relese_path;
        }
    }
    static String GET_TOKEN = getPath()+"v1/token";

    static String REPORT_DATA = getPath()+"v1/data/serial/";
}
