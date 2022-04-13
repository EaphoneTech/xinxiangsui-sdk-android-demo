# 易风设备管理SDK
## 说明
易风设备管理SDK，包括SDK初始化（）、获取设备列表（）、设备配网（）和设备实时波形数据（），根据需要您可以选择实现相关接口

[Demo](https://github.com/EaphoneTech/xinxiangsui-sdk-android-demo)
## 集成
一：Gradle：
在module的build.gradle文件中加入以下依赖：
SDK网络框架采用okhttp3，为了您能正常使用，需如下依赖
```language
dependencies {
    implementation 'io.github.eaphonetech:lib_sdk:1.0.6'
    //下面无论是maven途径还是.aar 都需要，你也可以根据项目需要替换成相应版本号
    implementation "com.squareup.okhttp3:okhttp:4.9.3"
    implementation "com.squareup.okhttp3:logging-interceptor:4.9.3"
    implementation 'com.google.code.gson:gson:2.8.9'
}
```
在项目根目录的build.gradle文件下添加以下：
```language

allprojects {
    repositories {
        mavenCentral()
    }
}

```

二：aar包:
将EaphoneSDKDemo中libs目录下的 lib_sdk-1.0.6.aar 拷贝至项目libs目录下,

```language
dependencies {
    implementation files('libs/lib_sdk-1.0.6.aar')
    //下面无论是maven途径还是.aar 都需要，你也可以根据项目需要替换成相应版本号
    implementation "com.squareup.okhttp3:okhttp:4.9.3"
    implementation "com.squareup.okhttp3:logging-interceptor:4.9.3"
    implementation 'com.google.code.gson:gson:2.8.9'
}
```


## 权限申请
```language
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

```
## SDK初始化
使用心相随sdk,需要先在平台申请app_id

```language
EaphoneInterface.init(mContext, app_id, app_secret, mInitResultListener)
```
参数说明

|参数|类型|说明|
|-|-|-|
|mContext|Context|--|
|app_id|String|心相随平台申请的app_id|
|app_secret|String|心相随平台申请的app_secret|
|mInitResultListener|InitResultListener|初始化回调函数|

**InitResultListener**
|方法|说明|
|-|-|
|onSucceed（）|初始化成功|
|onError（String result）|初始化失败，result（失败原因）|

## 设备列表
获取附近心相随设备列表，需要打开蓝牙和获取定位权限/GPS（特殊终端无需GPS，具体实现可参考蓝牙通信协议）。
代码示例：
```language
EaphoneInterface.getBindDevices(mContext, mBleScanListener)
```
参数说明

|参数|类型|说明|
|-|-|-|
|mContext|Context|--|
|mBleScanListener|BleScanListener|返回设备列表的回调函数|

**BleScanListener**
|方法|说明|
|-|-|
|onScanStarted（）|扫描开始|
|onLeScan(BluetoothDevice device)|扫描到设备，返回device（设备）|
|onScanFinished（List<BluetoothDevice> scanResultList）|扫描结束，返回scanResultList（总设备列表）|
|onError（String result）|扫描失败，result（失败原因）|

## 设备配网
心相随设备发起配网请求
代码示例：
```language
EaphoneInterface.netBind(mContext, mBluetoothDevice, wifi_name, wifi_password, mBleBindResultListener)
```
参数说明

|参数|类型|说明|
|-|-|-|
|mContext|Context|--|
|mBluetoothDevice|BluetoothDevice|心相随设备|
|wifi_name|String|wifi名称|
|wifi_password|String|wifi密码|
|mBleBindResultListener|BleBindResultListener|发起配网请求的回调函数|

**BleBindResultListener**
|方法|说明|
|-|-|
|onBindStarted（）|配网开始|
|onBindSucceed（）|配网成功|
|onBindError（String result）|配网失败，result（失败原因）|
|onConnetSucceed（）|设备连接成功|

## 实时波形数据

获取心相随设备的实时波形数据
代码示例：
```language
  EaphoneInterface.getECGData(mContext, mBluetoothDevice, mEcgDataResultListener)
```
参数说明

|参数|类型|说明|
|-|-|-|
|mContext|Context|--|
|mBluetoothDevice|BluetoothDevice|心相随设备|
|mEcgDataResultListener|EcgDataResultListener|实时波形数据的回调函数|

**EcgDataResultListener**
|方法|说明|
|-|-|
|onConnetSucceed（boolean isNewDevice）|连接设备成功，返回 isNewDevice（true 支持ecg/ppg双波形, false：仅ECG）|
|onError（String result）|配网失败，返回： result（失败原因）|
|onDeviceStatus（boolean isDown）|设备落座状态， 返回：isDown (true="落座" or fals="离坐")|
|onECGStatusResult（int ecg_status）|ecg数据信号状态，返回：ecg_status (1=正常 or -1=质量差)|
|onPPGStatusResult（int ppg_status）|ppg数据信号状态，返回：ppg_status (1=正常 or -1=质量差)|
|onEcgCuntResult（int heart_rate）|实时心率，返回：heart_rate(心率值)|
|onThighTemperatureResult(String thigh_temperature)|腿温，返回：thigh_temperature(腿温值)|
|onDataResult（long time, List<Integer> ecgData, List<Integer> ppgData1, List<Integer> ppgData2）|实时波行数据，返回：time(监测时长)，ecgData（每秒ecg数据,item值max=30000, min=-30000），ppgData1（每秒ppg数据，,item值max=30000, min=-30000），ppgData2（每秒ppg数据，,item值max=30000, min=-30000|

## 数据分析

获取心相随设备最近一次测量的数据分析结果
代码示例：
```language
  EaphoneInterface.getReportData(mBluetoothDevice, mEcgDataCallBack)
```
参数说明

|参数|类型|说明|
|-|-|-|
|mContext|Context|--|
|mBluetoothDevice|BluetoothDevice|心相随设备|
|mEcgDataCallBack|EcgDataCallBack|分析数据的回调函数|

**EcgDataCallBack**
|方法|说明|
|-|-|
|onSucceed（EcgReportData data）|成功，返回 data (EcgReportData)|
|onError（String errcode, String message）|失败，返回： errcode（错误码，具体参考ErrorCode类） message（错误原因）|


**EcgReportData**
|名称|类型|说明|
|-|-|-|
|batch_id|String|落座id|
|begin_time|String|开始时间|
|end_time|String|结束时间|
|heart_beats|int|总心搏|
|heart_rate|int|心率|
|heart_rate_max|int|最快心率|
|heart_rate_min|int|最慢心率|
|leg_temperature|double|腿温|
|respiration|int|呼吸率|
|duration|long|监测时长|


## 退出设备连接
相关页面关闭和SDK业务切换，需要调用退出接口EaphoneInterface.disConnetDevice（），防止内存泄漏和断开设备蓝牙连接
当设备已连接，设备无法被发现，调用设备列表接口，请确保设备已断开蓝牙连接。
代码示例：
```language

   override fun onDestroy() {
        super.onDestroy()
        EaphoneInterface.disConnetDevice(mContext)
    }


```

**ErrorCode类**

说明：SDK各接口返回错误码/消息说明类
路径：com.eaphone.lib_sdk.common.ErrorCode
|错误码|描述|
|-|-|
|8001001|APPID或Secret不正确|
|8001003|AccessToken已过期| 
|8004001|健康数据不存在|
|8004002|健康数据上传中|
|8004003|健康数据时长不够|
|8004004|健康数据分析中|
|100400|IOException|
|100101|SDK未初始化|
|100010|Device为空|
