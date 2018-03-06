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
    public static Double valueOf(String str){
        if (str!=null&&!str.equals(""))
            try {
                return  Double.valueOf(str);
            }catch (Exception e){
                return  null;
            }
        return  null;
    }

}
