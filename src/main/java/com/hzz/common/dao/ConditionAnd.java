package com.hzz.common.dao;

import com.hzz.exception.CommonException;
import com.hzz.utils.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hongshuiqiao on 2017/8/17.
 */
public class ConditionAnd implements Condition {
    private List<Condition> list = new ArrayList<>();

    public ConditionAnd add(Condition condition){
        list.add(condition);
        return this;
    }

    @Override
    public String parseSql(JdbcTemplate jdbcTemplate, String table, List<SqlParameter> args) throws CommonException {
        if(list.isEmpty())
            return null;
        StringBuilder sqlBuilder = new StringBuilder();
        for (Condition condition : list) {
            String sql = condition.parseSql(jdbcTemplate, table, args);
            if(StringUtil.isBlank(sql)){
               continue;
            }
            if(sqlBuilder.length()>0)
                sqlBuilder.append(" AND ");
            if(condition instanceof ConditionAnd || condition instanceof ConditionOr){
                sqlBuilder.append("(");
                sqlBuilder.append(sql);
                sqlBuilder.append(")");
            }else{
                sqlBuilder.append(sql);
            }
        }
        return sqlBuilder.toString();
    }
}
