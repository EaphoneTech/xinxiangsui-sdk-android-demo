package com.eaphone.sdktest.activity

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
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

class EcgDataActivity : AppCompatActivity(), EcgDataResultListener {

    private var mContext: Context? = null
    private var mBluetoothDevice: BluetoothDevice? = null
    //用于显示的数据
    private var showEcgValues = arrayListOf<Int>()
    private var showPpgValues = arrayListOf<Int>()
    private var timeCunt = 10L
    private var maxRow = 300
    private var isFistShow = true
    private var longTime = 0L

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
        mLoadingDialog.dismiss()
        if(isFistShow){
            val w_px = ScreenUtils.getScreenWidth()
            val padding_w = ConvertUtils.dp2px(20f)
            //每个大格子的宽度
            val w = (w_px-padding_w)/15
            //背景网格高度
            val h = w*6
            val lp = layou_ecg.layoutParams
            lp.height = h
            layou_ecg.layoutParams = lp
            layou_ppg.layoutParams = lp
            if(isNewDevice){
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
            isFistShow = false
        }
    }

    override fun onDataResult(time: Long, ecgData: List<Int>?, ppg1Data: List<Int>?, ppg2Data: List<Int>?) {
        tv_time.text = "监测时长：${MyUtils.formatSeconds(time)}"
        longTime = time
        if(ecgData != null){
            for(item in ecgData){
                showEcgValues.add(item)
            }
        }
        if(ppg1Data != null){
            for(item in ppg1Data){
                showPpgValues.add(item)
            }
        }
    }

    override fun onThighTemperatureResult(thigh_temperature: String?) {
        if (!TextUtils.isEmpty(thigh_temperature)) {
            tv_thigh_temperature.text = "腿温：$thigh_temperature ℃"
        }
    }

    override fun onError(result: String?) {
        //result请参考ResultCode类
        mLoadingDialog.dismiss()
        CommonDialog(mContext!!, false, "提示", result!!, "退出", "重连") {
            if (it == CommonDialog.BNT_YES) {
                EaphoneInterface.disConnetDevice(mContext!!)
                EaphoneInterface.getECGData(mContext, mBluetoothDevice!!, this)
                showLoadingDialog("设备连接中...")
            } else{
                ActivityUtils.finishToActivity(MainActivity::class.java, false)
            }
        }.show()
    }

    override fun onDeviceStatus(isDown: Boolean) {
        if(isDown){
            tv_status.text = "离座停止测量"
            ToastUtils.showShort("落座")
        } else{
            //确保每次监测时间在35秒以上，否则获取不到分析数据，本demo未做处理
            val intent = Intent(mContext, ReportDataActivity::class.java)
            intent.putExtra("mBluetoothDevice", mBluetoothDevice)
            intent.putExtra("long_time", longTime)
            startActivityForResult(intent, 10001)
            layou_error_ecg.visibility = View.GONE
            layou_error_ppg.visibility = View.GONE
            tv_status.text = "落座开始测量"
            showEcgIndex = 0
            showEcgValues = arrayListOf()
            showPpgIndex = 0
            showPpgValues = arrayListOf()
            ecgview.reset()
            ppgview.reset()
            tv_ecg.text = "心率：--"
            tv_thigh_temperature.text = "腿温：--"
            tv_time.text = "监测时长：--"
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10001) {
            tv_ecg.text = "心率：--"
            tv_thigh_temperature.text = "腿温：--"
            tv_time.text = "监测时长：--"
        }
    }

    override fun onEcgCuntResult(heart_rate: Int) {
        tv_ecg.text = "心率：$heart_rate bpm"
    }

    override fun onECGStatusResult(ecg_status: Int) {
        if(ecg_status == 1){
            layou_error_ecg.visibility = View.GONE
        } else if(ecg_status == -1){
            layou_error_ecg.visibility = View.VISIBLE
        }
    }

    override fun onPPGStatusResult(ppg_status: Int) {
        if(ppg_status == 1){
            layou_error_ppg.visibility = View.GONE
        } else if(ppg_status == -1){
            layou_error_ppg.visibility = View.VISIBLE
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
