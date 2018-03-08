package com.hzz.common.dao;

import com.hzz.exception.CommonException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;

import java.util.List;

/**
 * Created by hongshuiqiao on 2017/8/17.
 */
public interface Condition {

    String parseSql(JdbcTemplate jdbcTemplate, String table, List<SqlParameter> args) throws CommonException;

}
