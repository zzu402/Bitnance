package com.hzz.service;

import com.hzz.App;
import com.hzz.common.dao.ModelDao;
import com.hzz.constant.QueryConstant;
import com.hzz.exception.CommonException;
import com.hzz.main.Api;
import com.hzz.model.Config;
import com.hzz.model.Price;
import com.hzz.model.SellOrBuyInfo;
import com.hzz.model.User;
import com.hzz.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
    private ModelDao modelDao=DaoUtils.getDao(DaoUtils.getTemplate());

    private static Integer DISTANCE_THRESHOLD_MAX=18;//距离阈值，最小值距离当前价格的位置,一个单位代表10秒
    private static Integer DISTANCE_THRESHOLD_MIN=6;//距离阈值，最小值距离当前价格的位置,一个单位代表10秒

    /*
        手动处理交易
     */
    public void HmBuy() {
        List<Config> list1 = commonService.getConfigs(1, QueryConstant.CONFIG_SELECTED_HAND_TYPE, QueryConstant.CONFIG_BUY_TYPE);

        if (list1 != null&&!list1.isEmpty()) {//买入配置
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
                    logger.info(String.format("手动交易成功，成功以%s价格买入%s",currentPrice,config.getSymbol()));
                    mailService.sendNotify(config.getSymbol(),price.getPrice(),1);
                    //买完之后，num设置为0，防止重复买
                  doFinishUpdateNum(configInfo,config);
                }else {
                    logger.info("手动买入交易失败");
                }
            }
        }
    }

    public void doFinishUpdateNum(Map<String, String> configInfo,Config config){
        configInfo.put("num","0");
        config.setConfigInfo(JsonMapper.nonEmptyMapper().toJson(configInfo));
        List<Config> list2=new ArrayList<>();
        list2.add(config);
        commonService.insertOrUpdateConfigs(list2);
    }


    public boolean initKey(){//配置ApiKey和
        if(StringUtil.isBlank(Api.getApi_key())||StringUtil.isBlank(Api.getSecret_key())) {
            ModelDao modelDao = DaoUtils.getDao(DaoUtils.getTemplate());
            try {
                logger.info("设置key开始");
                List list = modelDao.select(new User());
                if (list.isEmpty()) {
                    logger.info("当前数据库没有存储两项KEY");
                    return false;
                }
                User user = (User) list.get(0);
                String apiKey = user.getApi_key();
                String secretKey = user.getSecret_key();
                String salt = user.getSalt();
                if (StringUtil.isBlank(secretKey) || StringUtil.isBlank(apiKey)) {
                    logger.info("当前数据库没有存储两项KEY");
                    return false;
                }

                String sk = EnDecryptUtil.decryptAES(secretKey, salt);
                String ak = EnDecryptUtil.decryptAES(apiKey, salt);

                Api.setApi_key(ak);
                Api.setSecret_key(sk);
                logger.info("设置Key结束");
                return true;

            } catch (CommonException e) {
                logger.error("初始化用户Key失败", e);
                return false;
            }
        }else{
            return true;
        }
    }

    public void HmSell(){
        List<Config> list1 = commonService.getConfigs(1, QueryConstant.CONFIG_SELECTED_HAND_TYPE, QueryConstant.CONFIG_SELL_TYPE);
        if(list1!=null&&!list1.isEmpty()){//卖出配置
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
                    logger.info(String.format("手动交易成功，成功以%s价格卖出%s",currentPrice,config.getSymbol()));
                    mailService.sendNotify(config.getSymbol(),price.getPrice(),2);
                    doFinishUpdateNum(configInfo,config);
                }else {
                    logger.info("手动卖出交易失败");
                }
            }
        }
    }

    public void tradeAutoMethodSell1(){//自动卖出策略1
        List<Config> list1 = commonService.getConfigs(1, QueryConstant.CONFIG_SELECTED_AUTO_TYPE_1, QueryConstant.CONFIG_SELL_TYPE);
        if(list1!=null&&!list1.isEmpty()){//执行自动买入策略1
            List list = commonService.getConfigSetList(QueryConstant.CONFIG_TYPE_PRE_SELL, 1);
            Config[] configs = commonService.sortConfigListByPriority(list);
            for (int i = 0; i < configs.length; i++) {
                Config config = configs[i];
                Map<String, String> configInfo = JsonMapper.nonDefaultMapper().fromJson(config.getConfigInfo(), Map.class);
                List<Price> prices = api.getMoneyPrice(config.getSymbol());
                if (prices == null)
                    continue;
                Price price = prices.get(0);//当前的价格
                Double currentPrice=Double.valueOf(price.getPrice());
                Price configPrice=new Price();
                configPrice.setSymbol(config.getSymbol());
                configPrice.limitCount(2*360);//获取最近两个小时的数据
                configPrice.groupBy("createTime desc");
                try {
                    List<Price>priceList=modelDao.select(configPrice);
                    if(priceList==null||priceList.isEmpty())
                        continue;
                    Double[] priceDoubles=coverPriceToDoubleArray(priceList);
                    priceDoubles[priceDoubles.length-1]=currentPrice;//将当前价格加入
                    Integer maxPosition=MathUtils.findMaxPosition(priceDoubles);//卖出去寻找最近两个小时最高价格
                    //触发条件，当前出现最低点在最新价格附近
                    if(maxPosition+DISTANCE_THRESHOLD_MAX>=priceDoubles.length-1&&maxPosition+DISTANCE_THRESHOLD_MIN<=priceDoubles.length-1) {
                        //如果最小的价格在当前价格并且不是当前价格
                        Double[] Ksub = MathUtils.getKsub(priceDoubles);
                        //获取价格的ksub值,计算从最高位置到当前价格的k是递减的，则可以卖出
                        Double sum = 0.0;
                        for (int j = maxPosition; j < Ksub.length; j++) {
                            sum += Ksub[i];
                        }
                        if (sum < 0) {//当前总体是下降趋势，判断可以卖出
                            Double setPrice=Double.valueOf(configInfo.get("price"));//还要判断价格
                            if(MathUtils.compareDouble(setPrice,0.0)>0) {
                                //如果设定价格非0,就得判断当前要买入的价格和设置的价格比较
                                if(MathUtils.compareDouble(currentPrice,setPrice)<0){//如果当前的价格比设置的价格小，就不卖出
                                    continue;
                                }else{
                                    //这里执行卖出操作
                                    SellOrBuyInfo sellOrBuyInfo=api.sell(config.getSymbol(),Double.valueOf(configInfo.get("num")),price.getPrice());
                                    if(sellOrBuyInfo!=null&&!StringUtil.isBlank(sellOrBuyInfo.getSide())){//交易成功，记录日志，邮件通知
                                        logger.info(String.format("自动交易成功，成功以%s价格卖出%s",currentPrice,config.getSymbol()));
                                        mailService.sendNotify(config.getSymbol(),price.getPrice(),2);
                                        doFinishUpdateNum(configInfo,config);
                                    }else {
                                        logger.info("自动卖出交易失败");
                                    }
                                }
                            }
                        }
                    }
                } catch (CommonException e) {
                    logger.error("执行自动策略获取数据库价格信息时候出错",e);
                }
            }
        }
    }



    public void tradeAutoMethodBuy1(){//自动买入策略1
        List<Config> list1 = commonService.getConfigs(1, QueryConstant.CONFIG_SELECTED_AUTO_TYPE_1, QueryConstant.CONFIG_BUY_TYPE);
        if(list1!=null&&!list1.isEmpty()){//执行自动买入策略1
            List list = commonService.getConfigSetList(QueryConstant.CONFIG_TYPE_PRE_BUY, 1);
            Config[] configs = commonService.sortConfigListByPriority(list);
            for (int i = 0; i < configs.length; i++) {
                Config config = configs[i];
                Map<String, String> configInfo = JsonMapper.nonDefaultMapper().fromJson(config.getConfigInfo(), Map.class);
                List<Price> prices = api.getMoneyPrice(config.getSymbol());
                if (prices == null)
                    continue;
                Price price = prices.get(0);//当前的价格
                Double currentPrice=Double.valueOf(price.getPrice());
                Price configPrice=new Price();
                configPrice.setSymbol(config.getSymbol());
                configPrice.limitCount(2*360);//获取最近两个小时的数据
                configPrice.groupBy("createTime desc");
                try {
                    List<Price>priceList=modelDao.select(configPrice);
                    if(priceList==null||priceList.isEmpty())
                        continue;
                    Double[] priceDoubles=coverPriceToDoubleArray(priceList);
                    priceDoubles[priceDoubles.length-1]=currentPrice;//将当前价格加入
                    Integer minPosition=MathUtils.findMinPosition(priceDoubles);//买入去寻找最近两个小时最低价格

                    //触发条件，当前出现最低点在最新价格附近
                    if(minPosition+DISTANCE_THRESHOLD_MAX>=priceDoubles.length-1&&minPosition+DISTANCE_THRESHOLD_MIN<=priceDoubles.length-1) {
                        //如果最小的价格在当前价格并且不是当前价格
                        Double[] Ksub = MathUtils.getKsub(priceDoubles);
                        //获取价格的ksub值,计算从最小位置到当前价格的k是递增的，则可以买入
                        Double sum = 0.0;
                        for (int j = minPosition; j < Ksub.length; j++) {
                            sum += Ksub[i];
                        }
                        if (sum > 0) {//当前总体是上升趋势，判断可以买入
                           Double setPrice=Double.valueOf(configInfo.get("price"));//还要判断价格
                            if(MathUtils.compareDouble(setPrice,0.0)>0) {
                                //如果设定价格非0,就得判断当前要买入的价格和设置的价格比较
                                if(MathUtils.compareDouble(currentPrice,setPrice)>0){//如果当前的价格比设置的价格大，就不买入
                                    continue;
                                }else{
                                    //这里执行买入操作
                                    SellOrBuyInfo sellOrBuyInfo = api.buy(config.getSymbol(), Double.valueOf(configInfo.get("num")), price.getPrice());
                                    if (sellOrBuyInfo != null&&!StringUtil.isBlank(sellOrBuyInfo.getSide())) {//交易成功，记录日志，邮件通知
                                        logger.info(String.format("自动交易成功，成功以%s价格买入%s",currentPrice,config.getSymbol()));
                                        mailService.sendNotify(config.getSymbol(),price.getPrice(),1);
                                        //买完之后，num设置为0，防止重复买
                                        doFinishUpdateNum(configInfo,config);
                                    }else {
                                        logger.info("自动买入交易失败");
                                    }
                                }
                            }
                        }
                    }
                } catch (CommonException e) {
                    logger.error("执行自动策略获取数据库价格信息时候出错",e);
                }


            }
        }
    }

    private Double[] coverPriceToDoubleArray(List<Price> priceList) {
        Double[]doubles=new Double[priceList.size()+1];
        for(int i=0;i<priceList.size();i++){
            doubles[i]=Double.valueOf(priceList.get(i).getPrice());
        }
        return doubles;
    }


    public void doHm(){

        new Thread(new Runnable() {//开启两个线程执行买入和卖出，每个10秒左右查询
            @Override
            public void run() {

                while (true){
                    try {
                        //手动
                        HmBuy();
                        //自动
                        tradeAutoMethodBuy1();
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
                        tradeAutoMethodSell1();
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        logger.error("线程执行卖出出错",e);
                    }
                }
            }
        }).start();

    }


}
