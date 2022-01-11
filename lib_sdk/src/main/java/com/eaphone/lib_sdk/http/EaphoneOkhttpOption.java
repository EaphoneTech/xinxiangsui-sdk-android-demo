package com.eaphone.lib_sdk.http;

import java.util.Map;


public class EaphoneOkhttpOption {
    private String mUrl;
    private String mTag;
    private Map<String, String> mHeaders;

    public EaphoneOkhttpOption(String mTag) {
        this.mTag = mTag;
    }

    public String getTag() {
        return mTag;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public static final class Builder {
        public String mTag;
        public Map<String, String> mHeaders;
        public String mUrl;

        public Builder setTag(String mTag) {
            this.mTag = mTag;
            return this;
        }

        public Builder setHeaders(Map<String, String> mHeaders) {
            this.mHeaders = mHeaders;
            return this;
        }

        public Builder setUrl(String mUrl) {
            this.mUrl = mUrl;
            return this;
        }

        public EaphoneOkhttpOption build() {
            EaphoneOkhttpOption option = new EaphoneOkhttpOption(mTag);
            option.mHeaders = mHeaders;
            option.mUrl = mUrl;
            return option;
        }
    }

}