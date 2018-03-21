package com.hzz.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class PropertiesUtils {
    private static Properties properties;
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
    /**
     * 加载属性文件
     *
     * @param file 文件
     * @return
     */
    public static Properties loadProps(File file) {
        properties = new Properties();
        try {
            logger.info("加载属性文件开始");
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            properties.load(in);
            logger.info("加载属性文件结束");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("加载属性文件时候失败",e);
        }
        return properties;
    }

    public static File getUserDir() {
        File file=new File(String.format("%s//config.properties",System.getProperty("user.dir")));
        if(!file.exists())
            try {
                logger.info("创建属性文件目录开始");
                file.createNewFile();
                logger.info("创建属性文件目录结束");
            } catch (IOException e) {
                logger.error("创建属性文件目录失败",e);
            }
        return  file;
    }

    /**
     * 读取配置文件
     *
     * @return
     */
    public static String getString(String key,String defaultValue) {
        String value= properties.getProperty(key);
        if(StringUtil.isBlank(value))
            return defaultValue;
        return value;
    }

    /**
     * 更新配置文件
     *
     * @return
     */
    public static void updateProperty(File file, String keyNname, String keyValue) {
        Properties pps=new Properties();
        try {
            logger.info("更新属性文件开始");
            InputStream in = new FileInputStream(file);
            pps.load(in);
            //调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
            //强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
            OutputStream out = new FileOutputStream(file);
            pps.setProperty(keyNname, keyValue);
            //以适合使用 load 方法加载到 Properties 表中的格式，
            //将此 Properties 表中的属性列表（键和元素对）写入输出流
            pps.store(out, "Update " + keyNname + " name");
            logger.info("更新属性文件结束");
        } catch (Exception e) {
            logger.error("更新属性文件失败",e);

        }
    }


}