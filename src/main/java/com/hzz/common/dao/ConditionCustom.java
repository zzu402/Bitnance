package com.hzz.common.dao;
import com.hzz.utils.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import java.util.List;

/**
 * 用于添加复杂条件，如函数等
 * Created by hongshuiqiao on 2017/8/17.
 */
public class ConditionCustom implements Condition {
    private String value;

    public ConditionCustom(String value) {
        this.value = value;
    }

    @Override
    public String parseSql(JdbcTemplate jdbcTemplate, String table, List<SqlParameter> args) {
        if(StringUtil.isBlank(value))
            return null;
        return value;
    }
}
