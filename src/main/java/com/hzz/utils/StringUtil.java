package com.hzz.utils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by hongshuiqiao on 2017/6/14.
 */
public class StringUtil {
    private static final long T = 1024L*1024L*1024L*1024L;
    private static final long G = 1024L*1024L*1024L;
    private static final long M = 1024L*1024L;
    private static final long K = 1024L;

    public static String uuid(){
        return UUID.randomUUID().toString();
    }

    public static String simpleUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static boolean isBlank(String value){
        return null==value || "".equals(value.trim());
    }

    public static boolean equals(String str1, String str2, boolean nullAble){
        if(null==str1&& null==str2)
            return nullAble;
        if(null!=str1&&null!=str2)
            return str1.equals(str2);
        return false;
    }

    public static boolean equalsIgnoreCase(String str1, String str2, boolean nullAble){
        if(null==str1&& null==str2)
            return nullAble;
        if(null!=str1&&null!=str2)
            return str1.equalsIgnoreCase(str2);
        return false;
    }

    /**
     * 手机脱敏
     * @param phone
     * @return
     */
    public static String phoneDesensitization(String phone){
        return String.format("%s****%s",phone.substring(0,3), phone.substring(7,11));
    }

    public static String nameDesensitization(String realName) {
        if(StringUtil.isBlank(realName))
            return realName;
        return String.format("%s*%s",realName.substring(0, realName.length()-2), realName.substring(realName.length()-1));
    }

    public static String getNotNullString(String value){
        if(null == value)
            return "";
        return value;
    }

    public static String hexTimeStr(){
        return Long.toHexString(System.currentTimeMillis()).toUpperCase();
    }

    public static String checkUTF8ContentLength(String content, int maxLength) {
        String result = content;
        try {
            if(result == null){
                return null;
            }

            StringBuffer buffer = new StringBuffer();
            int length = 0;
            char[] charArray = result.toCharArray();
            for (char c : charArray) {
                if(c<=127){
                    length++;
                }else if(c<=2047){
                    length=length+2;
                }else if(c<=65535){
                    length=length+3;
                }else if(c<=2097151){
                    length=length+4;
                }else if(c<=67108863){
                    length=length+5;
                }else if(c<=2147483647){
                    length=length+6;
                }else{
                    length=length+6;
                }
                if(length<=(maxLength-3)){
                    buffer.append(c);
                }else{
                    buffer.append("...");
                    break;
                }
            }
            return buffer.toString();
        } catch (Exception e) {
            return result;
        }
    }

    /**
     * @param time
     * @return
     */
    public static String buildTime(long time) {
        long days = TimeUnit.DAYS.convert(time, TimeUnit.MILLISECONDS);
        long hours = TimeUnit.HOURS.convert(time-1000L*24*60*60*days, TimeUnit.MILLISECONDS);
        long minutes = TimeUnit.MINUTES.convert(time-1000L*24*60*60*days-1000L*60*60*hours, TimeUnit.MILLISECONDS);
        long seconds = TimeUnit.SECONDS.convert(time-1000L*24*60*60*days-1000L*60*60*hours-1000L*60*minutes, TimeUnit.MILLISECONDS);
        long milliSeconds = time-1000L*24*60*60*days-1000L*60*60*hours-1000L*60*minutes - 1000L*seconds;
        StringBuilder builder = new StringBuilder();
        if(days>0){
            builder.append(String.format("%s天", days));
        }
        if(hours>0 || (builder.length() > 0)){
            builder.append(String.format("%s小时", hours));
        }
        if(minutes>0 || (builder.length() > 0)){
            builder.append(String.format("%s分钟", minutes));
        }
        builder.append(String.format("%s秒%s毫秒", seconds, milliSeconds));
        return builder.toString();
    }

    public static String buildSize(long size) {
        long valueT = convert(size, T);
        long valueG = convert(size-valueT*T, G);
        long valueM = convert(size-valueT*T-valueG*G, M);
        long valueK = convert(size-valueT*T-valueG*G-valueM*M, K);
        long valueB = size-valueT*T-valueG*G-valueM*M-valueK*K;
        StringBuilder builder = new StringBuilder();
        if(valueT>0){
            builder.append(String.format("%sG", valueT));
        }
        if(valueG>0 || (builder.length() > 0)){
            builder.append(String.format("%sG", valueG));
        }
        if(valueM>0 || (builder.length() > 0)){
            builder.append(String.format("%sM", valueM));
        }
        if(valueK>0 || (builder.length() > 0)){
            builder.append(String.format("%sK", valueK));
        }
        if(builder.length() <= 0){
            builder.append(String.format("%s", ((valueB > 0)? valueB: 0)));
        }else{
            builder.append(String.format("%s", ((valueB > 0)? valueB: "")));
        }
        return builder.toString();
    }

    private static long convert(long size, long delta){
        return size/delta;
    }

    /**
     * @param money     单位为分
     * @return
     */
    public static String getWanMoney(long money) {
        return String.format("%.2f万",(money/1000000.0));
    }

    /**
     * @param money     单位为分
     * @return
     */
    public static String getYuanMoney(long money) {
        return String.format("%.2f",(money/100.0));
    }
}
