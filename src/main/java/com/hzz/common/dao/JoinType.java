package com.hzz.common.dao;

/**
 * Created by hongshuiqiao on 2017/6/10.
 */
public enum JoinType {
    INNER("INNER JOIN"),
    LEFT("LEFT JOIN"),
    RIGHT("RIGHT JOIN"),
    FULL("FULL JOIN");

    private String type;

    private JoinType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
