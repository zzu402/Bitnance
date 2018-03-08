package com.hzz.model;

import com.hzz.common.dao.annotation.Column;
import com.hzz.common.dao.annotation.Table;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/5
 */
@Table("bitcon_balance")
public class Balance {
    @Column(pk=true)
    private Long id;
    @Column
    private Long accountId;
    @Column
    private String asset;
    @Column
    private String free;
    @Column
    private String locked;
    @Override
    public String toString() {
        return "Balance{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", asset='" + asset + '\'' +
                ", free='" + free + '\'' +
                ", locked='" + locked + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }
}
