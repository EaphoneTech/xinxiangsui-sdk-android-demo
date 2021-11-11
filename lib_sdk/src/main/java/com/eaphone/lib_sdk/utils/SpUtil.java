package com.eaphone.lib_sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {

    private static String SP_NAME = "eaphone_sp";
    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return prefs.getString("eaphone_token", "");
    }

    public static void setToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString("eaphone_token", token).apply();
    }

    public static String getAppID(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return prefs.getString("eaphone_app_id", "");
    }

    public static void setAppID(Context context, String app_id) {
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString("eaphone_app_id", app_id).apply();
    }

}

