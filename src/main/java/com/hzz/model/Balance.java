package com.hzz.model;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/5
 */
public class Balance {
    private String asset;
    private String free;
    private Double locked;

    @Override
    public String toString() {
        return "Balance{" +
                "asset='" + asset + '\'' +
                ", free=" + free +
                ", locked=" + locked +
                '}';
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

    public Double getLocked() {
        return locked;
    }

    public void setLocked(Double locked) {
        this.locked = locked;
    }
}
