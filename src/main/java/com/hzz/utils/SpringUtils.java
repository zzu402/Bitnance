package com.hzz.utils;

import com.hzz.App;
import com.hzz.common.dao.ModelDao;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/8
 */
public class SpringUtils {

    private static ConfigurableApplicationContext context;
    public static void init(Class clazz,String []args){
        context = SpringApplication.run(App.class, args);
    }
    public static ConfigurableApplicationContext getContext(){
        return context;
    }
    public static Object getBean(Class clazz){
        return context.getBean(clazz);
    }
}
