package com.hzz.model;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/7
 */
public class Trade {
    private String id;
    private String price;
    private String qty;
    private Long time;
    private Boolean isBestMatch;
    private Boolean isBuyerMaker;

    @Override
    public String toString() {
        return "Trade{" +
                "id='" + id + '\'' +
                ", price='" + price + '\'' +
                ", qty='" + qty + '\'' +
                ", time=" + time +
                ", isBestMatch=" + isBestMatch +
                ", isBuyerMaker=" + isBuyerMaker +
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

    public Boolean getBuyerMaker() {
        return isBuyerMaker;
    }

    public void setBuyerMaker(Boolean buyerMaker) {
        isBuyerMaker = buyerMaker;
    }
}
