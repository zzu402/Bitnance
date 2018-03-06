package com.hzz.model;

import java.util.List;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/6
 */
public class Symbol {

    private String symbol;
    private String status;
    private String baseAsset;
    private String quoteAsset;
    private Long baseAssetPrecision;
    private Long quoteAssetPrecision;
    private List<String> orderTypes;
    private Boolean icebergAllowed;
    private List<Filter>filters;

    @Override
    public String toString() {
        return "Symbol{" +
                "symbol='" + symbol + '\'' +
                ", status='" + status + '\'' +
                ", baseAsset='" + baseAsset + '\'' +
                ", quoteAsset='" + quoteAsset + '\'' +
                ", baseAssetPrecision=" + baseAssetPrecision +
                ", quoteAssetPrecision=" + quoteAssetPrecision +
                ", orderTypes=" + orderTypes +
                ", icebergAllowed=" + icebergAllowed +
                ", filters=" + filters +
                '}';
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBaseAsset() {
        return baseAsset;
    }

    public void setBaseAsset(String baseAsset) {
        this.baseAsset = baseAsset;
    }

    public String getQuoteAsset() {
        return quoteAsset;
    }

    public void setQuoteAsset(String quoteAsset) {
        this.quoteAsset = quoteAsset;
    }

    public Long getBaseAssetPrecision() {
        return baseAssetPrecision;
    }

    public void setBaseAssetPrecision(Long baseAssetPrecision) {
        this.baseAssetPrecision = baseAssetPrecision;
    }

    public Long getQuoteAssetPrecision() {
        return quoteAssetPrecision;
    }

    public void setQuoteAssetPrecision(Long quoteAssetPrecision) {
        this.quoteAssetPrecision = quoteAssetPrecision;
    }

    public List<String> getOrderTypes() {
        return orderTypes;
    }

    public void setOrderTypes(List<String> orderTypes) {
        this.orderTypes = orderTypes;
    }

    public Boolean getIcebergAllowed() {
        return icebergAllowed;
    }

    public void setIcebergAllowed(Boolean icebergAllowed) {
        this.icebergAllowed = icebergAllowed;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }
}
