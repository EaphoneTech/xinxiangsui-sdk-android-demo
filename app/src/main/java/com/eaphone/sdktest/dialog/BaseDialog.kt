package com.eaphone.sdktest.dialog

import android.app.Dialog
import android.content.Context
import com.eaphone.sdktest.R

/**
 * @ClassName       BaseDialog
 * @Model           todo
 * @Description     使用动画的对话框
 * @Author          chen qi hao
 * @Sign            沉迷学习不能自拔
 * @Date            2019/7/5 10:33
 * @Email           371232886@qq.com
 * @Copyright       xiaoge
 */
open class BaseDialog(context: Context) : Dialog(context, R.style.DialogBlackBgStyle) {
    init {
        window?.attributes?.windowAnimations = R.style.BottomDialogAnimation
    }
}