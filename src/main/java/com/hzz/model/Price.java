package com.hzz.model;

import com.hzz.common.dao.AbstractModel;
import com.hzz.common.dao.annotation.Column;
import com.hzz.common.dao.annotation.Table;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/5
 */
@Table("bitcon_price")
public class Price extends AbstractModel<Price> {
    @Column(pk=true)
    private Long id;
    @Column
    private String symbol;
    @Column
    private String price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Price{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", price='" + price + '\'' +
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
