package com.hzz.utils;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/7
 */
public class DBUtils<T> {
    private  QueryRunner qr=new QueryRunner(ConnectionUtils.getDataSource());
    private SqlUtils<T>sqlUtils=new SqlUtils<T>();
    public  int update(String sql,Object[]param) throws SQLException {
        return qr.update(sql,param);
    }
    public  int insert(T t)throws  SQLException{
        String sql=sqlUtils.createInsertSql(t);
        return qr.update(sql);
    }
    public  List<Object[]> query(String sql,Object[]param)throws  SQLException{
        return qr.query(sql,new ArrayListHandler(),param);
    }
    public  List<T> query(String sql,Object[]param,Class clazz)throws  SQLException{
        return qr.query(sql,new BeanListHandler<T>(clazz),param);
    }
    public static void main(String[]args){
        DBUtils<User> userDao=new DBUtils<User>();
        User user=new User();
        user.setName("hello");
        try {
            System.out.println(userDao.insert(user));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
