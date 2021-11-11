package com.eaphone.lib_sdk.entity;

public class GattCharaValue {

    private String uuid   ;
    private byte[] value  ;

    public GattCharaValue(String uuid, byte[] value)
    {
        this.uuid  = uuid  ;
        this.value = value ;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
}
