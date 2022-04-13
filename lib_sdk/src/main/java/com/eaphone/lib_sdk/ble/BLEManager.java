package com.eaphone.lib_sdk.ble;

import android.bluetooth.*;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import com.eaphone.lib_sdk.common.ErrorCode;
import com.eaphone.lib_sdk.listener.BleBindResultListener;
import com.eaphone.lib_sdk.listener.BleScanListener;
import com.eaphone.lib_sdk.listener.EcgDataResultListener;
import com.eaphone.lib_sdk.utils.ELog;
import com.eaphone.lib_sdk.utils.SpConstant;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;



/**
 * BLEManager
 */
public class BLEManager {

    private Context mContext;
    private volatile static BLEManager bleManager = null;
    private MyBluetoothGattCallback mGattCallback = null;
    private BluetoothGatt mBluetoothGatt = null;
    private GattConnector smGattConnector = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private ScanCallback mScanCallback = null;
    private BleScanListener mBleScanListener = null;

    // UUID ---connect
    private final String EA_UUID_DEVICE_PERMISSION = "0103";
    private final String EA_UUID_WIFI_STATE = "0201";
    private final String EA_UUID_WIFI_SSID = "0202";
    private final String EA_UUID_WIFI_PASSWORD = "0204";
    // UUID ---ecg
    private final String UUID_TEST_RESULT = "0803";
    private final String UUID_TEST_DATA = "0802";
    private final String UUID_TEST_DATA_NEW = "0812";

    private final String UUID_TEST_DATA_TEMPERATURE = "0410";

    private LinkedList<GattCharaValue> mGattWriteQueue = null;
    private LinkedList<String> mGattReadQueue = null;

    private BleBindResultListener mBleBindResultListener = null;
   // private BleConnectResultListener mBleConnectResultListener = null;
    private EcgDataResultListener mEcgDataResultListener = null;
    private int reSendNum = 0;//定义重发次数
    private final int maxSendNum = 3;//最多重发次数
    private int reConnectionNum = 0;//定义重连次数
    private final int maxConnectionNum = 3;//最多重连次数
    private final int TIME_OUT_SECONDS = 30;//设备蓝牙连接超时时长
    private boolean isScaning = false;//正在扫描
    private boolean isEcgMoudel = false;//ECG模式
    private boolean isSendConnet = false; //发送成功才开始接受通知
    private boolean isConnect = false;
    private boolean isNewDevice = false;
    private boolean isDown = false;//是否落座
    private String mSerial_number = null;
    private String mWifi_name = null;
    private String mWifi_password = null;
    private CountDownTimer downTimer = null;
    private List<BluetoothDevice> mBluetoothDeviceList;
    private  Handler mHandler;
    
    /**
     * 配网模式
     * true传serial_number到SDK扫描加配网
     * fals传device到SDK配网
     */
    private boolean isScan = true;


    private BLEManager(Context context) {
        mContext = context.getApplicationContext();
        mHandler = new Handler();
        innit();
    }

    public static BLEManager getInstance(Context context) {
        if (bleManager == null) {
            synchronized (BLEManager.class) {
                if (bleManager == null) {
                    bleManager = new BLEManager(context);
                }
            }
        }
        return bleManager;
    }

    /**
     * 初始化蓝牙
     */
    private void innit() {
        ELog.d("innit");
        mGattWriteQueue = new LinkedList<>();
        mGattReadQueue = new LinkedList<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initCallback();
        if (!mBluetoothAdapter.isEnabled()) {
            ELog.e(ErrorCode.MSG_ERROR_BLE_IS_SHUT);
        }
    }

    /**
     * 获取附近设备列表
     * @param listener BleScanListener
     */
    public void getDeviceList(BleScanListener listener) {
        if (listener == null) {
            ELog.e("BleScanListener is null");
            return;
        }
        if(!SpConstant.isLogin()){
            ELog.e(ErrorCode.MSG_INIT_SDK_FAIL);
            listener.onError(ErrorCode.MSG_INIT_SDK_FAIL);
            return;
        }
        mBleScanListener = listener;
        isScan = true;
        scanLeDevice(true);
    }

    /**
     * 开始配网
     * @param serial_number 设备序列号
     * @param wifi_name wifi名称
     * @param wifi_password wifi密码
     */
    public void bindDevice(String serial_number, String wifi_name, String wifi_password) {
        ELog.d("bindDevice");
        isScan = false;
        mSerial_number = serial_number;
        mWifi_name = wifi_name;
        mWifi_password = wifi_password;
    }

    /**
     * 开始配网
     * @param device 设备
     * @param wifi_name wifi名称
     * @param wifi_password wifi密码
     * @param listener BleBindResultListener
     */
    public void bindDevice(BluetoothDevice device, String wifi_name, String wifi_password, BleBindResultListener listener) {
        ELog.d("bindDevice()");
        if (listener == null) {
            ELog.e("listener is null");
            return;
        }
        if(!SpConstant.isLogin()){
            ELog.e(ErrorCode.MSG_INIT_SDK_FAIL);
            listener.onBindError(ErrorCode.MSG_INIT_SDK_FAIL);
            return;
        }
        if (device == null) {
            ELog.e("device is null");
            listener.onBindError(ErrorCode.MSG_ERROR_DEVICER_IS_NULL);
            return;
        }
        if (TextUtils.isEmpty(wifi_name)) {
            ELog.e("wifi_name is null");
            listener.onBindError(ErrorCode.MSG_ERROR_WIFI_NAME_IS_NULL);
            return;
        }
        if (TextUtils.isEmpty(wifi_password)) {
            ELog.e("wifi_password is null");
            listener.onBindError(ErrorCode.MSG_ERROR_WIFI_PASSWORD_IS_NULL);
            return;
        }
        isEcgMoudel = false;
        isScan = false;
        mBleBindResultListener = listener;
        mSerial_number = EaphoneCommUtils.getSerialNumber(device.getName());
        mWifi_name = wifi_name;
        mWifi_password = wifi_password;
        if(isConnect){
            send(false);
        } else{
            connetBluetoothDevice(device);
        }
    }


    /**
     * 获取ECG数据
     * @param device 设备
     * @param listener EcgDataResultListener
     */
    public void getEcgData(BluetoothDevice device, EcgDataResultListener listener) {
        ELog.d("调用获取实时波形方法...");
        if (listener == null) {
            ELog.e("listener is null");
            return;
        }
        if(!SpConstant.isLogin()){
            ELog.e(ErrorCode.MSG_INIT_SDK_FAIL);
            listener.onError(ErrorCode.MSG_INIT_SDK_FAIL);
            return;
        }
        if (device == null) {
            ELog.e("device is null");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            ELog.e(ErrorCode.MSG_ERROR_BLE_IS_SHUT);
            listener.onError(ErrorCode.MSG_ERROR_BLE_IS_SHUT);
            return;
        }
        isNewDevice = false;
        isEcgMoudel = true;
        isScan = false;
        mEcgDataResultListener = listener;
        mSerial_number = EaphoneCommUtils.getSerialNumber(device.getName());
        //停止扫描
        scanLeDevice(false);
        timeOutTimer();
        ELog.d("正在连接设备：" + mSerial_number);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        }
    }

    private Timer timer;
    //发送配网数据，开始配网
    private void send(boolean isReSend) {
        ELog.d("开始配网...");
        if (!isConnect) {
            ELog.e(ErrorCode.MSG_ERROR_SEND_BAND_FAIL);
            if(mBleBindResultListener != null){
                mBleBindResultListener.onBindError(ErrorCode.MSG_ERROR_SEND_BAND_FAIL);
            }
            return;
        }
        setEphoneData(mSerial_number, mWifi_name, mWifi_password, isReSend);
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (smGattConnector != null && mGattWriteQueue != null && mGattWriteQueue.size() > 0) {
                    if (smGattConnector.appWrite(mGattWriteQueue.get(0).getUuid(), mGattWriteQueue.get(0).getValue())) {
                        if(mGattWriteQueue.get(0).getUuid().equals(EA_UUID_WIFI_STATE)){
                            ELog.d("isSendConnet = true");
                            isSendConnet = true;
                        }
                        mGattWriteQueue.remove(0);
                    }
                }

                if (smGattConnector != null && mGattReadQueue != null && mGattReadQueue.size() > 0) {
                    if (smGattConnector.appRead(mGattReadQueue.get(0))) {
                        mGattReadQueue.remove(0);
                    }
                }
            }

        }, 500, 1000);

    }

    private void setEphoneData(String serial_number, String wifi_name, String wifi_password, boolean isReSend) {
        mGattReadQueue.clear();
        if(!isReSend){
            mGattWriteQueue.clear();
            String primisson = "permission.app." + serial_number;
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            byte[] bytes = md5.digest(primisson.getBytes());
            mGattWriteQueue.add(new GattCharaValue(EA_UUID_DEVICE_PERMISSION, bytes));
            mGattWriteQueue.add(new GattCharaValue(EA_UUID_WIFI_SSID, wifi_name.getBytes()));
            mGattWriteQueue.add(new GattCharaValue(EA_UUID_WIFI_PASSWORD, wifi_password.getBytes()));
            mGattWriteQueue.add(new GattCharaValue(EA_UUID_WIFI_STATE, "connect".getBytes()));
        }
        mGattReadQueue.add(EA_UUID_DEVICE_PERMISSION);
        mGattReadQueue.add(EA_UUID_WIFI_STATE);
    }

    private boolean isFistData = true;
    private void initCallback() {
        ELog.d("initCallback");
        smGattConnector = new GattConnector() {

            @Override
            public void onUpdate(String uuid, byte[] value) {
                switch (uuid) {
                    case EA_UUID_WIFI_STATE:
                        String state = new String(value);
                        ELog.d("连接状态: " + state);
                        switch (state) {
                            case "connected":
                                if(mBleBindResultListener!= null){
                                    mHandler.post(() -> mBleBindResultListener.onBindSucceed());
                                }
                                disconnet();
                                break;
                            case "disconnected":
                                reSend("400");
                                break;
                            case "disconnected:410":
                                reSend("410");
                                break;
                            default:
                                if(state.contains("disconnected:")){
                                    int l = state.lastIndexOf(":");
                                    String code = state.substring(l+1);
                                    reSend(code);
                                }
                                break;
                        }
                        break;
                    case UUID_TEST_DATA_TEMPERATURE:
                        int t = EaphoneCommUtils.getTemperature(value);
                        if(t!=0 && t!=-255){ //-255是读取失败的意思
                            double t1 = t*0.01;
                            String tt = EaphoneCommUtils.double2String(t1, 1);
                            ELog.d("腿温:"+ tt);
                            if(mEcgDataResultListener!= null){
                                mHandler.post(() -> mEcgDataResultListener.onThighTemperatureResult(tt));
                            }
                        }
                        break;
                    case UUID_TEST_DATA:
                        int status = EaphoneCommUtils.pinJie2ByteToInt(value[0], value[1]);
                        if (status == -1) { //离坐信号
                            ELog.d("设备离坐");
                            isDown = false;
                            if(mEcgDataResultListener!= null){
                                mHandler.post(() -> mEcgDataResultListener.onDeviceStatus(false));
                            }
                            isFistData = true;
                        }else{//数据信号
                            isDown = true;
                            if(isFistData){
                                ELog.d("设备落坐");
                                if(mEcgDataResultListener!= null){
                                    mHandler.post(() -> mEcgDataResultListener.onDeviceStatus(true));
                                }
                                isFistData = false;
                            } else{
                                ELog.d("波形数据信号："+status);
                            }
                            List<Integer> ecgData = new ArrayList<>();
                            for(int i =2; i< value.length; i++ ){
                                if(i%2 == 0){
                                    int data = EaphoneCommUtils.pinJie2ByteToInt(value[i], value[i+1]);
                                    ecgData.add(data);
                                }
                            }
                            if(mEcgDataResultListener!= null){
                                ELog.d("size ="+ecgData.size());
                                mHandler.post(() -> mEcgDataResultListener.onDataResult(status, ecgData, null, null));
                            }
                        }
                        break;
                    case UUID_TEST_DATA_NEW:
                        int statu = EaphoneCommUtils.pinJie2ByteToInt(value[0], value[1]);
                        if (statu == -1) { //离坐信号
                            ELog.d("设备离坐");
                            isDown = false;
                            if(mEcgDataResultListener!= null){
                                mHandler.post(() ->mEcgDataResultListener.onDeviceStatus(false));
                            }
                            isFistData = true;
                        }else{//数据信号
                            isDown = true;
                            if(isFistData){
                                ELog.d("设备落坐");
                                if(mEcgDataResultListener!= null){
                                    mHandler.post(() -> mEcgDataResultListener.onDeviceStatus(true));
                                }
                                isFistData = false;
                            } else{
                                ELog.d("波形数据信号："+statu);
                            }
                            if(value.length == 182){
                                List<Integer> ecgData = new ArrayList<>();
                                List<Integer> ppgData1 = new ArrayList<>();
                                List<Integer> ppgData2 = new ArrayList<>();
                                for(int i = 2; i < value.length; i++ ){
                                    if(i%2 == 0){
                                        int data = EaphoneCommUtils.pinJie2ByteToInt(value[i], value[i+1]);
                                        if(i < 61){
                                          //  ELog.d("ecg i="+i);
                                            ecgData.add(data);
                                        } else if(i < 121){
                                          //  ELog.d("ppg1 i="+i);
                                            ppgData1.add(data);
                                        } else{
                                         //   ELog.d("ppg2 i="+i);
                                            ppgData2.add(data);
                                        }
                                    }
                                }
                                if(mEcgDataResultListener!= null){
                                    mHandler.post(() -> mEcgDataResultListener.onDataResult(statu, ecgData, ppgData1, ppgData2));
                                }
                            }
                        }
                        break;
                    case UUID_TEST_RESULT:
                        String result = EaphoneCommUtils.bytes2HexString(value);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String n = jsonObject.getString("_n");
                            switch (n) {
                                case "HeartRate":  //心率
                                    ELog.d("HeartRate");
                                    if(result.contains("_v")){
                                        if(mEcgDataResultListener!= null){
                                            int cunt = jsonObject.getInt("_v");
                                            mHandler.post(() -> mEcgDataResultListener.onEcgCuntResult(cunt));
                                        }
                                    }
                                    break;
                                case "Sample":  //ecg信号状态
                                    ELog.d("Sample");
                                    if(result.contains("quality")){
                                        if(mEcgDataResultListener!= null){
                                            int quality = jsonObject.getInt("quality");
                                            mHandler.post(() -> mEcgDataResultListener.onECGStatusResult(quality));
                                        }
                                    }
                                    break;
                                case "all":  //信号状态
                                    if(result.contains("HeartRate")){
                                        int v = jsonObject.getInt("HeartRate");
                                        if(v != 0){
                                            if(mEcgDataResultListener!= null){
                                                ELog.d("ecg_cunt:"+v);
                                                mHandler.post(() -> mEcgDataResultListener.onEcgCuntResult(v));
                                            }
                                        }
                                    }
                                    if(result.contains("Sample_ECG")){
                                        int ecg = jsonObject.getInt("Sample_ECG");
                                        if(ecg != 0){
                                            if(mEcgDataResultListener!= null){
                                                ELog.d("ecg_status:"+ecg);
                                                mHandler.post(() -> mEcgDataResultListener.onECGStatusResult(ecg));
                                            }
                                        }
                                    }
                                    if(result.contains("Sample_PPG")){
                                        int ppg = jsonObject.getInt("Sample_PPG");
                                        if(ppg != 0){
                                            if(mEcgDataResultListener!= null){
                                                ELog.d("ppg_status:"+ppg);
                                                mHandler.post(() -> mEcgDataResultListener.onPPGStatusResult(ppg));
                                            }
                                        }
                                    }
                                    break;
                            }
                            break;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }
        };

        mGattCallback = new MyBluetoothGattCallback(smGattConnector);

        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                if (isScan) {
                    if (dataIsOK(device, mBluetoothDeviceList)) {
                        ELog.d("发现设备：" + EaphoneCommUtils.getSerialNumber(device.getName()));
                        if (mBleScanListener == null) {
                            ELog.e("BleScanListener is null");
                            return;
                        }
                        if (mBluetoothDeviceList == null) {
                            mBluetoothDeviceList = new ArrayList<>();
                            mBluetoothDeviceList.add(device);
                        } else {
                            mBluetoothDeviceList.add(device);
                        }
                        mBleScanListener.onLeScan(device);
                    }
                } else {
                    if (deviceIsOK(device, mSerial_number)) {
                        connetBluetoothDevice(device);
                    }
                }
            }
        };
    }

    private boolean dataIsOK(BluetoothDevice device, List<BluetoothDevice> list) {
        boolean isok = true;
        if (device.getName() != null && device.getName().contains("EPCU3.")) {
            if (list == null) {
                return true;
            } else {
                for (BluetoothDevice item : list) {
                    if (item.getName().equals(device.getName())) {
                        return false;
                    }
                }
            }
        } else {

            isok = false;
        }
        return isok;
    }

    private void reSend(String code) {
        ELog.d("reSend（），code="+code+"，reSendNum="+reSendNum);
        if (code.equals("410")) {
            ELog.e(ErrorCode.MSG_ERROR_WIFI_PASSWORD_IS_FAIL);
            if(mBleBindResultListener != null){
                mHandler.post(() -> mBleBindResultListener.onBindError(ErrorCode.MSG_ERROR_WIFI_PASSWORD_IS_FAIL));
            }
            disconnet();
        }else {
            if (reSendNum < maxSendNum) {
                //重连次数自增
                reSendNum++;
                send(true);
            } else {
                ELog.e("配网失败,错误码："+code);
                if(mBleBindResultListener!= null){
                    mHandler.post(() -> mBleBindResultListener.onBindError(ErrorCode.MSG_ERROR_BIND_UNKNOWN + code));
                }
                reSendNum = 0;
                disconnet();
            }
        }
    }

    private void connetBluetoothDevice(BluetoothDevice device) {
        //停止扫描
        isNewDevice = false;
        if(mBleBindResultListener == null){
            ELog.e("BleBindResultListener null");
            return;
        }
        if (device == null) {
            ELog.e("device is null");
            if(mBleBindResultListener != null){
                mBleBindResultListener.onBindError(ErrorCode.MSG_ERROR_DEVICER_IS_NULL);
            }
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            ELog.e("蓝牙未打开");
            if(mBleBindResultListener != null){
                mBleBindResultListener.onBindError(ErrorCode.MSG_ERROR_BLE_IS_SHUT);
            }
            return;
        }
        scanLeDevice(false);
        ELog.d("正在连接设备:" + device.getName());
        timeOutTimer();
        if(mBleBindResultListener != null){
            mBleBindResultListener.onBindStarted();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        }
    }

    private boolean checkPermission() {
        String permissionName = "android.permission.ACCESS_FINE_LOCATION";
        int perm = mContext.checkCallingOrSelfPermission(permissionName);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    public void scanLeDevice(Boolean enable) {
        if(enable){
            ELog.d("开始扫描附近设备...");
        } else{
            if(isScaning){
                ELog.d("停止扫描附近设备");
            }
        }
        if (!mBluetoothAdapter.isEnabled()) {
            ELog.e("蓝牙未打开");
            if (mBleScanListener != null) {
                mBleScanListener.onError(ErrorCode.MSG_ERROR_BLE_IS_SHUT);
            }
            return;
        }
//        if(!EaphoneCommUtils.isTV(mContext)){
//
//        }
        if (!checkPermission()) {
            ELog.e("未获取到权限:android.permission.ACCESS_FINE_LOCATION");
//            if (mBleScanListener != null) {
//                mBleScanListener.onError(ErrorCode.MSG_ERROR_NO_PERMISSION_ACCESS_FINE_LOCATION);
//            }
//            return;
        }
        if (!EaphoneCommUtils.isLocServiceEnable(mContext)) {
            ELog.e("GPS未打开");
//            if (mBleScanListener != null) {
//                mBleScanListener.onError(ErrorCode.MSG_ERROR_GPS_IS_SHUT);
//            }
//            return;
        }

        if (mScanCallback == null) {
            ELog.e("mScanCallback null");
            if (mBleScanListener != null) {
                mBleScanListener.onError("ScanCallback == null");
            }
            return;
        }
        if (enable) {
            mHandler.postDelayed(() -> {
                if(isScaning){
                    BLEManager.this.scanLeDevice(false);
                }
            }, 10000);
            if (isScaning) {
                ELog.e("正在扫描设备中...，请稍后再获取设备列表");
                if (mBleScanListener != null) {
                    mBleScanListener.onError(ErrorCode.MSG_ERROR_IS_SCANING);
                }
            } else {
                isScaning = true;
                if (mBluetoothDeviceList != null) {
                    mBluetoothDeviceList.clear();
                }
                mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
                if (mBleScanListener != null) {
                    mBleScanListener.onScanStarted();
                }
            }
        } else {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
            if (mBleScanListener != null && isScaning) {
                if(mBluetoothDeviceList == null){
                    mBluetoothDeviceList = new ArrayList<>();
                }
                mBleScanListener.onScanFinished(mBluetoothDeviceList);
            }
            isScaning = false;
        }
    }

    private boolean deviceIsOK(BluetoothDevice device, String serial_number) {
        boolean isok = false;
        if (device.getName() != null && device.getName().contains(serial_number)) {
            isok = true;
        }
        return isok;
    }

    public class MyBluetoothGattCallback extends BluetoothGattCallback {

        private GattConnector smGattConnector;
        private HashMap<String, BluetoothGattCharacteristic> characteristicMap = new HashMap<>();

        MyBluetoothGattCallback(GattConnector smGattConnector) {
            this.smGattConnector = smGattConnector;
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    ELog.d("连接设备成功，正在发现服务");
                    if (mBluetoothGatt != null) {
                        ELog.d("发现服务中...");
                        mBluetoothGatt.discoverServices();
                    }
                }
            } else {
                if (isConnect) {
                    ELog.e("设备断开连接");
                    isConnect = false;
                    isDown = false;
                    if(isEcgMoudel){
                        if (mEcgDataResultListener != null) {
                            mHandler.post(() -> mEcgDataResultListener.onError(ErrorCode.MSG_ERROR_DEVICE_IS_DISCONNECT));
                        }
                    }else{
                        if (mBleBindResultListener != null) {
                            mHandler.post(() -> mBleBindResultListener.onBindError(ErrorCode.MSG_ERROR_DEVICE_IS_DISCONNECT));
                        }
//                        if (mBleConnectResultListener != null) {
//                            mHandler().post(() -> mBleConnectResultListener.onConnectError(ErrorCode.MSG_ERROR_DEVICE_IS_DISCONNECT));
//                        }
                    }
                } else {
                    ELog.e("连接设备失败");
                    endTimeOut();
                    if (mBluetoothGatt != null) {
                        mBluetoothGatt.close();
                    }
                    reConnectionNum = 0;
                    if(isEcgMoudel){
                        if (mEcgDataResultListener != null) {
                            mHandler.post(() -> mEcgDataResultListener.onError(ErrorCode.MSG_ERROR_CONNNECT_DEVICE_FAIL));
                        }
                    } else{
//                        if (mBleConnectResultListener != null) {
//                            mHandler().post(() -> mBleConnectResultListener.onConnectError(ErrorCode.MSG_ERROR_CONNNECT_DEVICE_FAIL));
//                        }
                        if (mBleBindResultListener != null) {
                            mHandler.post(() -> mBleBindResultListener.onBindError(ErrorCode.MSG_ERROR_CONNNECT_DEVICE_FAIL));
                        }
                    }
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ELog.d("已发现服务,开始发送配置参数...");
                gatt.requestMtu(512);
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ELog.d("配置参数已设置");
                if (gatt != null && smGattConnector!= null) {
                    List<BluetoothGattService> gattService = gatt.getServices();
                    if (gattService != null) {
                        BluetoothGattCharacteristic connetStatusCharacteristic = null;
                        for (BluetoothGattService item : gattService) {
                            List<BluetoothGattCharacteristic> listGattCharacteristic = item.getCharacteristics();
                            for (BluetoothGattCharacteristic characteristic : listGattCharacteristic) {
                                // 截取UUID最后4位
                                String uuid16 = characteristic.getUuid().toString().substring(4, 8);
                                characteristicMap.put(uuid16, characteristic);
                                if (isEcgMoudel) {
                                    if (uuid16.equals(UUID_TEST_DATA) || uuid16.equals(UUID_TEST_RESULT) || uuid16.equals(UUID_TEST_DATA_NEW)) {
                                        connetStatusCharacteristic = characteristic;
                                        ELog.d("ecg_uuid");
                                        if(uuid16.equals(UUID_TEST_DATA_NEW)){
                                            isNewDevice = true;
                                        }
                                    }
                                } else {
                                    if (uuid16.equals(EA_UUID_WIFI_STATE)) {
                                        connetStatusCharacteristic = characteristic;
                                        ELog.d("bind_uuid");
                                    }
                                }
                                if (connetStatusCharacteristic != null) {
                                    boolean isEnableNotification = gatt.setCharacteristicNotification(connetStatusCharacteristic, true);
                                    if(isEnableNotification){
                                        BluetoothGattDescriptor mDescriptors = connetStatusCharacteristic.getDescriptor(connetStatusCharacteristic.getUuid());
                                        if(mDescriptors != null){
                                            boolean  b0 = mDescriptors.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                            if(b0){
                                                mBluetoothGatt.writeDescriptor(mDescriptors);
                                            }
                                        }
                                    }
                                    connetStatusCharacteristic = null;
                                }
                            }
                        }
                        ELog.d(mSerial_number+"设备连接成功");
                        reConnectionNum = 0;
                        isConnect = true;
                        endTimeOut();
                        if (smGattConnector != null) {
                            smGattConnector.drvConnect(mBluetoothGatt, characteristicMap);
                            if(isEcgMoudel) {
                                if(mEcgDataResultListener != null){
                                    mHandler.post(() -> mEcgDataResultListener.onConnetSucceed(isNewDevice));
                                }
                                openNewData();
                                getTemperature();
                            } else{
                                if(mBleBindResultListener != null){
                                    mHandler.post(() -> mBleBindResultListener.onConnetSucceed());
                                }
                                send(false);
                            }
                        } else {
                            ELog.e("smGattConnector==null");
                        }

                    }
//                    if (mBleConnectResultListener != null && !isEcgMoudel) {
//                        mHandler().post(() -> mBleConnectResultListener.onConnectSucceed());
//                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                String uuid16 = characteristic.getUuid().toString().substring(4, 8);
                smGattConnector.onUpdate(uuid16, characteristic.getValue());
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            String uuid16 = characteristic.getUuid().toString().substring(4, 8);
            if(uuid16.equals(EA_UUID_WIFI_STATE)){
                if(isSendConnet){
                    smGattConnector.onUpdate(uuid16, characteristic.getValue());
                }
            } else{
                smGattConnector.onUpdate(uuid16, characteristic.getValue());
            }
        }

    }

    //新固件，支持ecg/ppg双波行
    private void openNewData(){
        ELog.d( "是否支持多波形："+isNewDevice);
        if(isNewDevice){
            mGattWriteQueue.clear();
            mGattWriteQueue.add(new GattCharaValue(UUID_TEST_DATA_NEW, "All".getBytes()));
            if (smGattConnector != null && mGattWriteQueue != null && mGattWriteQueue.size() > 0) {
                if (smGattConnector.appWrite(mGattWriteQueue.get(0).getUuid(), mGattWriteQueue.get(0).getValue())) {
                    mGattWriteQueue.remove(0);
                }
            }
        }
    }

    private Timer temperatureTimer;
    private void getTemperature() {
        ELog.d("获取腿温...");
        if (temperatureTimer != null) {
            temperatureTimer.cancel();
        }
        mGattReadQueue.clear();
        mGattReadQueue.add(UUID_TEST_DATA_TEMPERATURE);
        temperatureTimer = new Timer();
        temperatureTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (smGattConnector != null && mGattReadQueue != null && mGattReadQueue.size() > 0 && isDown && isConnect) {
                    smGattConnector.appRead(mGattReadQueue.get(0));
                }
            }

        }, 500, 3000);
    }

    //结束计时
    private void endTimeOut(){
        if (downTimer != null) {
            downTimer.cancel();
            downTimer = null;
        }
    }

    //蓝牙连接超时
    private void timeOutTimer(){
        ELog.d("timeOutTimer()");
        downTimer = new CountDownTimer(TIME_OUT_SECONDS*1000,  1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if(!isConnect){
                    if(!isEcgMoudel && mBleBindResultListener!= null){
                        ELog.e(ErrorCode.MSG_ERROR_CONNECT_TIME_OUT);
                        mHandler.post(() -> mBleBindResultListener.onBindError(ErrorCode.MSG_ERROR_CONNECT_TIME_OUT));
                    }
                    if(isEcgMoudel && mEcgDataResultListener!= null){
                        ELog.e(ErrorCode.MSG_ERROR_CONNECT_TIME_OUT);
                        mHandler.post(() -> mEcgDataResultListener.onError(ErrorCode.MSG_ERROR_CONNECT_TIME_OUT));
                    }
                }
            }
        }.start();
    }

    private void disconnet(){
        ELog.d(  "disconnet()");
        isConnect = false;
        reSendNum = 0;
        if (timer != null) {
            timer.cancel();
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    public void onColse() {
        ELog.d(  "onColse()");
        endTimeOut();
        disconnet();
        isDown = false;
        if(temperatureTimer != null){
            temperatureTimer.cancel();
        }
        mBleBindResultListener = null;
        //mBleConnectResultListener = null;
        mEcgDataResultListener = null;
    }

}
