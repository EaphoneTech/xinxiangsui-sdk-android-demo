package com.eaphone.sdktest.activity

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.blankj.utilcode.util.*
import com.eaphone.lib_sdk.sdk.EaphoneInterface
import com.eaphone.lib_sdk.listener.BleBindResultListener
import com.eaphone.sdktest.R
import com.eaphone.sdktest.dialog.CommonDialog
import com.eaphone.sdktest.dialog.LoadingDialog
import com.eaphone.sdktest.dialog.SiginBntDialog
import com.eaphone.sdktest.utils.WifiUitils
import com.eaphone.sdktest.utils.MyUtils
import com.eaphone.sdktest.utils.setWidthHeight
import kotlinx.android.synthetic.main.activity_wifi_set.*
import kotlinx.android.synthetic.main.titlebar_white.*


class WifiSetActivity : AppCompatActivity(), BleBindResultListener {


    private var is5G = false
    private var mContext: Context? = null
    private var mBluetoothDevice: BluetoothDevice? = null

    private val mLoadingDialog by lazy {
        LoadingDialog(mContext!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_set)
        mContext = this
        tv_tab_title.text = "配置Wi-Fi"
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT)
        BarUtils.setStatusBarLightMode(this, true)
        view_status_bar.setWidthHeight(view_status_bar.width, BarUtils.getStatusBarHeight())
        initData()
        initEvent()
    }

    private fun initData() {
        mBluetoothDevice = intent.getParcelableExtra("item")
        checkPermission()
    }

    private fun initEvent() {
        iv_back.setOnClickListener {
            finish()
        }
        bnt_bind.setOnClickListener {
            connet()
        }
        cb_wifi.setOnClickListener {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        }
        cb_pass.setOnCheckedChangeListener { _, b ->
            if (b) {
                edtApPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                edtApPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            //为了让光标一直显示在最后
            if (!TextUtils.isEmpty(edtApPassword.text)) {
                val content = edtApPassword.text.toString()
                edtApPassword.setSelection(content.length)
            }
        }
    }

    private fun showLoadingDialog(msg: String?) {
        if (!TextUtils.isEmpty(msg)) {
            mLoadingDialog.setMessage(msg)
        }
        mLoadingDialog.show()
    }

    private fun connet() {
        if (is5G) run {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        } else {
            if (!NetworkUtils.getWifiEnabled() || !NetworkUtils.isWifiConnected()) { //wifi已打开并可用
                ToastUtils.showShort("请连接wifi")
                return
            }
            val apSsid = tvApSssidConnected.text.toString()
            if (TextUtils.isEmpty(apSsid)) {
                ToastUtils.showShort("请连接wifi")
                return
            }
            val apPassword = edtApPassword.text.toString()
            if (TextUtils.isEmpty(apPassword)) {
                ToastUtils.showShort("请输入wifi密码")
                return
            }
            EaphoneInterface.netBind(mContext, mBluetoothDevice, apSsid, apPassword, this)
        }
    }

    private fun checkPermission() {
        PermissionUtils.permission(Manifest.permission.ACCESS_FINE_LOCATION)
            .callback(object : PermissionUtils.SimpleCallback {
                override fun onDenied() {
                    //权限拒绝
                    ToastUtils.showShort("应用定位权限被拒绝，无法获取WiFi信息")
                }

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onGranted() {
                    //权限通过
                    if (MyUtils.isLocServiceEnable(mContext)) {
                        edtApPassword.requestFocus()
                    } else {
                        ToastUtils.showShort("手机定位功能尚未打开，请前往设置打开")
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }
                }
            }).rationale { shouldRequest -> shouldRequest!!.again(true) }
            .request()
    }

    override fun onBindError(result: String?) {
        EaphoneInterface.disConnetDevice(mContext!!)
        runOnUiThread {
            mLoadingDialog.dismiss()
            val errorDialog = CommonDialog(mContext!!, false, "提示", "配网失败,原因：$result", "取消", "重试") {
                if(it == CommonDialog.BNT_YES){
                    connet()
                }
            }
            errorDialog.show()
        }
    }

    override fun onBindSucceed() {
        runOnUiThread {
            mLoadingDialog.dismiss()
            val succeedDialog = SiginBntDialog(mContext!!, false, "提示", "配网成功", "确定") {
                finish()
            }
            succeedDialog.show()
        }
    }

    override fun onBindStarted() {
        showLoadingDialog("正在配网...")
    }

    override fun onResume() {
        super.onResume()
        val apSsid = WifiUitils.getWifiConnectedSsid(mContext)
        if (apSsid != null) {
            tvApSssidConnected.text = apSsid
            if (WifiUitils.is5GHz(mContext)) {
                if (WifiUitils.is5Gor4G(mContext, apSsid)) {
                    is5G = false
                    edtApPassword.hint = "请输入Wi-Fi密码"
                    edtApPassword.isEnabled = true
                    bnt_bind.text = "开始配网"
                } else {
                    is5G = true
                    edtApPassword.hint = "当前设备不支持5GHz网络"
                    edtApPassword.isEnabled = false
                    bnt_bind.text = "连接其他Wi-Fi"
                }
            } else {
                is5G = false
                edtApPassword.hint = "请输入Wi-Fi密码"
                edtApPassword.isEnabled = true
                bnt_bind.text = "开始配网"
            }
        } else {
            tvApSssidConnected.text = ""
        }
        var isApSsidEnble = true
        if (TextUtils.equals(apSsid, "<unknown ssid>") || TextUtils.isEmpty(apSsid)) {
            isApSsidEnble = false
        }
        bnt_bind.isEnabled = isApSsidEnble
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                val imm =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v?.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // Return whether touch the view.
    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && (v is EditText)) {
            val l = intArrayOf(0,0)
            v.getLocationInWindow(l)
            val left = l [0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        EaphoneInterface.disConnetDevice(mContext!!)
    }

}
