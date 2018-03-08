package com.hzz.common.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hongshuiqiao on 2017/8/17.
 */
public class JoinModel {
    private Class<? extends AbstractModel> joinModel;
    private String aliasName;
    private ConditionAnd on = new ConditionAnd();
    private List<String> columns = new ArrayList<>();

    public Class<? extends AbstractModel> getJoinModel() {
        return joinModel;
    }

    public void setJoinModel(Class<? extends AbstractModel> joinModel) {
        this.joinModel = joinModel;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public ConditionAnd on() {
        return on;
    }

    public List<String> columns() {
        return columns;
    }
}
