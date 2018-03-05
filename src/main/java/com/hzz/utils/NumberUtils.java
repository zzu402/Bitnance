package com.hzz.utils;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/5
 */
public class NumberUtils {

    public static boolean isEquals(Double d1,Double d2){
       return Double.doubleToLongBits(d1)!=Double.doubleToLongBits(d2);
    }


}
