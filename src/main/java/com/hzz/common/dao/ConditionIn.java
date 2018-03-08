package com.hzz.common.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by hongshuiqiao on 2017/8/17.
 */
public class ConditionIn implements Condition {
    private String column;
    private boolean notIn;
    private Object value;

    public ConditionIn(String column, Object value) {
        this(column, false, value);
    }

    public ConditionIn(String column, boolean notIn, Object value) {
        this.column = column;
        this.notIn = notIn;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public boolean isNotIn() {
        return notIn;
    }

    public void setNotIn(boolean notIn) {
        this.notIn = notIn;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String parseSql(JdbcTemplate jdbcTemplate, String table, List<SqlParameter> args) {
        if(null==value)
            return null;
        StringBuilder sqlBuilder = new StringBuilder();
        if(column.contains(".")){
            sqlBuilder.append(column);
        }else{
            sqlBuilder.append(DaoHelper.quote(column));
        }
        if (notIn) {
            sqlBuilder.append(" NOT");
        }
        sqlBuilder.append(" IN (");
        List valueList = null;
        if (value instanceof Collection) {
            valueList = new ArrayList((Collection) value);
        } else if (value.getClass().isArray()) {
            valueList = Arrays.asList((Object[]) value);
        } else {
            valueList = new ArrayList();
            valueList.add(value);
        }
        boolean flag = false;
        for (Object value : valueList) {
            if (flag) {
                sqlBuilder.append(",");
            }

            if (value instanceof String) {
                sqlBuilder.append(String.format("\"%s\"",(String) value));
            }else{
                sqlBuilder.append(String.format("%s", value));
            }
//            sqlBuilder.append("?");
//            DaoHelper.addValueToArgs(jdbcTemplate, args, tableModel.table, column, value);
            flag = true;
        }
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }
}
