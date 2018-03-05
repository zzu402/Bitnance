package com.hzz.model;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/5
 */
public class Price {
    private String symbol;
    private Double price;

    @Override
    public String toString() {
        return "Price{" +
                "symbol='" + symbol + '\'' +
                ", price=" + price +
                '}';
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
