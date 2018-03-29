package com.hzz.service;

import com.hzz.common.dao.ModelDao;
import com.hzz.constant.QueryConstant;
import com.hzz.exception.CommonException;
import com.hzz.main.Api;
import com.hzz.model.*;
import com.hzz.utils.DaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/27
 */
public class DataService {

    private Logger logger = LoggerFactory.getLogger(CommonService.class);
    private ModelDao modelDao = DaoUtils.getDao(DaoUtils.getTemplate());
    public static Api api = new Api();

    public void getAccount() {
        Account account = api.getAccountInfo();
        Map<String, Balance> map = api.getAllMoneyFree();
        Iterator it = map.entrySet().iterator();
        Double moneyCount = 0.0;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            Balance value = (Balance) entry.getValue();

            //KEY 是币名 ,查找价格均以BTC换算
            List prices = api.getMoneyPrice(key+ QueryConstant.DEFAULT_TRADE_CONVERT_CON);
            if (prices == null)
                continue;
            Price price = (Price) prices.get(0);
            Double currentPrice = Double.valueOf(price.getPrice());
            Double all = Double.valueOf(value.getFree()) + Double.valueOf(value.getLocked());
            moneyCount += currentPrice * all;
        }
        moneyCount = moneyCount * 100;
        account.setMoneyCount(moneyCount.longValue());
        account.setUpdateTime(System.currentTimeMillis()/1000);
        try {
            modelDao.insert(account);
        } catch (CommonException e) {
            logger.error("插入账户信息出错",e);
        }
    }


    public void getPrice(){
        List<Price> priceList=api.getMoneyPrice("");
        if(priceList==null)
            return;
        for(int i=0;i<priceList.size();i++){
            priceList.get(i).setCreateTime(System.currentTimeMillis()/1000);
        }
        try {
            modelDao.batchInsert(priceList);
        } catch (CommonException e) {
            logger.error("插入价格信息出错",e);
        }
    }

    public void getMyTrade(){
        List<MyTrade> myTradeList=api.getMyTrades("","",10);
        try {
            modelDao.batchInsert(myTradeList);
        } catch (CommonException e) {
            logger.error("插入我的历史交易信息出错",e);
        }
    }

    public void getOrder(){
        List<Order> orders=api.getMyOrders("","",10);
        try {
            modelDao.batchInsert(orders);
        } catch (CommonException e) {
            logger.error("插入我的历史订单信息出错",e);
        }
    }


    public void doSaveInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        getAccount();
                        Thread.sleep(24*60*60*1000);
                    } catch (InterruptedException e) {
                        logger.error("线程执行错误",e);
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        getPrice();
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        logger.error("线程执行错误",e);
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        getMyTrade();
                        getOrder();
                        Thread.sleep(12*60*60*1000);
                    } catch (InterruptedException e) {
                        logger.error("线程执行错误",e);
                    }
                }
            }
        }).start();
    }

}