package com.eaphone.sdktest.activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.eaphone.lib_sdk.common.EcgReportData
import com.eaphone.lib_sdk.listener.BleScanListener
import com.eaphone.lib_sdk.listener.EcgDataCallBack
import com.eaphone.lib_sdk.listener.InitResultListener
import com.eaphone.lib_sdk.sdk.EaphoneInterface
import com.eaphone.sdktest.R
import com.eaphone.sdktest.adapter.DeviceListAdapter
import com.eaphone.sdktest.dialog.CommonDialog
import com.eaphone.sdktest.dialog.TypeDialog
import com.eaphone.sdktest.utils.MyUtils
import com.eaphone.sdktest.utils.setWidthHeight
import com.xw.repo.refresh.PullToRefreshLayout
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),PullToRefreshLayout.OnRefreshListener, BleScanListener {

    private var mContext : Context? = null
    private var  mAdapter: DeviceListAdapter? = null
    private var mBluetoothAdapter: BluetoothAdapter?= null
    private val BT_OPEN_GPS = 10008
    private val BT_OPEN_REQUIRE_CODE = 10009
    private val ITEM_CODE = 10020

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        pb_loading.visibility = View.INVISIBLE
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(mContext!!, R.color.colorblue))
        BarUtils.setStatusBarLightMode(this, false)
        view_status_bar.setWidthHeight(view_status_bar.width, BarUtils.getStatusBarHeight())
        initView()
        initEvent()
    }

    private fun initView(){
        mAdapter = DeviceListAdapter(mContext, arrayListOf())
        list_view.adapter = mAdapter
        pullToRefreshLayout.loadMoreFinish(false)
        pullToRefreshLayout.setOnRefreshListener(this)
    }

    private fun initData(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        //模拟SDK初始化成功后
        Handler().postDelayed({
            checkOpenBle()
        }, 2000)
        EaphoneInterface.setDebugMode(true)
        test()
    }



    private fun initEvent(){
        list_view.setOnItemClickListener { parent, view, position, id ->
            val item = mAdapter!!.getItem(position) as BluetoothDevice
            val dialog = TypeDialog(mContext!!){
                if(it == TypeDialog.BNT_ECG){
                    val intent = Intent(mContext, EcgDataActivity::class.java)
                    intent.putExtra("item", item)
                    startActivity(intent)
                } else{
                    val intent = Intent(mContext, WifiSetActivity::class.java)
                    intent.putExtra("item", item)
                    startActivityForResult(intent, ITEM_CODE)
                }
            }
            dialog.show()
        }
    }

    private fun test(){
       // EaphoneInterface.init(mContext!!, "6152cb8965ae3256fa67fd13", "e9e04d642b084cd5b4813e3700429f6a", object:InitResultListener{

    }
    private fun test1(){
        EaphoneInterface.init(mContext!!, "6152cb8965ae3256fa67fd13", "e9e04d642b084cd5b4813e3700429f6a", object:InitResultListener{
            override fun onSucceed() {

            }

            override fun onError(result: String?) {

            }



        })
    }

    private fun checkOpenBle(){
        if(!mBluetoothAdapter?.isEnabled!!){
            val dialog = CommonDialog(mContext!!, false, "蓝牙信息", "蓝牙未打开无法扫描到设备，请前往设置打开", "取消", "去设置"){
                if(it == CommonDialog.BNT_YES){
                    val startBt = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(startBt, BT_OPEN_REQUIRE_CODE)
                }
            }
            dialog.show()
        } else{
            checkPermission()
        }
    }

    private fun toGPS(){
        if(MyUtils.isLocServiceEnable(mContext)){
            tv_erro.visibility = View.GONE
            pullToRefreshLayout.visibility = View.VISIBLE
            EaphoneInterface.getBindDevices(mContext, this)
        } else{
            val dialog = CommonDialog(mContext!!, false, "位置信息", "手机定位功能尚未打开，请前往设置打开", "取消", "去设置"){
                if(it == CommonDialog.BNT_YES){
                    val  intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, BT_OPEN_GPS)
                }
            }
            dialog.show()
        }
    }

    private fun checkPermission() {
        PermissionUtils.permission(Manifest.permission.ACCESS_FINE_LOCATION)
            .callback(object : PermissionUtils.SimpleCallback {
                override fun onDenied() {
                    //权限拒绝
                    ToastUtils.showShort("应用定位权限被拒绝，无法获取发现附近设备")
                }

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onGranted() {
                    //权限通过
                    toGPS()
                }
            }).rationale { shouldRequest -> shouldRequest!!.again(true) }
            .request()
    }

    override fun onLoadMore(pullToRefreshLayout: PullToRefreshLayout?) {

    }

    override fun onRefresh(pullToRefreshLayout: PullToRefreshLayout?) {
        EaphoneInterface.getBindDevices(mContext, this)
    }

    override fun onLeScan(device: BluetoothDevice) {
        mAdapter?.addData(device)
    }

    override fun onScanFinished(scanResultList: List<BluetoothDevice>) {
        pb_loading.visibility = View.INVISIBLE
        pullToRefreshLayout.refreshFinish(true)
    }

    override fun onScanStarted() {
        mAdapter?.clearData()
        pb_loading.visibility = View.VISIBLE
        pullToRefreshLayout.refreshFinish(true)
    }

    override fun onError(result: String) {
        ToastUtils.showLong(result)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == BT_OPEN_REQUIRE_CODE){
            if(mBluetoothAdapter?.isEnabled!!){
                checkPermission()
            } else{
                tv_erro.text = "蓝牙开启失败，暂无法使用"
                pullToRefreshLayout.visibility = View.GONE
                tv_erro.visibility = View.VISIBLE
            }
        } else if(requestCode == BT_OPEN_GPS){
            tv_erro.visibility = View.GONE
            pullToRefreshLayout.visibility = View.VISIBLE
            if(MyUtils.isLocServiceEnable(mContext)){
                //开启扫描
                tv_erro.visibility = View.GONE
                pullToRefreshLayout.visibility = View.VISIBLE
                EaphoneInterface.getBindDevices(mContext, this)
            } else {
                tv_erro.text = "未打开定位权限，暂无法使用"
                pullToRefreshLayout.visibility = View.GONE
                tv_erro.visibility = View.VISIBLE
            }
        } else if(requestCode == ITEM_CODE){
//            mAdapter?.clearData()
//            if(!mBluetoothAdapter?.isEnabled!!){
//                val startBt = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                startActivityForResult(startBt, BT_OPEN_REQUIRE_CODE)
//            } else{
//                EaphoneInterface.getBindDevices(mContext, mBleScanListener)
//            }
        }
    }

    private var exitTime = 0L
    override fun onBackPressed() {
        val backTime = System.currentTimeMillis()
        if (backTime - exitTime <= 2000) {
            finish()
        } else {
            ToastUtils.showShort("再按一次退出应用")
            exitTime = backTime

        }
    }


    private var isFist = true
    override fun onResume() {
        super.onResume()
        if(isFist){
            initData()
            isFist = false
        }
    }

}
