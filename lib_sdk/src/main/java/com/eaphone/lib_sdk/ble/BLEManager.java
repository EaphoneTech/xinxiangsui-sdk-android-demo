package com.eaphone.lib_sdk.ble;

import android.bluetooth.*;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import com.eaphone.lib_sdk.entity.GattCharaValue;
import com.eaphone.lib_sdk.listener.BleBindResultListener;
import com.eaphone.lib_sdk.listener.BleConnectResultListener;
import com.eaphone.lib_sdk.listener.BleScanListener;
import com.eaphone.lib_sdk.listener.EcgDataResultListener;
import com.eaphone.lib_sdk.utils.ELog;
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

    private LinkedList<GattCharaValue> mGattWriteQueue = null;
    private LinkedList<String> mGattReadQueue = null;

    private BleBindResultListener mBleBindResultListener = null;
    private BleConnectResultListener mBleConnectResultListener = null;
    private EcgDataResultListener mEcgDataResultListener = null;
    private int reConnectionNum = 0;//定义重连次数
    private final int maxConnectionNum = 3;//最多重连次数
    private boolean isScaning = false;//正在扫描
    private boolean isEcgMoudel = false;//ECG模式
    private boolean isConnect = false;
    private boolean isNewDevice = false;
    private String mSerial_number = null;
    private String mWifi_name = null;
    private String mWifi_password = null;

    private List<BluetoothDevice> mBluetoothDeviceList;
    /**
     * 配网模式
     * true传serial_number到SDK扫描加配网
     * fals传device到SDK配网
     */
    private boolean isScan = true;

    private BLEManager(Context context) {
        mContext = context.getApplicationContext();
        innit();
    }

    /**
     * 获取一个BLEManager单例
     * @param context Context
     * @return 返回一个BLEManager的实例对象
     */
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
            ELog.e("蓝牙未打开");
        }
    }

    public BleBindResultListener getmBleBindResultListener() {
        return mBleBindResultListener;
    }

    public void setmBleBindResultListener(BleBindResultListener mBleBindResultListener) {
        this.mBleBindResultListener = mBleBindResultListener;
    }

    /**
     * 获取附近设备列表
     * @param bleScanListener BleScanListener
     */
    public void getDeviceList(BleScanListener bleScanListener) {
        mBleScanListener = bleScanListener;
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
        ELog.d("bindDevice");
        if (listener == null) {
            ELog.e("BleBindResultListener null");
            return;
        }
        if (device == null) {
            ELog.e("device null");
            listener.onBindError("device null");
            return;
        }
        if (TextUtils.isEmpty(wifi_name)) {
            listener.onBindError("wifi_name null");
            ELog.e("wifi_name null");
            return;
        }
        if (TextUtils.isEmpty(wifi_password)) {
            listener.onBindError("wifi_password nul");
            ELog.e("wifi_password null");
            return;
        }
        isEcgMoudel = false;
        isScan = false;
        mBleBindResultListener = listener;
        mSerial_number = EaphoneCommUtils.getSerialNumber(device.getName());
        mWifi_name = wifi_name;
        mWifi_password = wifi_password;
        connetBluetoothDevice(device);
    }


    /**
     * 获取ECG数据
     * @param device 设备
     * @param listener EcgDataResultListener
     */
    public void getEcgData(BluetoothDevice device, EcgDataResultListener listener) {
        ELog.d("getEcgData()");
        if (device == null) {
            ELog.e("device null");
            return;
        }
        if (listener == null) {
            ELog.e("EcgDataResultListener null");
            return;
        }
        isNewDevice = false;
        isEcgMoudel = true;
        isScan = false;
        mEcgDataResultListener = listener;
        mSerial_number = EaphoneCommUtils.getSerialNumber(device.getName());
        //停止扫描
        scanLeDevice(false);
        if (!mBluetoothAdapter.isEnabled()) {
            ELog.e("蓝牙未打开");
            listener.onError("蓝牙未打开");
            return;
        }
        ELog.d("正在配对:" + device.getName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        }
    }

    private Timer timer;
    //发送配网数据，开始配网
    private void send() {
        ELog.d("send()---开始配网");
        if (!isConnect) {
            ELog.e("ble未连接，发送配网请求失败");
            if(mBleBindResultListener != null){
                mBleBindResultListener.onBindError("ble未连接，发送配网请求失败");
            }
            return;
        }
        setEphoneData(mSerial_number, mWifi_name, mWifi_password);
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (smGattConnector != null && mGattWriteQueue != null && mGattWriteQueue.size() > 0) {
                    if (smGattConnector.appWrite(mGattWriteQueue.get(0).getUuid(), mGattWriteQueue.get(0).getValue())) {
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

    private void setEphoneData(String serial_number, String wifi_name, String wifi_password) {
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
                            case "disconnected":
                                reSend(0);
                                break;
                            case "connected":
                                if(mBleBindResultListener!= null){
                                    mBleBindResultListener.onBindSucceed();
                                }
                                onColse();
                                break;
                            case "disconnected:410":
                                reSend(1);
                                break;
                            case "disconnected:404":
                                reSend(2);
                                break;
                            case "disconnected:500":
                                reSend(3);
                                break;
                        }
                        break;
                    case UUID_TEST_DATA:
                        int status = EaphoneCommUtils.pinJie2ByteToInt(value[0], value[1]);
                        if (status == -1) { //离坐信号
                            if(mEcgDataResultListener!= null){
                                mEcgDataResultListener.onDeviceStatus(false);
                            }
                            isFistData = true;
                        }else{//数据信号
                           // int l = value.length;
                            if(isFistData){
                                if(mEcgDataResultListener!= null){
                                    mEcgDataResultListener.onDeviceStatus(true);
                                }
                                isFistData = false;
                            }
                            List<Integer> ecgData = new ArrayList<>();
                            for(int i =2; i< value.length; i++ ){
                                if(i%2 == 0){
                                    int data = EaphoneCommUtils.pinJie2ByteToInt(value[i], value[i+1]);
                                    ecgData.add(data);
                                }
                            }
                            if(mEcgDataResultListener!= null){
                                mEcgDataResultListener.onDataResult(status, ecgData, null);
                            }
                        }
                        break;
                    case UUID_TEST_DATA_NEW:
                        int statu = EaphoneCommUtils.pinJie2ByteToInt(value[0], value[1]);
                        if (statu == -1) { //离坐信号
                            if(mEcgDataResultListener!= null){
                                mEcgDataResultListener.onDeviceStatus(false);
                            }
                            isFistData = true;
                        }else{//数据信号
                            // int l = value.length;
                            if(isFistData){
                                if(mEcgDataResultListener!= null){
                                    mEcgDataResultListener.onDeviceStatus(true);
                                }
                                isFistData = false;
                            }
                            List<Integer> ecgData = new ArrayList<>();
                            List<Integer> ppgData = new ArrayList<>();
                            for(int i =2; i< 61; i++ ){
                                if(i%2 == 0){
                                    int data = EaphoneCommUtils.pinJie2ByteToInt(value[i], value[i+1]);
                                    ecgData.add(data);
                                }

                            }
                            for(int i =62; i< 121; i++ ){
                                if(i%2 == 0){
                                    int data = EaphoneCommUtils.pinJie2ByteToInt(value[i], value[i+1]);
                                    ppgData.add(data);
                                }
                            }
                            if(mEcgDataResultListener!= null){
                                mEcgDataResultListener.onDataResult(statu, ecgData, ppgData);
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
                                    if(mEcgDataResultListener!= null){
                                        mEcgDataResultListener.onEcgCuntResult(jsonObject.getInt("_v"));
                                    }
                                    break;
                                case "Sample":  //ecg信号状态
                                    if(mEcgDataResultListener!= null){
                                        mEcgDataResultListener.onECGStatusResult(jsonObject.getInt("quality"));
                                    }
                                    break;
                                case "all":  //信号状态
                                    int v = jsonObject.getInt("HeartRate");
                                    if(v != 0){
                                        if(mEcgDataResultListener!= null){
                                            mEcgDataResultListener.onEcgCuntResult(v);
                                        }
                                    }
                                    int ecg = jsonObject.getInt("Sample_ECG");
                                    if(ecg != 0){
                                        if(mEcgDataResultListener!= null){
                                            mEcgDataResultListener.onECGStatusResult(ecg);
                                        }
                                    }
                                    int ppg = jsonObject.getInt("Sample_PPG");
                                    if(ppg != 0){
                                        if(mEcgDataResultListener!= null){
                                            mEcgDataResultListener.onPPGStatusResult(ppg);
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
                        ELog.d("onScanResult: " + device.getName());
                        if (mBleScanListener == null) {
                            ELog.e("BleScanListener为空");
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

    private void reSend(int type) {
        if (type == 1) {
            ELog.e("wifi密码错误,配网失败");
            if(mBleBindResultListener != null){
                mBleBindResultListener.onBindError("wifi密码错误,配网失败");
            }
            onColse();
        } else {
            if (reConnectionNum < maxConnectionNum) {
                //重连次数自增
                reConnectionNum++;
                send();
            } else {
                ELog.e("未知错误,配网失败");
                if(mBleBindResultListener!= null){
                    mBleBindResultListener.onBindError("未知错误,配网失败");
                }
                reConnectionNum = 0;
                onColse();
            }
        }
    }

    private void connetBluetoothDevice(BluetoothDevice device) {
        //停止扫描
        isNewDevice = false;
        scanLeDevice(false);
        if(mBleBindResultListener == null){
            ELog.e("BleBindResultListener null");
            return;
        }
        if (device == null) {
            ELog.e("bond device null");
            if(mBleBindResultListener != null){
                mBleBindResultListener.onBindError("device is null");
            }
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            ELog.e("蓝牙未打开");
            if(mBleBindResultListener != null){
                mBleBindResultListener.onBindError("蓝牙未打开");
            }
            return;
        }
        ELog.d("正在配对:" + device.getName());
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

    private void scanLeDevice(Boolean enable) {
        ELog.d("scanLeDevice()----" + enable);
        if (!mBluetoothAdapter.isEnabled()) {
            ELog.e("蓝牙未打开");
            if (mBleScanListener != null) {
                mBleScanListener.onError("蓝牙未打开");
            }
            return;
        }
        if (!checkPermission()) {
            ELog.e("请打开定位权限--android.permission.ACCESS_FINE_LOCATION");
            if (mBleScanListener != null) {
                mBleScanListener.onError("没有获取到定位权限");
            }
            return;
        }
        if (!EaphoneCommUtils.isLocServiceEnable(mContext)) {
            ELog.e("GPS未打开");
            if (mBleScanListener != null) {
                mBleScanListener.onError("GPS未打开");
            }
            return;
        }
        if (mScanCallback == null) {
            ELog.e("mScanCallback null");
            if (mBleScanListener != null) {
                mBleScanListener.onError("ScanCallback == null");
            }
            return;
        }
        if (enable) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    BLEManager.this.scanLeDevice(false);
                }
            }, 10000);
            if (isScaning) {
                ELog.e("正在扫描设备中...，请稍后再获取设备列表");
                if (mBleScanListener != null) {
                    mBleScanListener.onError("正在扫描设备中...，请稍后再获取设备列表");
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
            isScaning = false;
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
            if (mBleScanListener != null) {
                mBleScanListener.onScanFinished(mBluetoothDeviceList);
            }
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
                if (!isConnect) {
                    if (mBluetoothGatt != null) {
                        mBluetoothGatt.close();
                    }
                    ELog.e("连接设备失败");
                    reConnectionNum = 0;
                    if(isEcgMoudel){
                        if (mEcgDataResultListener != null) {
                            mEcgDataResultListener.onError("连接设备失败");
                        }
                    } else{
                        if (mBleConnectResultListener != null) {
                            mBleConnectResultListener.onConnectError("连接设备失败");
                        }
                        if (mBleBindResultListener != null) {
                            mBleBindResultListener.onBindError("连接设备失败");
                        }
                    }

                } else {
                    ELog.e("设备断开连接");
                    if(isEcgMoudel){
                        if (mEcgDataResultListener != null) {
                            mEcgDataResultListener.onError("设备断开连接");
                        }
                    }else{
                        if (mBleBindResultListener != null) {
                            mBleBindResultListener.onBindError("设备断开连接");
                        }
                        if (mBleConnectResultListener != null) {
                            mBleConnectResultListener.onConnectError("设备断开连接");
                        }
                    }

                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ELog.d("发现服务,requestMtu()...");
                gatt.requestMtu(512);
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ELog.d("requestMtu成功");
                if (gatt != null) {
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
                                        ELog.d("service_uuid:"+item.getUuid());
                                        ELog.d("uuid:"+characteristic.getUuid().toString());
                                        if(uuid16.equals(UUID_TEST_DATA_NEW)){
                                            isNewDevice = true;
                                        }
                                    }
                                } else {
                                    if (uuid16.equals(EA_UUID_WIFI_STATE)) {
                                        connetStatusCharacteristic = characteristic;
                                        ELog.d("uuid:"+characteristic.getUuid().toString());
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
                        if (smGattConnector != null) {
                            smGattConnector.drvConnect(gatt, characteristicMap);
                            if(isEcgMoudel) {
                                if(mEcgDataResultListener != null){
                                    mEcgDataResultListener.onConnetSucceed(isNewDevice);
                                }
                                openNewData();
                            } else{
                                send();
                            }
                        } else {
                            ELog.e("smGattConnector==null");
                        }
                    }
                    if (mBleConnectResultListener != null && !isEcgMoudel) {
                        mBleConnectResultListener.onConnectSucceed();
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                String uuid16 = characteristic.getUuid().toString().substring(4, 8);
                ELog.d("onCharacteristicRead:uuid"+uuid16);
                smGattConnector.onUpdate(uuid16, characteristic.getValue());
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            String uuid16 = characteristic.getUuid().toString().substring(4, 8);
            ELog.d("onCharacteristicChanged:uuid"+uuid16);
            smGattConnector.onUpdate(uuid16, characteristic.getValue());
        }

    }

    //新固件，支持ecg/ppg双波行
    private void openNewData(){
        ELog.e( "openNewData（）， isNewDevice="+isNewDevice);
        if(isNewDevice){
            mGattWriteQueue.clear();
            mGattWriteQueue.add(new GattCharaValue(UUID_TEST_DATA_NEW, "All".getBytes()));
            if (smGattConnector != null && mGattWriteQueue != null && mGattWriteQueue.size() > 0) {
                if (smGattConnector.appWrite(mGattWriteQueue.get(0).getUuid(), mGattWriteQueue.get(0).getValue())) {
                    ELog.d(  "appWrite true");
                    if(mGattWriteQueue.get(0).getUuid().equals(UUID_TEST_DATA_NEW)){
                        ELog.d(  "isSendNewData");
                    }
                    mGattWriteQueue.remove(0);
                }
            }
        }
    }

    public boolean isNewDevice() {
        return isNewDevice;
    }

    public void setNewDevice(boolean newDevice) {
        isNewDevice = newDevice;
    }

    public void onColse() {
        ELog.d(  "onColse()");
        isConnect = false;
        if (timer != null) {
            timer.cancel();
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        mBleBindResultListener = null;
        mBleConnectResultListener = null;
        mEcgDataResultListener = null;
    }

}
