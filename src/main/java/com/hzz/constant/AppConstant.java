package com.hzz.constant;

import com.hzz.utils.PropertiesUtils;

/**
 * @Author: huangzz
 * @Description: APP内所有可以设置的值
 * @Date :2018/4/8
 */
public class AppConstant {

    public static Double USDT_PRICE=6.2862;//美元兑换人民币
    public static Long  JOB_CHECK_SLEEP_TIME=2*60*1000L;
    public static Integer DISTANCE_THRESHOLD_MAX=18;//距离阈值，最小值距离当前价格的位置,一个单位代表10秒
    public static Integer DISTANCE_THRESHOLD_MIN=6;//距离阈值，最小值距离当前价格的位置,一个单位代表10秒
    public static Integer TIME_MARGIN_DATA_COUNT=30;//时间段内数据，1分钟6条，5分钟30条
    public static Long SAVE_ACCOUNT_MARGIN_TIME=1*60*60*1000L;//每个多长时间检查下账户
    public static Long SAVE_PRICE_MARGIN_TIME=10*1000L;//每隔多长时间保存下价格
    public static Long DO_BUY_TRADE_MARGIN_TIME=10*1000L;//每隔多长时间计算
    public static Long DO_SELL_TRADE_MARGIN_TIME=10*1000L;//每隔多长时间计算
    static {
        PropertiesUtils.loadProps(PropertiesUtils.getUserDir());
        DISTANCE_THRESHOLD_MAX=Integer.parseInt(PropertiesUtils.getString("tradeMarginMax","18"));
        DISTANCE_THRESHOLD_MIN=Integer.parseInt(PropertiesUtils.getString("tradeMarginMin","6"));
        TIME_MARGIN_DATA_COUNT=Integer.parseInt(PropertiesUtils.getString("tradeDataCount","30"));
        SAVE_ACCOUNT_MARGIN_TIME=Long.parseLong(PropertiesUtils.getString("saveAccountMarginTime","3600000"));
        SAVE_PRICE_MARGIN_TIME=Long.parseLong(PropertiesUtils.getString("savePriceMarginTime","10000"));
        DO_BUY_TRADE_MARGIN_TIME=Long.parseLong(PropertiesUtils.getString("doBuyMarginTime","10000"));
        DO_SELL_TRADE_MARGIN_TIME=Long.parseLong(PropertiesUtils.getString("doSellMarginTime","10000"));
    }







}
