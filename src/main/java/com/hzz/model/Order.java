package com.hzz.model;

import com.hzz.common.dao.AbstractModel;
import com.hzz.common.dao.annotation.Column;
import com.hzz.common.dao.annotation.Table;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/7
 */
@Table("bitcon_order")
public class Order extends AbstractModel<Order> {
    @Column(pk=true)
    private Long id;
    @Column
    private String symbol;
    @Column
    private String orderId;
    @Column
    private String clientOrderId;
    @Column
    private String price;
    @Column
    private String origQty;
    @Column
    private String executedQty;
    @Column
    private String status;
    @Column
    private String timeInForce;
    @Column
    private String type;
    @Column
    private String side;
    @Column
    private String stopPrice;
    @Column
    private String icebergQty;
    @Column
    private Long time;
    @Column
    private String isWorking;

    @Override
    public String toString() {
        return "Order{" +
                "symbol='" + symbol + '\'' +
                ", orderId='" + orderId + '\'' +
                ", clientOrderId='" + clientOrderId + '\'' +
                ", price='" + price + '\'' +
                ", origQty='" + origQty + '\'' +
                ", executedQty='" + executedQty + '\'' +
                ", status='" + status + '\'' +
                ", timeInForce='" + timeInForce + '\'' +
                ", type='" + type + '\'' +
                ", side='" + side + '\'' +
                ", stopPrice='" + stopPrice + '\'' +
                ", icebergQty='" + icebergQty + '\'' +
                ", time=" + time +
                ", isWorking=" + isWorking +
                '}';
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOrigQty() {
        return origQty;
    }

    public void setOrigQty(String origQty) {
        this.origQty = origQty;
    }

    public String getExecutedQty() {
        return executedQty;
    }

    public void setExecutedQty(String executedQty) {
        this.executedQty = executedQty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(String stopPrice) {
        this.stopPrice = stopPrice;
    }

    public String getIcebergQty() {
        return icebergQty;
    }

    public void setIcebergQty(String icebergQty) {
        this.icebergQty = icebergQty;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getWorking() {
        return isWorking;
    }

    public void setWorking(String working) {
        isWorking = working;
    }
}
