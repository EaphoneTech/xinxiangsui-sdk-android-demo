package com.eaphone.sdktest.activity

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.*
import com.eaphone.lib_sdk.listener.EcgDataResultListener
import com.eaphone.lib_sdk.sdk.EaphoneInterface
import com.eaphone.sdktest.R
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
    }

    private fun initEvent() {
        iv_back.setOnClickListener {
            finish()
        }
    }

    override fun onConnetSucceed(isNewDevice: Boolean) {
        if(isNewDevice){
            layou_type.visibility = View.VISIBLE
            layou_ppg.visibility = View.VISIBLE
            timeCunt = 33L
            maxRow = 90
            showPpgTimer()
        } else{
            timeCunt = 10L
            maxRow = 300
            val lp = layou_ecg.layoutParams
            lp.height = MyUtils.toPx(180f)
            layou_ecg.layoutParams = lp
            layou_ppg.visibility = View.GONE
            layou_type.visibility = View.GONE
        }
        showEcgTimer()
    }

    override fun onDataResult(time: Long, ecgData: List<Int>?, ppgData: List<Int>?) {
        runOnUiThread {
            tv_time.text = MyUtils.formatSeconds(time)
        }
        if(ecgData != null){
            showEcgValues.addAll(ecgData)
        }
        if(ppgData != null){
            showPpgValues.addAll(ppgData)
        }
    }

    override fun onError(result: String?) {
        runOnUiThread {
            ToastUtils.showLong(result)
        }
    }

    override fun onDeviceStatus(isDown: Boolean) {
        runOnUiThread {
            if(isDown){
                tv_status.text = "离座停止测量"
            } else{
                layou_error_ecg.visibility = View.GONE
                layou_error_ppg.visibility = View.GONE
                tv_status.text = "落座开始测量"
                showEcgIndex = 0
                showEcgValues = arrayListOf()
                showPpgIndex = 0
                showPpgValues = arrayListOf()
                ecgview.reset()
                ppgview.reset()
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
    private var mShowEcgTimer: Timer? = null
    private fun showEcgTimer() {
        ecgview.setData(showEcgValues, ecgview.SHOW_MODEL_DYNAMIC_SCROLL, maxRow)
        if (mShowEcgTimer == null) {
            mShowEcgTimer = Timer()
            val mTimerTask = object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        if (showEcgValues.isNotEmpty() && showEcgValues.size > showEcgIndex) {
                            val va = showEcgValues[showEcgIndex]
                            ecgview.showLine(va)
                            showEcgIndex++
                        }
                    }
                }
            }

            mShowEcgTimer?.schedule(mTimerTask, 200, timeCunt)
        }
    }

    private var showPpgIndex = 0
    private var mShowPpgTimer: Timer? = null
    private fun showPpgTimer() {
        ppgview.setData(showPpgValues, ppgview.SHOW_MODEL_DYNAMIC_SCROLL, maxRow)

        if (mShowPpgTimer == null) {
            mShowPpgTimer = Timer()
            val mTimerTask = object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        if (showPpgValues.isNotEmpty() && showPpgValues.size > showPpgIndex) {
                            val va = showPpgValues[showPpgIndex]
                            ppgview.showLine(va)
                            showPpgIndex++
                        }
                    }
                }
            }
            mShowPpgTimer?.schedule(mTimerTask, 200, timeCunt)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mShowEcgTimer?.cancel()
        mShowEcgTimer = null
        mShowPpgTimer?.cancel()
        mShowPpgTimer = null
        EaphoneInterface.disConnetDevice(mContext!!)
    }

}
