package com.hzz.model;

import com.hzz.common.dao.AbstractModel;
import com.hzz.common.dao.annotation.Column;
import com.hzz.common.dao.annotation.Table;

import java.util.List;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/5
 */
@Table("bitcon_account")
public class Account extends AbstractModel<Account> {
    @Column(pk=true)
    private Long id;
    private List<Balance> balances;
    @Column
    private String makerCommission;
    @Column
    private String buyerCommission;
    @Column
    private String canWithdraw;
    @Column
    private String sellerCommission;
    @Column
    private Long updateTime;
    @Column
    private String canDeposit;
    @Column
    private String takerCommission;
    @Column
    private String canTrade;
    @Column
    private Long moneyCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balances=" + balances +
                ", makerCommission='" + makerCommission + '\'' +
                ", buyerCommission='" + buyerCommission + '\'' +
                ", canWithdraw='" + canWithdraw + '\'' +
                ", sellerCommission='" + sellerCommission + '\'' +
                ", updateTime=" + updateTime +
                ", canDeposit='" + canDeposit + '\'' +
                ", takerCommission='" + takerCommission + '\'' +
                ", canTrade='" + canTrade + '\'' +
                '}';
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    public String getMakerCommission() {
        return makerCommission;
    }

    public void setMakerCommission(String makerCommission) {
        this.makerCommission = makerCommission;
    }

    public String getBuyerCommission() {
        return buyerCommission;
    }

    public void setBuyerCommission(String buyerCommission) {
        this.buyerCommission = buyerCommission;
    }

    public String getCanWithdraw() {
        return canWithdraw;
    }

    public void setCanWithdraw(String canWithdraw) {
        this.canWithdraw = canWithdraw;
    }

    public String getSellerCommission() {
        return sellerCommission;
    }

    public void setSellerCommission(String sellerCommission) {
        this.sellerCommission = sellerCommission;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getCanDeposit() {
        return canDeposit;
    }

    public void setCanDeposit(String canDeposit) {
        this.canDeposit = canDeposit;
    }

    public String getTakerCommission() {
        return takerCommission;
    }

    public void setTakerCommission(String takerCommission) {
        this.takerCommission = takerCommission;
    }

    public String getCanTrade() {
        return canTrade;
    }

    public void setCanTrade(String canTrade) {
        this.canTrade = canTrade;
    }

    public Long getMoneyCount() {
        return moneyCount;
    }

    public void setMoneyCount(Long moneyCount) {
        this.moneyCount = moneyCount;
    }
}
