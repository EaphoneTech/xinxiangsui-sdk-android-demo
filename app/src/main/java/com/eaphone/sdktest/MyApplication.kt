package com.eaphone.sdktest

import android.app.Application
import com.eaphone.lib_sdk.listener.InitResultListener
import com.eaphone.lib_sdk.sdk.EaphoneInterface


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //SDK初始化，初始化执行位置可根据业务需求自行更改
        EaphoneInterface.init(this, "app_id", " app_secret", object:
            InitResultListener {
            override fun onSucceed() {

            }

            override fun onError(result: String?) {

            }
        })
    }

}