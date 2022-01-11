package com.eaphone.lib_sdk.http;


public class BaseResponseEntity<T> extends  BaseResponseStatusEntity{

    private T data = null;

    public BaseResponseEntity(boolean success, T data) {
        super(success);
        this.data = data;
    }

    public BaseResponseEntity(boolean success, String errcode, T data) {
        super(success, errcode);
        this.data = data;
    }

    public BaseResponseEntity(boolean success, String errcode, String message, T data) {
        super(success, errcode, message);
        this.data = data;
    }

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
