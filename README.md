# 易风设备管理SDK
## 说明
易风设备管理SDK，包括获取设备列表，设备配网和设备实时波形数据，根据需要您可以选择实现相关接口

[EaphoneSDKDemo](https://github.com/a312588726/EaphoneSDK)
## 集成
一：Gradle：
在module的build.gradle文件中加入以下依赖：
```language
implementation 'io.github.a312588726:lib_sdk:1.0.1'
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
将EaphoneSDKDemo中libs目录下的 lib_sdk-1.0.1.aar 拷贝至项目libs目录下,

```language
dependencies {
    compile(name:'lib_sdk-1.0.1', ext:'aar')
}
```


## 权限申请
```language

<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

```
## SDK初始化
使用心相随sdk,需要先在平台申请app_key

```language
EaphoneInterface.init(mContext, app_key, mInitResultListener)
```
参数说明

|参数|类型|说明|
|-|-|-|
|mContext|Context|--|
|app_key|String|心相随平台申请的app_key|
|mInitResultListener|InitResultListener|初始化回调函数|

**InitResultListener**
|方法|说明|
|-|-|
|onSucceed（）|初始化成功|
|onError（String result）|初始化失败，result（失败原因）|

## 设备列表
获取附近心相随设备列表，需要打开蓝牙和获取定位权限。
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
|onDataResult（long time, List<Integer> ecgData, List<Integer> ppgData）|实时波行数据，返回：time(监测时长)，ecgData（每秒ecg数据,item值max=30000, min=-30000），ppgData（每秒ppg数据，,item值max=30000, min=-30000）|

## 退出设备连接
相关页面关闭，需要调用退出接口，防止内存泄漏
代码示例：
```language

   override fun onDestroy() {
        super.onDestroy()
        EaphoneInterface.disConnetDevice(mContext)
    }


```
