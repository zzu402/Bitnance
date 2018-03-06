package com.hzz.model;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/6
 */
public class Filter {
    private String filterType;
    private String minPrice;
    private String maxPrice;
    private String tickSize;

    @Override
    public String toString() {
        return "Filter{" +
                "filterType='" + filterType + '\'' +
                ", minPrice='" + minPrice + '\'' +
                ", maxPrice='" + maxPrice + '\'' +
                ", tickSize='" + tickSize + '\'' +
                '}';
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getTickSize() {
        return tickSize;
    }

    public void setTickSize(String tickSize) {
        this.tickSize = tickSize;
    }
}
