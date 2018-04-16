package com.hzz.utils;

import java.text.NumberFormat;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/5
 */
public class NumberUtils {

    public static Boolean isEquals(Double d1,Double d2){
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


    public static String getNumberStr(Double d){
        NumberFormat nf= NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        return  nf.format(d);
    }

    public static String getNumberStr(Long d){
        NumberFormat nf= NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        return  nf.format(d);
    }

}
