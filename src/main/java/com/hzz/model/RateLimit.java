package com.hzz.model;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/6
 */
public class RateLimit {

    private  String rateLimitType;
    private String interval;
    private Double limit;

    @Override
    public String toString() {
        return "RateLimit{" +
                "rateLimitType='" + rateLimitType + '\'' +
                ", interval='" + interval + '\'' +
                ", limit=" + limit +
                '}';
    }

    public String getRateLimitType() {
        return rateLimitType;
    }

    public void setRateLimitType(String rateLimitType) {
        this.rateLimitType = rateLimitType;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public Double getLimit() {
        return limit;
    }

    public void setLimit(Double limit) {
        this.limit = limit;
    }
}
