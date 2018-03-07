package com.hzz.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/7
 */
public class ConnectionUtils {
    private static DataSource ds = new ComboPooledDataSource("mysqlConnection");
    public static Connection getConnection() {
        DataSource ds = getDataSource();
        try {
            Connection connection = ds.getConnection();
            return connection;
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
    }
    public static DataSource getDataSource(){
        return ds;
    }
}
