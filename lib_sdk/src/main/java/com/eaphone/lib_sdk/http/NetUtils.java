package com.eaphone.lib_sdk.http;

import java.util.Map;
import java.util.Set;


public class NetUtils {

    static String appendUrl(String url, Map<String, String> map){
        if (map != null && map.size() > 0) {
            StringBuffer sb = new StringBuffer(url);
            sb.append("?");
            Set set = map.keySet();
            for (Object str : set) {
                sb.append(str).append("=").append(map.get(str)).append("&");
            }
            url = sb.substring(0, sb.length() - 1);
        }
        return url;
    }

    static EaphoneOkhttpOption checkNetworkOption(EaphoneOkhttpOption option, String url){
        EaphoneOkhttpOption.Builder mManbaOkhttpOption = new EaphoneOkhttpOption.Builder();
//        mManbaOkhttpOption.setHeaders();
        mManbaOkhttpOption.setTag("EaphoneAPI");
        mManbaOkhttpOption.setUrl(url);
        return mManbaOkhttpOption.build();

    }
}
