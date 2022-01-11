package com.eaphone.lib_sdk.http;


public interface IResponseCallback {

    void onFail(EaphoneOkHttpException e);

    void onResponse(String response);
}
