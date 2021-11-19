package com.eaphone.sdktest.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.eaphone.sdktest.R

class LoadingDialog(context: Context) : Dialog(context) {

    private var tvMsg: TextView? = null

    private var ivLoading: ImageView? = null

    init {
        val v = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
        this.setContentView(v)
        this.window?.setGravity(Gravity.CENTER)
        this.window?.setBackgroundDrawable(BitmapDrawable())
        this.setCanceledOnTouchOutside(false)
        tvMsg = v.findViewById(R.id.tv_msg)
        ivLoading = v.findViewById(R.id.iv_loading)
    }

    private val mAnimationUtils by lazy {
        AnimationUtils.loadAnimation(context, R.anim.rotate)
    }

    override fun show() {
        super.show()
        ivLoading?.startAnimation(mAnimationUtils)
    }

    override fun dismiss() {
        super.dismiss()
        ivLoading?.clearAnimation()
    }

    /**
     * 加载提示语
     */
    fun setMessage(msg: String?) {
        if (msg == null)
            return
        tvMsg?.text = msg
    }
}