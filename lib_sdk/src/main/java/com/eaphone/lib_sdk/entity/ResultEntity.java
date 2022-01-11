package com.eaphone.lib_sdk.entity;

public class ResultEntity {
    private String _n;
    private int _v;
    private int quality;
    private int HeartRate;
    private int Sample_ECG;
    private int Sample_PPG;

    public String get_n() {
        return _n;
    }

    public void set_n(String _n) {
        this._n = _n;
    }

    public int get_v() {
        return _v;
    }

    public void set_v(int _v) {
        this._v = _v;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public int getHeartRate() {
        return HeartRate;
    }

    public void setHeartRate(int heartRate) {
        HeartRate = heartRate;
    }

    public int getSample_ECG() {
        return Sample_ECG;
    }

    public void setSample_ECG(int sample_ECG) {
        Sample_ECG = sample_ECG;
    }

    public int getSample_PPG() {
        return Sample_PPG;
    }

    public void setSample_PPG(int sample_PPG) {
        Sample_PPG = sample_PPG;
    }
}
