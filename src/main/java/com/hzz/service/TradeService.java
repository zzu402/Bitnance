package com.hzz.service;

import com.hzz.common.dao.ModelDao;
import com.hzz.constant.QueryConstant;
import com.hzz.exception.CommonException;
import com.hzz.main.Api;
import com.hzz.model.Config;
import com.hzz.model.Price;
import com.hzz.model.SellOrBuyInfo;
import com.hzz.model.User;
import com.hzz.utils.DaoUtils;
import com.hzz.utils.EnDecryptUtil;
import com.hzz.utils.JsonMapper;
import com.hzz.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/27
 */
public class TradeService {

    private Logger logger= LoggerFactory.getLogger(TradeService.class);
    private Api api=new Api();
    private CommonService commonService=new CommonService();
    private MailService mailService=new MailService();
    /*
        手动处理交易
     */
    public void HmBuy() {
        List<Config> list1 = commonService.getConfigs(1, QueryConstant.CONFIG_SELECTED_HAND_TYPE, QueryConstant.CONFIG_BUY_TYPE);

        if (list1 != null) {//买入配置
            List list = commonService.getConfigSetList(QueryConstant.CONFIG_TYPE_PRE_BUY, 1);
            Config[] configs = commonService.sortConfigListByPriority(list);
            for (int i = 0; i < configs.length; i++) {
                Config config = configs[i];
                Map<String, String> configInfo = JsonMapper.nonDefaultMapper().fromJson(config.getConfigInfo(), Map.class);
                List<Price> prices = api.getMoneyPrice(config.getSymbol());
                if (prices == null)
                    continue;
                Price price = prices.get(0);
                Double currentPrice = Double.valueOf(price.getPrice());
                Double buyPrice = Double.valueOf(configInfo.get(QueryConstant.CONFIG_TYPE_PRE_BUY + "Price"));
                if (currentPrice.doubleValue() > buyPrice.doubleValue()) {//买入（价格比较，当前价格币设定价格小，就可以买入）
                    continue;
                }
                SellOrBuyInfo sellOrBuyInfo = api.buy(config.getSymbol(), Double.valueOf(configInfo.get("num")), price.getPrice());
                if (sellOrBuyInfo != null&&!StringUtil.isBlank(sellOrBuyInfo.getSide())) {//交易成功，记录日志，邮件通知
                    logger.info(String.format("交易成功，成功以%s价格买入%s",currentPrice,config.getSymbol()));
                    mailService.sendNotify(config.getSymbol(),price.getPrice(),1);
                }else {
                    logger.info("手动买入交易失败");
                }
            }
        }
    }


    public boolean initKey(){//配置ApiKey和
        ModelDao modelDao= DaoUtils.getDao(DaoUtils.getTemplate());
        try {
            logger.info("设置key开始");
            List list=modelDao.select(new User());
            if(list.isEmpty()) {
                logger.info("当前数据库没有存储两项KEY");
                return false;
            }
            User user= (User) list.get(0);
            String apiKey=user.getApi_key();
            String secretKey=user.getSecret_key();
            String salt=user.getSalt();
            if(StringUtil.isBlank(secretKey)||StringUtil.isBlank(apiKey)) {
                logger.info("当前数据库没有存储两项KEY");
                return false;
            }

            String sk=EnDecryptUtil.decryptAES(secretKey,salt);
            String ak=EnDecryptUtil.decryptAES(apiKey,salt);

            Api.setApi_key(ak);
            Api.setSecret_key(sk);
            logger.info("设置Key结束");
            return  true;

        } catch (CommonException e) {
            logger.error("初始化用户Key失败",e);
            return  false;
        }


    }

    public void HmSell(){
        List<Config> list1 = commonService.getConfigs(1, QueryConstant.CONFIG_SELECTED_HAND_TYPE, QueryConstant.CONFIG_SELL_TYPE);
        if(list1!=null){//卖出配置
            List list=commonService.getConfigSetList(QueryConstant.CONFIG_TYPE_PRE_SELL,1);
            Config[]configs= commonService.sortConfigListByPriority(list);
            for (int i=0;i<configs.length;i++){
                Config config = configs[i];
                Map<String,String> configInfo = JsonMapper.nonDefaultMapper().fromJson(config.getConfigInfo(), Map.class);
                List<Price>prices=api.getMoneyPrice(config.getSymbol());
                if(prices==null)
                    continue;
                Price price=prices.get(0);
                Double currentPrice=Double.valueOf(price.getPrice());
                Double sellPrice=Double.valueOf(configInfo.get(QueryConstant.CONFIG_TYPE_PRE_SELL+"Price"));
                if(currentPrice.doubleValue()<sellPrice.doubleValue()){//卖出（价格比较，当前价格币设定价大，就可以卖出）
                    continue;
                }
                SellOrBuyInfo sellOrBuyInfo=api.sell(config.getSymbol(),Double.valueOf(configInfo.get("num")),price.getPrice());
                if(sellOrBuyInfo!=null&&!StringUtil.isBlank(sellOrBuyInfo.getSide())){//交易成功，记录日志，邮件通知
                    logger.info(String.format("交易成功，成功以%s价格卖出%s",currentPrice,config.getSymbol()));
                    mailService.sendNotify(config.getSymbol(),price.getPrice(),2);
                }else {
                    logger.info("手动卖出交易失败");
                }
            }
        }
    }


    public void doHm(){

        new Thread(new Runnable() {//开启两个线程执行买入和卖出，每个10秒左右查询
            @Override
            public void run() {

                while (true){
                    try {
                        HmBuy();
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        logger.error("线程执行买入出错",e);
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        HmSell();
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        logger.error("线程执行卖出出错",e);
                    }
                }
            }
        }).start();

    }


}
