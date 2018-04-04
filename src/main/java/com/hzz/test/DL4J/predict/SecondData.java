package com.hzz.test.DL4J.predict;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/4/4
 */
public class SecondData {
    //价格
    private double price;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "SecondData{" +
                "price=" + price +
                '}';
    }
}
