package com.eaphone.sdktest

import android.app.Application
import com.eaphone.lib_sdk.listener.InitResultListener
import com.eaphone.lib_sdk.sdk.EaphoneInterface


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        EaphoneInterface.init(this, "app_id", "app_secret", object:
            InitResultListener {
            override fun onSucceed() {

            }

            override fun onError(result: String?) {

            }
        })
    }

}