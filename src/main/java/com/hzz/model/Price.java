package com.hzz.model;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/5
 */
public class Price {
    private String symbol;
    private String price;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
