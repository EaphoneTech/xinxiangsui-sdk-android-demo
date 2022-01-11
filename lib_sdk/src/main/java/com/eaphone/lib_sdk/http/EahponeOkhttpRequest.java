package com.eaphone.lib_sdk.http;




import android.content.Context;
import android.os.Handler;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


public class EahponeOkhttpRequest implements EaPhoneRequest {

    private static int cacheSize = 10 * 1024 * 1024; // 10 MiB

    private static OkHttpClient client;
    private Context mContext;

    public static Handler mHandler = new Handler();

    public static Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        return mHandler;
    }

    private volatile static EahponeOkhttpRequest instance = null;

    private EahponeOkhttpRequest() {
    }


    public static EahponeOkhttpRequest getInstance() {
        if (null == instance) {
            synchronized (EahponeOkhttpRequest.class) {
                if (null == instance) {
                    instance = new EahponeOkhttpRequest();
                }
            }
        }
        return instance;
    }

    @Override
    public void init(Context context) {
        mContext = context.getApplicationContext();
        client = getCilent();
    }

    private OkHttpClient getCilent() {
        if (client == null) {
            OkHttpClient.Builder mBuilder = new OkHttpClient.Builder().
                    connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
                  //  .addInterceptor(new LogInterceptor())
                    .addInterceptor(new MyTokenInterceptor());
               //     .cache(new Cache(mContext.getExternalFilesDir("manbaOkhttp"), cacheSize));
            client = mBuilder.build();
        }
        return client;

    }

    @Override
    public void doGet(String url, IResponseCallback callback) {
        doGet(url, null, null, callback);
    }

    @Override
    public void doGet(String url, Map<String, String> paramsMap, IResponseCallback callback) {
        doGet(url, paramsMap, null, callback);
    }

    @Override
    public void doGet(String url, Map<String, String> paramsMap, EaphoneOkhttpOption option, final IResponseCallback callback) {
        url = NetUtils.appendUrl(url, paramsMap);
        final EaphoneOkhttpOption manbaOkhttpOption = NetUtils.checkNetworkOption(option, url);
        Request.Builder builder = new Request.Builder().url(url).tag(manbaOkhttpOption.getTag());
        //builder = configHeaders(builder, manbaOkhttpOption);
        Request build = builder.build();
        getCilent().newCall(build).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleError(e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResult(response, callback);
            }
        });
    }

    @Override
    public void doPost(String url, Map<String, String> paramsMap, IResponseCallback callback) {
        doPost(url,paramsMap,null,callback);
    }

    @Override
    public void doPost(String url, Map<String, String> paramsMap, EaphoneOkhttpOption option, final IResponseCallback callback) {
        url = NetUtils.appendUrl(url, paramsMap);
        final EaphoneOkhttpOption manbaOkhttpOption = NetUtils.checkNetworkOption(option, url);
        // 以表单的形式提交
        FormBody.Builder builder = new FormBody.Builder();
        builder = configPostParam(builder,paramsMap);
        FormBody formBody = builder.build();
        Request.Builder requestBuilder = new Request.Builder().url(url).post(formBody).tag(manbaOkhttpOption.getTag());
        //requestBuilder = configHeaders(requestBuilder, manbaOkhttpOption);
        Request build = requestBuilder.build();

        getCilent().newCall(build).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleError(e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResult(response, callback);
            }
        });
    }

    private FormBody.Builder configPostParam(FormBody.Builder builder, Map<String, String> paramsMap) {
        if(paramsMap!=null){
            Set<Map.Entry<String, String>> entries = paramsMap.entrySet();
            for(Map.Entry<String,String> entry:entries ){
                String key = entry.getKey();
                String value = entry.getValue();
                builder.add(key,value);
            }
        }
        return builder;
    }

    private Request.Builder configHeaders(Request.Builder builder, EaphoneOkhttpOption option) {
        Map<String, String> headers = option.getHeaders();
        if (headers == null || headers.size() == 0) {
            return builder;
        }
        Set<Map.Entry<String, String>> entries = headers.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.addHeader(key, value);
        }
        return builder;

    }


    private void handleResult(Response response, final IResponseCallback callback) throws IOException {
        final String result = response.body().string();
        if (callback != null) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onResponse(result);
                }
            });
        }
    }

    private void handleError(IOException e, final IResponseCallback callback) {
        if (callback != null) {
            final EaphoneOkHttpException httpException = new EaphoneOkHttpException();
            httpException.setE(e);
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onFail(httpException);
                }
            });

        }
    }

    @Override
    public void cancel(String tag) {
        if (client != null) {
            for (Call call : client.dispatcher().queuedCalls()) {
                if (call.request().tag().equals(tag)) {
                    call.cancel();
                }
            }
        }
    }
}
