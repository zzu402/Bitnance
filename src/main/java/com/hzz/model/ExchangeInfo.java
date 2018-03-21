package com.hzz.model;

import com.hzz.common.dao.AbstractModel;

import java.util.List;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/6
 */
public class ExchangeInfo  {

    private String timezone;
    private Long serverTime;
    private List<RateLimit> rateLimits;
    private List<String> exchangeFilters;
    private List<Symbol> symbols;

    @Override
    public String toString() {
        return "ExchangeInfo{" +
                "timezone='" + timezone + '\'' +
                ", serverTime=" + serverTime +
                ", rateLimits=" + rateLimits +
                ", exchangeFilters=" + exchangeFilters +
                ", symbols=" + symbols +
                '}';
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Long getServerTime() {
        return serverTime;
    }

    public void setServerTime(Long serverTime) {
        this.serverTime = serverTime;
    }

    public List<RateLimit> getRateLimits() {
        return rateLimits;
    }

    public void setRateLimits(List<RateLimit> rateLimits) {
        this.rateLimits = rateLimits;
    }

    public List<String> getExchangeFilters() {
        return exchangeFilters;
    }

    public void setExchangeFilters(List<String> exchangeFilters) {
        this.exchangeFilters = exchangeFilters;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<Symbol> symbols) {
        this.symbols = symbols;
    }
}
