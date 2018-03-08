package com.hzz.common.dao;

import com.hzz.exception.CommonException;
import com.hzz.utils.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;

import java.util.List;

/**
 * Created by hongshuiqiao on 2017/8/17.
 */
public class ConditionOp implements  Condition {
    private OpType opType;
    private String column;
    private Object value;

    public ConditionOp(String column, OpType opType, Object value) {
        this.opType = opType;
        this.column = column;
        this.value = value;
    }

    public OpType getOpType() {
        return opType;
    }

    public void setOpType(OpType opType) {
        this.opType = opType;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String parseSql(JdbcTemplate jdbcTemplate, String table, List<SqlParameter> args) throws CommonException {
        if(null==value)
            return null;
        StringBuilder builder = new StringBuilder();
        if(column.contains(".")){
            builder.append(column);
        }else{
            builder.append(DaoHelper.quote(column));
        }
        builder.append(" ");
        builder.append(opType.getOp());
        if(StringUtil.isBlank(table) && !(value instanceof SqlParameterValue)){
            builder.append(" ");
            if (value instanceof String) {
                builder.append("\"");
                builder.append(value);
                builder.append("\"");
            }else{
                builder.append(value);
            }
        }else{
            builder.append(" ?");
            if(value instanceof SqlParameterValue){
                args.add((SqlParameterValue) value);
            }else{
                DaoHelper.addValueToArgs(jdbcTemplate, args, table, column, value);
            }
        }
        return builder.toString();
    }
}
