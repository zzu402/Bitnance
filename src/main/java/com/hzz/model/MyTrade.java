package com.hzz.model;

import com.hzz.common.dao.annotation.Column;
import com.hzz.common.dao.annotation.Table;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/7
 */
@Table("bitcon_my_trade")
public class MyTrade {
    @Column(pk=true)
    private String id;
    @Column
    private String price;
    @Column
    private String qty;
    @Column
    private Long time;
    @Column
    private String isBestMatch;
    @Column
    private String orderId;
    @Column
    private String commission;
    @Column
    private String commissionAsset;
    @Column
    private String isMaker;
    @Column
    private String isBuyer;

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

    public String getBestMatch() {
        return isBestMatch;
    }

    public void setBestMatch(String bestMatch) {
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

    public String getMaker() {
        return isMaker;
    }

    public void setMaker(String maker) {
        isMaker = maker;
    }

    public String getBuyer() {
        return isBuyer;
    }

    public void setBuyer(String buyer) {
        isBuyer = buyer;
    }
}
