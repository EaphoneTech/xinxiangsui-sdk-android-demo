package com.eaphone.lib_sdk.http;


public class BaseResponseEntity<T> extends  BaseResponseStatusEntity{

    private T data = null;
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponseEntity{" +
                "data=" + data +
                '}';
    }
}
