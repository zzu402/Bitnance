package com.hzz.service;

import com.hzz.utils.DBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/30
 */
public class JobService {
    private static Logger logger= LoggerFactory.getLogger(JobService.class);
    private  static  AccountService accountService = new AccountService();
    private  static  PriceService priceService = new PriceService();
    public static void doJob(){
        boolean isDoJob=false;
        while(!isDoJob) {
            TradeService tradeService = new TradeService();
            if (DBUtils.checkDBConfigAndKeys()) {
                logger.info("init Key start ...");
                tradeService.initKey();
                logger.info("init Key end ...");
                doSaveInfo();
                logger.info("get Data start ...");
                tradeService.doHm();
                logger.info("doHm start... ");
                isDoJob=true;
            } else {
                logger.info("check db or keys no set...");
                try {
                    Thread.sleep(2*60*1000);//每个2分钟检查一次，直到进入正常工作
                } catch (InterruptedException e) {
                    logger.error("线程休眠异常",e);
                }
            }
        }
    }

    private static void doSaveInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        accountService.saveAccount();
                        Thread.sleep(1*60*60*1000);
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
                        priceService.savePrice();
                        Thread.sleep(10000);//每隔10秒更新数据
                    } catch (InterruptedException e) {
                        logger.error("线程执行错误",e);
                    }
                }
            }
        }).start();
    }




}
