package com.eaphone.sdktest

import android.app.Application
import com.eaphone.lib_sdk.listener.InitResultListener
import com.eaphone.lib_sdk.sdk.EaphoneInterface


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
      //  EaphoneInterface.init(this, "app_id", "app_secret", object:
        EaphoneInterface.init(this, "61b1e60f3e2b3d5a4b457425", "649a613a884c47dda2b530aeadb95c6f", object:
            InitResultListener {
            override fun onSucceed() {

            }

            override fun onError(result: String?) {

            }
        })
    }

}