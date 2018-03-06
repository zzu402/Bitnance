package com.hzz.model;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/6
 */
public class SellOrBuyInfo {

    private String symbol;
    private String side;
    private String executedQty;
    private Long orderId;
    private String price;
    private String origQty;
    private String clientOrderId;
    private String transacTime;
    private String type;
    private String timeInForce;
    private String status;

    @Override
    public String toString() {
        return "SellOrBuyInfo{" +
                "symbol='" + symbol + '\'' +
                ", side='" + side + '\'' +
                ", executedQty='" + executedQty + '\'' +
                ", orderId=" + orderId +
                ", price='" + price + '\'' +
                ", origQty='" + origQty + '\'' +
                ", clientOrderId='" + clientOrderId + '\'' +
                ", transacTime='" + transacTime + '\'' +
                ", type='" + type + '\'' +
                ", timeInForce='" + timeInForce + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getExecutedQty() {
        return executedQty;
    }

    public void setExecutedQty(String executedQty) {
        this.executedQty = executedQty;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public String getTransacTime() {
        return transacTime;
    }

    public void setTransacTime(String transacTime) {
        this.transacTime = transacTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
