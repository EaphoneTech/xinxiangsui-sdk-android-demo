package com.eaphone.sdktest.activity

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.*
import com.eaphone.lib_sdk.listener.EcgDataResultListener
import com.eaphone.lib_sdk.sdk.EaphoneInterface
import com.eaphone.sdktest.R
import com.eaphone.sdktest.dialog.CommonDialog
import com.eaphone.sdktest.dialog.LoadingDialog
import com.eaphone.sdktest.utils.MyUtils
import com.eaphone.sdktest.utils.setWidthHeight
import kotlinx.android.synthetic.main.activity_ecg.*
import kotlinx.android.synthetic.main.titlebar_white.*
import java.util.*

class EcgDataActivity : Activity(), EcgDataResultListener {

    private var mContext: Context? = null
    private var mBluetoothDevice: BluetoothDevice? = null
    //用于显示的数据
    private var showEcgValues = arrayListOf<Int>()
    private var showPpgValues = arrayListOf<Int>()
    private var timeCunt = 10L
    private var maxRow = 300
    private var isFistData = true

    private val mLoadingDialog by lazy {
        LoadingDialog(mContext!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ecg)
        mContext = this
        tv_tab_title.text = "实时波形"
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT)
        BarUtils.setStatusBarLightMode(this, true)
        view_status_bar.setWidthHeight(view_status_bar.width, BarUtils.getStatusBarHeight())
        initData()
        initEvent()
    }

    private fun initData() {
        mBluetoothDevice = intent.getParcelableExtra("item")
        EaphoneInterface.getECGData(mContext, mBluetoothDevice!!, this)
        showLoadingDialog("设备连接中...")
    }

    private fun initEvent() {
        iv_back.setOnClickListener {
            onback()
        }
    }

    private fun showLoadingDialog(msg: String?) {
        if (!TextUtils.isEmpty(msg)) {
            mLoadingDialog.setMessage(msg)
        }
        if(!mLoadingDialog.isShowing){
            mLoadingDialog.show()
        }
    }

    override fun onConnetSucceed(isNewDevice: Boolean) {
        runOnUiThread {
            mLoadingDialog.dismiss()
            val w_px = ScreenUtils.getScreenWidth()
            val padding_w = ConvertUtils.dp2px(20f)
            //每个大格子的宽度
            val w = (w_px-padding_w)/15
            //背景网格高度
            val h = w*6
            val lp = layou_ecg.layoutParams
            lp.height = h
            layou_ecg.layoutParams = lp
            if(isNewDevice){
                layou_ppg.layoutParams = lp
                layou_type.visibility = View.VISIBLE
                layou_ppg.visibility = View.VISIBLE
                timeCunt = 33L
                maxRow = 90
            } else{
                layou_ppg.visibility = View.GONE
                layou_type.visibility = View.GONE
                timeCunt = 10L
                maxRow = 300
            }
            layou_main.visibility = View.VISIBLE
            showTimer()
        }
    }

    override fun onDataResult(time: Long, ecgData: List<Int>?, ppgData: List<Int>?) {
        runOnUiThread {
            tv_time.text = MyUtils.formatSeconds(time)
            if(ecgData != null){
                for(item in ecgData){
                    showEcgValues.add(item)
                }
            }
            if(ppgData != null){
                for(item in ppgData){
                    showPpgValues.add(item)
                }
            }
        }
    }

    override fun onError(result: String?) {
        runOnUiThread {
            mLoadingDialog.dismiss()
            ToastUtils.showLong(result)
        }
    }

    override fun onDeviceStatus(isDown: Boolean) {
        runOnUiThread {
            if(isDown){
                tv_status.text = "离座停止测量"
                if(isFistData){
                    ecgview.reset()
                    ppgview.reset()
                    isFistData = false
                }
            } else{
                layou_error_ecg.visibility = View.GONE
                layou_error_ppg.visibility = View.GONE
                tv_status.text = "落座开始测量"
                showEcgIndex = 0
                showEcgValues = arrayListOf()
                showPpgIndex = 0
                showPpgValues = arrayListOf()
                isFistData = true
            }
        }
    }

    override fun onEcgCuntResult(heart_rate: Int) {
        runOnUiThread {
            tv_ecg.text = "$heart_rate bpm"
            layou_xin.visibility = View.VISIBLE
        }
    }

    override fun onECGStatusResult(ecg_status: Int) {
        runOnUiThread {
            if(ecg_status == 1){
                layou_error_ecg.visibility = View.GONE
            } else if(ecg_status == -1){
                layou_error_ecg.visibility = View.VISIBLE
            }
        }
    }

    override fun onPPGStatusResult(ppg_status: Int) {
        runOnUiThread {
            if(ppg_status == 1){
                layou_error_ppg.visibility = View.GONE
            } else if(ppg_status == -1){
                layou_error_ppg.visibility = View.VISIBLE
            }
        }
    }

    private var showEcgIndex = 0
    private var showPpgIndex = 0
    private var mShowTimer: Timer? = null
    private fun showTimer() {
        ecgview.setData(showEcgValues, ecgview.SHOW_MODEL_DYNAMIC_SCROLL, maxRow)
        ecgview.setLinColor( ContextCompat.getColor(mContext!!,R.color.colorRed))
        ppgview.setData(showPpgValues, ppgview.SHOW_MODEL_DYNAMIC_SCROLL, maxRow)
        ppgview.setLinColor( ContextCompat.getColor(mContext!!,R.color.colorgreen))
        if (mShowTimer == null) {
            mShowTimer = Timer()
            val mTimerTask = object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        if (showEcgValues.isNotEmpty() && showEcgValues.size > showEcgIndex) {
                            val va = showEcgValues[showEcgIndex]
                            ecgview.showLine(va)
                            showEcgIndex++
                        }
                        if (showPpgValues.isNotEmpty() && showPpgValues.size > showPpgIndex) {
                            val va = showPpgValues[showPpgIndex]
                            ppgview.showLine(va)
                            showPpgIndex++
                        }
                    }
                }
            }
            mShowTimer?.schedule(mTimerTask, 100, timeCunt)
        }
    }

    private fun onback() {
        val dialog = CommonDialog(mContext!!, true, "温馨提示", "是否离开实时波形？", "取消", "离开") {
            if (it == CommonDialog.BNT_YES) {
                finish()
            }
        }
        dialog.show()
    }

    override fun onBackPressed() {
       // super.onBackPressed()
        onback()
    }
    override fun onDestroy() {
        super.onDestroy()
        mShowTimer?.cancel()
        mShowTimer = null
        EaphoneInterface.disConnetDevice(mContext!!)
    }

}
