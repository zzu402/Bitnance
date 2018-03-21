package com.hzz.utils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.File;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/21
 */
public class DaoUtils {
    private  static JdbcTemplate template;
    private static DriverManagerDataSource source;
    public static DriverManagerDataSource setDataSource(File configFile){
        source= new DriverManagerDataSource();
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setUrl("jdbc:mysql://localhost:3306/bitcon?useUnicode=true&characterEncoding=UTF-8");
        source.setUsername("root");
        source.setPassword("huangzhenzong");
        return  source;
    }

    public static JdbcTemplate getTemplate(){
        setDataSource(null);
        template=new JdbcTemplate(source);
        return template;
    }

}
