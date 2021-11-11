package com.eaphone.sdktest.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.eaphone.sdktest.R
import kotlinx.android.synthetic.main.dialog_common.view.*

/**
 * @ClassName       CommonDialog
 * @Model           todo
 * @Description     操作提示对话框
 * @Author          he lin hua
 */
class CommonDialog(context: Context, isCancelable:Boolean, title: String, content: String, textLeft: String, textRight: String, action: (Int) -> Unit) : BaseDialog(context) {

    companion object {
        const val BNT_YES = 1 //确认
        const val BNT_NO = 0 //取消
    }

    init {
        val v = LayoutInflater.from(context).inflate(R.layout.dialog_common, null)
        this.setContentView(v)
        this.window?.setGravity(Gravity.CENTER)
        val lp = this.window?.attributes
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        this.window?.attributes = lp
        v.tv_title.text = title
        v.tv_content.text = content
        v.negtive.text = textLeft
        v.positive.text = textRight
        this.setCancelable(isCancelable)
        v.negtive.setOnClickListener {
            action.invoke(BNT_NO)
            this.dismiss()
        }

        v.positive.setOnClickListener {
            action.invoke(BNT_YES)
            this.dismiss()
        }
    }

}