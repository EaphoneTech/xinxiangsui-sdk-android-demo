package com.eaphone.sdktest.utils

import android.view.View

/**
 * @ClassName       Ex
 * @Model           todo
 * @Description     扩展方法
 * @Author          he lin hua
 * @Date            2019/6/13 11:01
 */


/**
 * 动态设置view的宽高
 */
fun View.setWidthHeight(w: Int, h: Int) {
    val lp = this.layoutParams
    lp.height = h
    lp.width = w
    this.layoutParams = lp
}
