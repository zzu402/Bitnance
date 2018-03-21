package com.hzz.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/8
 */
public class LoggerUtils {
    public static Logger getLogger(Class clazz){
        return  LoggerFactory.getLogger(clazz);
    }
    public static void info(Logger logger,String message){
        logger.info(message);
    }
    public static void error(Logger logger,String error){
        logger.error(error);
    }
    public static void warn(Logger logger,String message){
        logger.warn(message);
    }
    public static void debug(Logger logger,String message){
        logger.debug(message);
    }
    public static void trace(Logger logger,String message){
        logger.trace(message);
    }
}
