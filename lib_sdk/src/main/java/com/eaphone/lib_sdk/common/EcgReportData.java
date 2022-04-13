package com.eaphone.lib_sdk.common;


import com.eaphone.lib_sdk.http.BaseResponseStatusEntity;

public class EcgReportData extends BaseResponseStatusEntity {

    /**
     *数据id
     */
    private String dataid;
    /**
     *如厕开始时间
     */
    private String begin_time;
    /**
     *如厕结束时间
     */
    private String end_time;
    /**
     *总心博
     */
    private int heart_beats;
    /**
     *平均心率
     */
    private int heart_rate;
    /**
     *最快心率
     */
    private int heart_rate_max;
    /**
     *最慢心率
     */
    private int heart_rate_min;
    /**
     *腿温
     */
    private double leg_temperature;
    /**
     *呼吸率
     */
    private int respiration;
    /**
     *如厕时长
     */
    private long duration;


    public String getDataid() {
        return dataid;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getHeart_beats() {
        return heart_beats;
    }

    public void setHeart_beats(int heart_beats) {
        this.heart_beats = heart_beats;
    }

    public int getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(int heart_rate) {
        this.heart_rate = heart_rate;
    }

    public int getHeart_rate_max() {
        return heart_rate_max;
    }

    public void setHeart_rate_max(int heart_rate_max) {
        this.heart_rate_max = heart_rate_max;
    }

    public int getHeart_rate_min() {
        return heart_rate_min;
    }

    public void setHeart_rate_min(int heart_rate_min) {
        this.heart_rate_min = heart_rate_min;
    }

    public double getLeg_temperature() {
        return leg_temperature;
    }

    public void setLeg_temperature(double leg_temperature) {
        this.leg_temperature = leg_temperature;
    }

    public int getRespiration() {
        return respiration;
    }

    public void setRespiration(int respiration) {
        this.respiration = respiration;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "EcgReportData{" +
                "dataid='" + dataid + '\'' +
                ", begin_time='" + begin_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", heart_beats=" + heart_beats +
                ", heart_rate=" + heart_rate +
                ", heart_rate_max=" + heart_rate_max +
                ", heart_rate_min=" + heart_rate_min +
                ", leg_temperature=" + leg_temperature +
                ", respiration=" + respiration +
                ", duration=" + duration +
                '}';
    }
}
