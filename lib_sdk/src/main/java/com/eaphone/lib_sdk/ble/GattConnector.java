package com.eaphone.lib_sdk.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import com.eaphone.lib_sdk.utils.ELog;

import java.util.Map;

/**
 * GattConnector
 */
abstract class GattConnector {

    // 底层Gatt接口
    private BluetoothGatt mBluetoothGatt= null;
    // 已获取的属性表
    private Map<String, BluetoothGattCharacteristic> characteristicMap = null;

    /**
     * 应用层异步写入
     * @param uuid16 uuid16
     * @param value byte[]
     * @return true or false
     */
    boolean appWrite(String uuid16, byte[] value){
        if (mBluetoothGatt == null) {
            ELog.e("appWrite GATT is disconnected");
            return false;
        }
        if(characteristicMap == null){
            ELog.e( "characteristicMap is null");
            return false;
        }
        BluetoothGattCharacteristic characteristic = characteristicMap.get(uuid16);
        if (characteristic == null) {
            ELog.e( "appWrite GATT no such characteristic");
            return false;
        }
        if (!characteristic.setValue(value)) {
            ELog.e("appWrite GATT write error ");
            return false;
        }
        if (!mBluetoothGatt.writeCharacteristic(characteristic)) {
            ELog.e("doWrite GATT write error ");
            return false;
        }
        ELog.d("appWrite true");
        return true;
    }

    /**
     * 应用层异步读取
     * @param uuid16 uuid16
     * @return true or false
     */
    boolean appRead(String uuid16) {
        if (mBluetoothGatt == null) {
            ELog.e("appRead error GATT is disconnected");
            return false;
        }
        if(characteristicMap == null){
            ELog.e( "characteristicMap is null");
            return false;
        }
        BluetoothGattCharacteristic characteristic = characteristicMap.get(uuid16);
        if (characteristic == null) {
            ELog.e( "appRead gatt no such characteristic");
            return false;
        }
        if (mBluetoothGatt.readCharacteristic(characteristic)) {
        //    ELog.d("appRead true");
            return true;
        } else
        ELog.e("appRead failed");
        return false;
    }

    /**
     * 应用层关闭连接
     */
    void appDisconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    /**
     * 驱动层获取服务回调
     * @param bluetoothGatt BluetoothGatt
     * @param characteristicMap Map<String, BluetoothGattCharacteristic>>
     */
    void drvConnect(BluetoothGatt bluetoothGatt, Map<String, BluetoothGattCharacteristic> characteristicMap) {
        this.characteristicMap = characteristicMap;
        this.mBluetoothGatt = bluetoothGatt;

    }


    /**
     * 属性更新回调
     * @param uuid uuid
     * @param value byte[]
     */
    abstract void onUpdate(String uuid, byte[] value);
}
