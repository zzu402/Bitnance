package com.hzz.model;

import com.hzz.common.dao.AbstractModel;
import com.hzz.common.dao.annotation.Column;
import com.hzz.common.dao.annotation.Table;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/7
 */
@Table("bitcon_trade")
public class Trade extends AbstractModel<Trade> {
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
    private String isBuyerMaker;

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

    public String getBestMatch() {
        return isBestMatch;
    }

    public void setBestMatch(String bestMatch) {
        isBestMatch = bestMatch;
    }

    public String getBuyerMaker() {
        return isBuyerMaker;
    }

    public void setBuyerMaker(String buyerMaker) {
        isBuyerMaker = buyerMaker;
    }
}
