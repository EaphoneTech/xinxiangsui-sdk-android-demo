package com.eaphone.lib_sdk.utils;

import android.util.Log;
import com.eaphone.lib_sdk.sdk.EaphoneInterface;

public class ELog {

    private static String TAG = "ELog";

    public static void d(String str){
        if(EaphoneInterface.getDebugMode()){
            Log.d(TAG, str);
        }
    }

    public static void e(String str){
        if(EaphoneInterface.getDebugMode()){
            Log.e(TAG, str);
        }
    }
}
