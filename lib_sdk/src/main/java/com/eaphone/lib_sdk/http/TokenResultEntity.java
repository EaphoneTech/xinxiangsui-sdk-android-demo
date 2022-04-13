package com.eaphone.lib_sdk.http;


public class TokenResultEntity extends BaseResponseStatusEntity {
    private String access_token;
    private int expire_in;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpire_in() {
        return expire_in;
    }

    public void setExpire_in(int expire_in) {
        this.expire_in = expire_in;
    }

    @Override
    public String toString() {
        return "TokenResultEntity{" +
                "access_token='" + access_token + '\'' +
                ", expire_in=" + expire_in +
                '}';
    }
}
