package com.hzz.common.dao;

/**
 * Created by hongshuiqiao on 2017/6/10.
 */
public enum OpType {
    EQ("="),
    NE("!="),
    GE(">="),
    LE("<="),
    GT(">"),
    LT("<");

    private String op;

    private OpType(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }
}
