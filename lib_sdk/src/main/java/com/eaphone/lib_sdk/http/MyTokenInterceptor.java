package com.eaphone.lib_sdk.http;

import android.text.TextUtils;
import com.eaphone.lib_sdk.common.ErrorCode;
import com.eaphone.lib_sdk.http.api.ApiTools;
import com.eaphone.lib_sdk.listener.InitResultListener;
import com.eaphone.lib_sdk.utils.SpConstant;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;


public class MyTokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        if(TextUtils.isEmpty(SpConstant.getToken())){
            response = chain.proceed(request);
        } else if(request.header("Authorization") != null){
            response = chain.proceed(request);
        } else{
            Request.Builder requestBuilder = request.newBuilder();
            requestBuilder.addHeader("Authorization", "Bearer " + SpConstant.getToken());
            Request mRequest = requestBuilder.build();
            response = chain.proceed(mRequest);
        }
        //根据和服务端的约定判断token过期
        if (isTokenExpired(response)) {
            String app_id = SpConstant.getAppID();
            String secret = SpConstant.getSecret();
            ApiTools.setToken(app_id, secret, new InitResultListener() {
                @Override
                public void onSucceed() {

                }

                @Override
                public void onError(String result) {

                }
            });
        }
        return response;
    }

    private boolean isTokenExpired(Response response){
        try {
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.buffer();
                Charset charset= Charset.forName("UTF-8");
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(Charset.forName("UTF-8"));
                }
                String bodyString = buffer.clone().readString(charset);
                JSONObject jsonObject = new JSONObject(bodyString);
                if(bodyString.contains("code")){
                    String code = jsonObject.getString("code");
                    return code.equals(ErrorCode.CODE_ERROR_TOKEN_IS_EXCEED);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
