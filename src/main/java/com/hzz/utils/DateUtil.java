package com.hzz.utils;

import com.hzz.exception.CommonException;
import com.hzz.exception.CommonExceptionHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * DateFormat不是线程安全的，因此通过此工具类并每个线程准备一个副本。
 * Created by hongshuiqiao on 2017/6/14.
 */
public class DateUtil {
    private static ThreadLocal<Map<String, DateFormat>> threadLocal = new ThreadLocal<Map<String, DateFormat>>() {
        /**
         * 获取线程的变量副本，如果不覆盖initialValue，第一次get返回null
         * 需要初始化一个实例，并set到threadLocal中
         */
        protected Map<String, DateFormat> initialValue() {
            return new HashMap<String, DateFormat>();
        }
    };

    public static DateFormat getDateFormat(String pattern) {
        if (StringUtil.isBlank(pattern))
            return null;

        Map<String, DateFormat> map = threadLocal.get();
        DateFormat dateFormat = map.get(pattern);
        if (null == dateFormat) {
            dateFormat = new SimpleDateFormat(pattern);
            map.put(pattern, dateFormat);
        }
        return dateFormat;
    }

    public static String format(String pattern, Date date) {
        return getDateFormat(pattern).format(date);
    }

    public static Date parse(String pattern, String date) throws CommonException {
        try {
            return getDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            throw CommonExceptionHelper.commonException("日期解析出错",e);
        }
    }


    //获取昨天的开始时间
    public static Date getBeginDayOfYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    //获取昨天的结束时间
    public static Date getEndDayOfYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    //获取上周的开始时间,(记周日为第一天开始)
    public static Date getBeginDayOfLastWeek() {
        Calendar cal = Calendar.getInstance();
        //设置时间成本周第一天(周日)
        cal.set(Calendar.DAY_OF_WEEK,1);
        //上周日时间
        cal.add(Calendar.DATE, -7);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    //获取上周的结束时间,(记周日为第一天开始)
    public static Date getEndDayOfLastWeek() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_WEEK,1);
        //上周六时间
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    //获取上月的开始时间
    public static Date getBeginDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        int lastMonthMaxDay=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    //获取上月的结束时间
    public static Date getEndDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        int lastMonthMaxDay=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), lastMonthMaxDay, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
    //获取上年的开始时间
    public static Date getBeginDayOfLastYear() {
        Calendar cal = Calendar.getInstance();
        Date date=new Date();
        cal.setTime(date);
        // cal.set
        cal.set(Calendar.YEAR,cal.get(Calendar.YEAR)-1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }
    //获取上年的结束时间
    public static Date getEndDayOfLastYear() {
        Calendar cal = Calendar.getInstance();
        Date date=new Date();
        cal.setTime(date);
        // cal.set
        cal.set(Calendar.YEAR,cal.get(Calendar.YEAR)-1);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DATE, 31);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        return cal.getTime();
    }

    public static Date getDateBegin(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getWeekBegin(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK,1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getMonthBegin(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getYearBegin(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
