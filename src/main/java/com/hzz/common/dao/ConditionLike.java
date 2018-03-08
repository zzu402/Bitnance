package com.hzz.common.dao;

import com.hzz.utils.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;

import java.util.List;

/**
 * Created by hongshuiqiao on 2017/8/17.
 */
public class ConditionLike implements Condition {
    private String column;
    private boolean notLike;
    private String value;
    private LikeType type;

    public ConditionLike(String column, boolean notLike, LikeType type, String value) {
        this.column = column;
        this.notLike = notLike;
        this.type = type;
        this.value = value;
    }

    public ConditionLike(String column, String value) {
        this(column, false, LikeType.BOTH, value);
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public boolean isNotLike() {
        return notLike;
    }

    public void setNotLike(boolean notLike) {
        this.notLike = notLike;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String parseSql(JdbcTemplate jdbcTemplate, String table, List<SqlParameter> args) {
        if(StringUtil.isBlank(value))
            return null;
        StringBuilder sqlBuilder = new StringBuilder();
        if(column.contains(".")){
            sqlBuilder.append(column);
        }else{
            sqlBuilder.append(DaoHelper.quote(column));
        }
        if (notLike) {
            sqlBuilder.append(" NOT");
        }
        sqlBuilder.append(" LIKE ");

        sqlBuilder.append("\"");
        if (type == LikeType.BOTH || type == LikeType.LEFT) {
            sqlBuilder.append("%");
        }
        sqlBuilder.append(value);
        if (type == LikeType.BOTH || type == LikeType.RIGHT) {
            sqlBuilder.append("%");
        }
        sqlBuilder.append("\"");

//        sqlBuilder.append("CONCAT(");
//        if (type == LikeType.BOTH || type == LikeType.LEFT) {
//            sqlBuilder.append("'%',");
//        }
//        sqlBuilder.append("?");
//        if (type == LikeType.BOTH || type == LikeType.RIGHT) {
//            sqlBuilder.append(",'%'");
//        }
//        sqlBuilder.append(")");
//        DaoHelper.addValueToArgs(jdbcTemplate, args, tableModel.table, column, value);
        return sqlBuilder.toString();
    }
}
