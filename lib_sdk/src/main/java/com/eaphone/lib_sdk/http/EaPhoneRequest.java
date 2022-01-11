package com.eaphone.lib_sdk.http;

import android.content.Context;

import java.util.Map;


/**
 * Request 请求接口
 */
public interface EaPhoneRequest {

    void init(Context context);

    void doGet(String url, IResponseCallback callback);

    void doGet(String url, Map<String, String> paramsMap, IResponseCallback callback);

    void doGet(String url, Map<String, String> paramsMap, EaphoneOkhttpOption option, IResponseCallback callback);

    void doPost(String url, Map<String, String> paramsMap, IResponseCallback callback);

    void doPost(String url, Map<String, String> paramsMap, EaphoneOkhttpOption option, IResponseCallback callback);

    void cancel(String tag);
}