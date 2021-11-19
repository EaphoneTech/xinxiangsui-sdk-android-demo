package com.eaphone.sdktest.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.eaphone.sdktest.R
import kotlinx.android.synthetic.main.dialog_type.view.*

class TypeDialog(context: Context, action: (Int) -> Unit) : BaseDialog(context) {

    companion object {
        const val BNT_NET = 0
        const val BNT_ECG = 1
    }

    init {
        val v = LayoutInflater.from(context).inflate(R.layout.dialog_type, null)
        this.setContentView(v)
        this.window?.setGravity(Gravity.CENTER)
        val lp = this.window?.attributes
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        this.window?.attributes = lp
        this.setCancelable(true)
        v.tv_net.setOnClickListener {
            action.invoke(BNT_NET)
            this.dismiss()
        }

        v.tv_ecg.setOnClickListener {
            action.invoke(BNT_ECG)
            this.dismiss()
        }
    }

}