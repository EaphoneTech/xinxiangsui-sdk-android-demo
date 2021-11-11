package com.eaphone.sdktest.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.eaphone.sdktest.R
import kotlinx.android.synthetic.main.dialog_sigin.view.*

/**
 * @ClassName       SiginBntDialog
 * @Model           todo
 * @Description     操作提示对话框
 * @Author          he lin hua
 */
class SiginBntDialog(context: Context, isCancelable:Boolean, title: String, content: String, bnttext: String, action: (Int) -> Unit) : BaseDialog(context) {

    companion object {
        const val BNT_YES = 1 //确认
    }

    init {
        val v = LayoutInflater.from(context).inflate(R.layout.dialog_sigin, null)
        this.setContentView(v)
        this.window?.setGravity(Gravity.CENTER)
        val lp = this.window?.attributes
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        this.window?.attributes = lp
        v.tv_title.text = title
        v.tv_content.text = content
        v.positive.text = bnttext
        this.setCancelable(isCancelable)

        v.positive.setOnClickListener {
            action.invoke(BNT_YES)
            this.dismiss()
        }
    }

}