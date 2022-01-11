package com.eaphone.lib_sdk.http.api;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import com.eaphone.lib_sdk.ble.EaphoneCommUtils;
import com.eaphone.lib_sdk.common.EcgReportData;
import com.eaphone.lib_sdk.common.ErrorCode;
import com.eaphone.lib_sdk.http.TokenResultEntity;
import com.eaphone.lib_sdk.http.BaseResponseEntity;
import com.eaphone.lib_sdk.http.EahponeOkhttpRequest;
import com.eaphone.lib_sdk.http.EaphoneOkHttpException;
import com.eaphone.lib_sdk.http.IResponseCallback;
import com.eaphone.lib_sdk.listener.EcgDataCallBack;
import com.eaphone.lib_sdk.listener.InitResultListener;
import com.eaphone.lib_sdk.utils.ELog;
import com.eaphone.lib_sdk.utils.SPUtils;
import com.eaphone.lib_sdk.utils.SpConstant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ApiTools {

    public static void setToken(String appid, String secret, InitResultListener listener){
        ELog.d("sdk init");
        if(listener == null){
            ELog.e(ErrorCode.MSG_ERROR_INIT_LISTENER_IS_NULL);
        }
        if(TextUtils.isEmpty(appid)){
            ELog.e(ErrorCode.MSG_ERROR_APP_ID_IS_NULL);
            if(listener != null){
                listener.onError(ErrorCode.MSG_ERROR_APP_ID_IS_NULL);
            }
            return;
        }
        if(TextUtils.isEmpty(secret)){
            ELog.e(ErrorCode.MSG_ERROR_SECRET_IS_NULL);
            if(listener != null){
                listener.onError(ErrorCode.MSG_ERROR_SECRET_IS_NULL);
            }
            return;
        }
        SPUtils.getInstance(SpConstant.SP_NAME).remove(SpConstant.TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("appid", appid);
        map.put("secret", secret);
        map.put("grant_type", "client_credential");
        EahponeOkhttpRequest.getInstance().doGet(API.GET_TOKEN, map, new IResponseCallback() {
            @Override
            public void onFail(EaphoneOkHttpException e) {
                ELog.e("sdk init fail");
                ELog.e(e.getE().toString());
                SpConstant.setLogin(false);
                if(listener != null){
                    listener.onError(e.getE().toString());
                }
            }

            @Override
            public void onResponse(String response) {
                //不再重复定义Result类
                Type userType = new TypeToken<BaseResponseEntity<TokenResultEntity>>(){}.getType();
                BaseResponseEntity<TokenResultEntity> baseResult = new Gson().fromJson(response,userType);
                if(baseResult.isSuccess()){
                    ELog.d("sdk init success");
                    TokenResultEntity result = baseResult.getData();
                    SpConstant.setToken(result.getAccess_token());
                    SpConstant.setAppID(appid);
                    SpConstant.setSecret(secret);
                    SpConstant.setLogin(true);
                    if(listener != null){
                        listener.onSucceed();
                    }
                } else{
                    ELog.e("sdk init fail:" + baseResult.getMessage());
                    SpConstant.setLogin(false);
                    if(listener != null){
                        listener.onError(baseResult.getMessage());
                    }
                }
            }
        });
    }

    public static void getReportData(BluetoothDevice device, EcgDataCallBack dataCallBack){
        ELog.d("get report data");
        if(dataCallBack == null){
            ELog.e("EcgDataCallBack is null");
            return;
        }
        if(!SpConstant.isLogin()){
            ELog.e(ErrorCode.MSG_INIT_SDK_FAIL);
            dataCallBack.onError(ErrorCode.CODE_INIT_SDK_FAIL, ErrorCode.MSG_INIT_SDK_FAIL);
            return;
        }
        if(device == null){
            ELog.e(ErrorCode.MSG_ERROR_DEVICER_IS_NULL);
            dataCallBack.onError(ErrorCode.CODE_ERROR_DEVICER_IS_NULL, ErrorCode.MSG_ERROR_DEVICER_IS_NULL);
            return;
        }
        String serialNumber = EaphoneCommUtils.getSerialNumber(device.getName());
        String url = API.REPORT_DATA+serialNumber+"/";
        EahponeOkhttpRequest.getInstance().doGet(url, new IResponseCallback() {
            @Override
            public void onFail(EaphoneOkHttpException e) {
                ELog.e("sdk getReportData fail");
                ELog.e(e.getE().toString());
                dataCallBack.onError( ErrorCode.CODE_ERROR_IO_EXCEPTION, e.getE().toString());
            }

            @Override
            public void onResponse(String response) {
                //不再重复定义Result类
                Type userType = new TypeToken<BaseResponseEntity<EcgReportData>>(){}.getType();
                BaseResponseEntity<EcgReportData> baseResult = new Gson().fromJson(response,userType);
                if(baseResult.isSuccess()){
                    ELog.d("sdk getReportData success");
                    EcgReportData data = baseResult.getData();
                    dataCallBack.onSucceed(data);
                } else{
                    ELog.e("sdk getReportData fail:"+baseResult.getMessage());
                    dataCallBack.onError(baseResult.getErrcode(), baseResult.getMessage());
                }
            }
        });
    }
}
