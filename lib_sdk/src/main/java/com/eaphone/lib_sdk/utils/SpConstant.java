package com.eaphone.lib_sdk.utils;

public class SpConstant {
    public static final String SP_NAME = "eaphone_sp";
    public static final String TOKEN = "eaphone_sdk_token";
    private static final String APP_ID = "eaphone_sdk_app_id";
    private static final String APP_SECRET = "eaphone_sdk_app_secret";
    private static final String IS_LOGIN = "eaphone_sdk_is_login";

    public static String getToken() {
        return SPUtils.getInstance(SP_NAME).getString(TOKEN);
    }

    public static void setToken(String token) {
        SPUtils.getInstance(SP_NAME).put(TOKEN, token);
    }

    public static String getAppID() {
        return SPUtils.getInstance(SP_NAME).getString(APP_ID);
    }

    public static void setAppID(String app_id) {
        SPUtils.getInstance(SP_NAME).put(APP_SECRET, app_id);
    }
    public static String getSecret() {
        return SPUtils.getInstance(SP_NAME).getString(APP_ID);
    }

    public static void setSecret(String secret) {
        SPUtils.getInstance(SP_NAME).put(APP_SECRET, secret);
    }

    public static boolean isLogin() {
        return SPUtils.getInstance(SP_NAME).getBoolean(IS_LOGIN);
    }

    public static void setLogin(boolean is) {
        SPUtils.getInstance(SP_NAME).put(IS_LOGIN, is);
    }
}
