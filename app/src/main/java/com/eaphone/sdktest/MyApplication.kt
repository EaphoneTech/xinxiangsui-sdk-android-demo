package com.eaphone.sdktest

import android.app.Application
import android.content.Context
import com.eaphone.lib_sdk.sdk.EaphoneInterface


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
       // EaphoneInterface.init(this, "2802e5ee2c3e4d7e8d960aded9d38c5e", null)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}