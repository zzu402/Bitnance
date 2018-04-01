package com.hzz.utils;

import com.hzz.common.dao.ModelDao;
import com.hzz.ui.AbstractUI;
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
    private static ModelDao dao;
    public static DriverManagerDataSource setDataSource(){
        PropertiesUtils.loadProps(PropertiesUtils.getUserDir());
        String url="jdbc:mysql://localhost:"+PropertiesUtils.getString("port","3306")+"/"+PropertiesUtils.getString("dbName","bitcon")+"?useUnicode=true&characterEncoding=UTF-8";
        source= new DriverManagerDataSource();
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setUrl(url);
        source.setUsername(PropertiesUtils.getString("userName","root"));
        source.setPassword(PropertiesUtils.getString("password","root"));
        return  source;
    }

    public static JdbcTemplate getTemplate(){
        if(source==null)
            synchronized (DaoUtils.class) {
                if(source==null)
                    setDataSource();
            }
        if(template==null)
            synchronized (DaoUtils.class) {
                if(template==null) {
                    template = new JdbcTemplate(source);
                }
            }
        return template;
    }

    public static ModelDao getDao(JdbcTemplate jdbcTemplate){
        if(dao==null) {
            synchronized (DaoUtils.class) {
                if(dao==null) {
                    dao = new ModelDao();
                    dao.setJdbcTemplate(jdbcTemplate);
                }
            }
        }
        return  dao;

    }

    public static void DBError(AbstractUI subWindow){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(subWindow!=null)
                    subWindow.frame.setVisible(true);
            }
        }).start();

    }

}
