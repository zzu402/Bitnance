package com.hzz.utils;

import com.hzz.common.dao.ModelDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/26
 * 创建表，初始化
 */
public class DBUtils {
    private static Logger logger= LoggerFactory.getLogger(DBUtils.class);
    public static boolean initDatabase(){
        logger.info("init database start ...");
           InputStream inputStream = DBUtils.class.getClassLoader().getResourceAsStream("initTable.sql");
        try {
            int length=inputStream.available();
            byte[] sqlBytes=new byte[length];
            inputStream.read(sqlBytes);
            String sqls=new String(sqlBytes,"utf-8");
            String[]sqlArray=sqls.split("--------------------");
            for(int i=0;i<sqlArray.length;i++) {
                JdbcTemplate jdbcTemplate = DaoUtils.getTemplate();
                jdbcTemplate.update(sqlArray[i]);
            }
        } catch (Exception e) {
            logger.error("init database error",e);
            return false;
        }
        logger.info("init database end ...");
        return true;
    }


}
