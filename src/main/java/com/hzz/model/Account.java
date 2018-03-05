package com.hzz.model;

import java.util.List;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/5
 */
public class Account {
  private List<Balance> balances;
  private Double makerCommission;
  private Double buyerCommission;
  private Boolean canWithdraw;
  private Double sellerCommission;
  private Long updateTime;
  private Boolean canDeposit;
  private Long takerCommission;
  private Boolean canTrade;

    @Override
    public String toString() {
        return "Account{" +
                "balances=" + balances +
                ", makerCommission=" + makerCommission +
                ", buyerCommission=" + buyerCommission +
                ", canWithdraw=" + canWithdraw +
                ", sellerCommission=" + sellerCommission +
                ", updateTime=" + updateTime +
                ", canDeposit=" + canDeposit +
                ", takerCommission=" + takerCommission +
                ", canTrade=" + canTrade +
                '}';
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    public Double getMakerCommission() {
        return makerCommission;
    }

    public void setMakerCommission(Double makerCommission) {
        this.makerCommission = makerCommission;
    }

    public Double getBuyerCommission() {
        return buyerCommission;
    }

    public void setBuyerCommission(Double buyerCommission) {
        this.buyerCommission = buyerCommission;
    }

    public Boolean getCanWithdraw() {
        return canWithdraw;
    }

    public void setCanWithdraw(Boolean canWithdraw) {
        this.canWithdraw = canWithdraw;
    }

    public Double getSellerCommission() {
        return sellerCommission;
    }

    public void setSellerCommission(Double sellerCommission) {
        this.sellerCommission = sellerCommission;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getCanDeposit() {
        return canDeposit;
    }

    public void setCanDeposit(Boolean canDeposit) {
        this.canDeposit = canDeposit;
    }

    public Long getTakerCommission() {
        return takerCommission;
    }

    public void setTakerCommission(Long takerCommission) {
        this.takerCommission = takerCommission;
    }

    public Boolean getCanTrade() {
        return canTrade;
    }

    public void setCanTrade(Boolean canTrade) {
        this.canTrade = canTrade;
    }
}
