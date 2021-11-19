package com.eaphone.sdktest.dialog

import android.app.Dialog
import android.content.Context
import com.eaphone.sdktest.R


open class BaseDialog(context: Context) : Dialog(context, R.style.DialogBlackBgStyle) {
    init {
        window?.attributes?.windowAnimations = R.style.BottomDialogAnimation
    }
}