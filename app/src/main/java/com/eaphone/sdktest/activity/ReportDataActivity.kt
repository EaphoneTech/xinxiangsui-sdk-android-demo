package com.eaphone.sdktest.activity

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.blankj.utilcode.util.*
import com.eaphone.lib_sdk.common.EcgReportData
import com.eaphone.lib_sdk.common.ErrorCode
import com.eaphone.lib_sdk.listener.EcgDataCallBack
import com.eaphone.lib_sdk.sdk.EaphoneInterface
import com.eaphone.sdktest.R
import com.eaphone.sdktest.utils.MyUtils
import com.eaphone.sdktest.utils.setWidthHeight
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.titlebar_white.*

class ReportDataActivity : AppCompatActivity(), EcgDataCallBack {

    private var mContext: Context? = null
    private var mBluetoothDevice: BluetoothDevice? = null
    private val MAX_GET_COUNT = 15
    private var GET_COUNT = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        mContext = this
        tv_tab_title.text = "数据分析"
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT)
        BarUtils.setStatusBarLightMode(this, true)
        view_status_bar.setWidthHeight(view_status_bar.width, BarUtils.getStatusBarHeight())
        initData()
        initEvent()
        setResult(Activity.RESULT_OK)
    }

    private fun initData() {
        mBluetoothDevice = intent.getParcelableExtra("mBluetoothDevice")
        EaphoneInterface.getReportData(mBluetoothDevice, this)
        layou_loding.visibility = View.VISIBLE
    }

    private fun initEvent() {
        iv_back.setOnClickListener {
            finish()
        }
    }

    override fun onSucceed(data: EcgReportData?) {
        if(data != null){
            LogUtils.e(data.toString())
            layou_loding.visibility = View.GONE
            setViewData(data)
        }
    }

    override fun onError(errcode:String?, message:String?) {
        if(errcode == ErrorCode.CODE_ERROR_ECG_REPORT_DATA_PULL_ING || errcode == ErrorCode.CODE_ERROR_ECG_REPORT_DATA_CHECK_ING){
            if(GET_COUNT < MAX_GET_COUNT){
                Handler().postDelayed({
                    EaphoneInterface.getReportData(mBluetoothDevice, this)
                }, 2*1000)
                GET_COUNT++
            } else{
                pb_loading.visibility = View.INVISIBLE
                tv_loding.text = "抱歉！此次测量暂未分析出数据报告。"
                startRunTimer()
            }
        } else{
            pb_loading.visibility = View.INVISIBLE
            tv_loding.text = "抱歉！此次测量暂未分析出数据报告。异常原因：($message)"
            startRunTimer()
        }
    }

    private fun setViewData(data:EcgReportData){
        if(data.heart_rate != 0){
            tv_heart_rate.text = "${data.heart_rate}dpm"
        } else{
            tv_heart_rate.text = "--"
        }
        if(data.heart_rate != 0){
            tv_heart_rate_max.text = "${data.heart_rate_max}dpm"
        } else{
            tv_heart_rate_max.text = "--"
        }
        if(data.heart_rate_min != 0){
            tv_heart_rate_min.text = "${data.heart_rate_min}dpm"
        } else{
            tv_heart_rate_min.text = "--"
        }
        if(data.leg_temperature != 0.0){
            tv_leg_temperature.text = "${data.leg_temperature}℃"
        } else{
            tv_leg_temperature.text = "--"
        }
        if(data.respiration != 0){
            tv_respiration.text = "${data.respiration}dpm"
        } else{
            tv_respiration.text = "--"
        }
        if(data.duration != 0L){
            tv_duration.text = MyUtils.formatSeconds(data.duration)
        } else{
            tv_heart_rate.text = "--"
        }
        if(data.heart_rate != 0){
            tv_heart_beats.text = "${data.heart_rate}次"
        } else{
            tv_heart_beats.text = "--"
        }
    }




    private var timer: CountDownTimer? = null
    private fun startRunTimer(){
        tv_time_down.visibility = View.VISIBLE
        timer = object : CountDownTimer((6 * 1000 - 1).toLong(), 1000) {
            override fun onFinish() {
                finish()
            }

            override fun onTick(millisUntilFinished: Long) {
                //（10秒）后将退出
                tv_time_down.text = "${millisUntilFinished / 1000}秒后将退出本次测量"
            }
        }.start()
    }



    override fun onDestroy() {
        super.onDestroy()
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

}
