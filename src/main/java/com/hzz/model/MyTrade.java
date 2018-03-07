package com.hzz.model;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/7
 */
public class MyTrade {

    private String id;
    private String price;
    private String qty;
    private Long time;
    private Boolean isBestMatch;
    private String orderId;
    private String commission;
    private String commissionAsset;
    private Boolean isMaker;
    private Boolean isBuyer;

    @Override
    public String toString() {
        return "MyTrade{" +
                "id='" + id + '\'' +
                ", price='" + price + '\'' +
                ", qty='" + qty + '\'' +
                ", time=" + time +
                ", isBestMatch=" + isBestMatch +
                ", orderId='" + orderId + '\'' +
                ", commission='" + commission + '\'' +
                ", commissionAsset='" + commissionAsset + '\'' +
                ", isMaker=" + isMaker +
                ", isBuyer=" + isBuyer +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Boolean getBestMatch() {
        return isBestMatch;
    }

    public void setBestMatch(Boolean bestMatch) {
        isBestMatch = bestMatch;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getCommissionAsset() {
        return commissionAsset;
    }

    public void setCommissionAsset(String commissionAsset) {
        this.commissionAsset = commissionAsset;
    }

    public Boolean getMaker() {
        return isMaker;
    }

    public void setMaker(Boolean maker) {
        isMaker = maker;
    }

    public Boolean getBuyer() {
        return isBuyer;
    }

    public void setBuyer(Boolean buyer) {
        isBuyer = buyer;
    }
}
