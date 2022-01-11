package com.eaphone.lib_sdk.http;


public class BaseResponseStatusEntity {
    private boolean success;
    private String errcode;
    private String message;



    public BaseResponseStatusEntity(boolean success) {
        this.success = success;
    }

    public BaseResponseStatusEntity(boolean success, String errcode) {
        this.success = success;
        this.errcode = errcode;
    }

    public BaseResponseStatusEntity(boolean success, String errcode, String message) {
        this.success = success;
        this.errcode = errcode;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
