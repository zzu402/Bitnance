package com.hzz.utils;

import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.main.Api;
import com.hzz.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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

    public static boolean checkDBConfigAndKeys(){
        File file=new File(String.format("%s//config.properties",System.getProperty("user.dir")));
        if(!file.exists())
            return false;
        PropertiesUtils.loadProps(PropertiesUtils.getUserDir());
        if(StringUtil.isBlank(PropertiesUtils.getString("userName",""))||StringUtil.isBlank(PropertiesUtils.getString("password","")))
            return false;
        ModelDao modelDao= DaoUtils.getDao(DaoUtils.getTemplate());
        try {
            List list=modelDao.select(new User());
            if(list.isEmpty()) {
                return false;
            }
            User user= (User) list.get(0);
            String apiKey=user.getApi_key();
            String secretKey=user.getSecret_key();
            if(StringUtil.isBlank(secretKey)||StringUtil.isBlank(apiKey)) {
                return false;
            }
            return  true;

        } catch (CommonException e) {
            return  false;
        }


    }


}
