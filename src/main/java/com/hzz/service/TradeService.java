package com.hzz.service;

import com.hzz.common.dao.*;
import com.hzz.constant.AppConstant;
import com.hzz.constant.QueryConstant;
import com.hzz.exception.CommonException;
import com.hzz.main.Api;
import com.hzz.model.*;
import com.hzz.ui.panel.UserInfoPanel;
import com.hzz.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/27
 */
public class TradeService {

    private Logger logger= LoggerFactory.getLogger(TradeService.class);
    private Api api=Api.getApi();
    private ConfigService configService=new ConfigService();
    private MailService mailService=new MailService();
    private ModelDao modelDao=DaoUtils.getDao(DaoUtils.getTemplate());

    private  static long buyLogger=-1L;
    private  static long sellLogger=-1L;

    public void saveMyTrade(String symbol){
        List<MyTrade> myTradeList = api.getMyTrades(symbol, "", 10);
        if(myTradeList==null||myTradeList.isEmpty())
            return;
        try {
            modelDao.batchInsertOrUpdate(myTradeList,new String[]{"price","qty","time","isBestMatch","isBuyer","isMaker"});
        } catch (CommonException e) {
            logger.error("插入我的历史交易信息出错", e);
        }
    }


    public void saveOrder(String symbol){
        List<Order> orders = api.getMyOrders(symbol, "", 10);
        if(orders==null||orders.isEmpty())
            return;
        try {
            modelDao.batchInsertOrUpdate(orders,new String[]{"symbol","orderId","clientOrderId","price","origQty","executedQty","status","timeInForce","type","side","stopPrice","icebergQty","isWorking","time"});
        } catch (CommonException e) {
            logger.error("插入我的历史订单信息出错", e);
        }
    }


    public List<Map<String, Object>> getTradeBySymbol(String symbol){
        ModelDao modelDao = DaoUtils.getDao(DaoUtils.getTemplate());
        Map<JoinModel, JoinType> joinMap = new LinkedHashMap<>();
        JoinModel joinModel = new JoinModel();
        joinModel.setAliasName("t");
        joinModel.setJoinModel(MyTrade.class);
        joinMap.put(joinModel, JoinType.INNER);

        joinModel = new JoinModel();
        joinModel.setAliasName("o");
        joinModel.setJoinModel(Order.class);
        joinModel.on().add(new ConditionCustom("o.orderId=t.orderId and o.symbol='"+symbol+"'"));
        joinMap.put(joinModel, JoinType.INNER);

        ConditionModel condition = new ConditionModel();
        condition.orderBy("t.time desc");
        condition.columns().addAll(Arrays.asList(new String[]{"t.time", "t.price", "t.qty", "t.isMaker", "t.isBuyer", "o.symbol","o.side"}));

        try {
            List<Map<String, Object>> tradeList = modelDao.select(joinMap, condition);
            return  tradeList;
        } catch (CommonException e) {
            logger.error("查询交易出错",e);
            return  null;
        }

    }

    /*
        手动处理交易
     */
    public void HmBuy() {
        buyLogger++;
        List<Config> list1 = configService.getConfigs(1, QueryConstant.CONFIG_SELECTED_HAND_TYPE, QueryConstant.CONFIG_BUY_TYPE);
        if(buyLogger%60==0)
            logger.info("进入手动买入程序...");
        if (list1 != null&&!list1.isEmpty()) {//买入配置
            List list = configService.getConfigSetList(QueryConstant.CONFIG_TYPE_PRE_BUY, 1);
            if(list==null||list.isEmpty())
                return;
            Config[] configs = configService.sortConfigListByPriority(list);
            for (int i = 0; i < configs.length; i++) {
                Config config = configs[i];
                Map<String, String> configInfo = JsonMapper.nonDefaultMapper().fromJson(config.getConfigInfo(), Map.class);
                Double num=Double.valueOf(configInfo.get("num"));
                if(buyLogger%60==0)
                    logger.info("手动买入查询币种:"+config.getSymbol());
                if(NumberUtils.isEquals(num,0.0))
                    continue;
                List<Price> prices = api.getMoneyPrice(config.getSymbol());
                if (prices == null)
                    continue;
                Price price = prices.get(0);
                Double currentPrice = Double.valueOf(price.getPrice());
                Double buyPrice = Double.valueOf(configInfo.get( "buyPrice"));

                if (currentPrice.doubleValue() > buyPrice.doubleValue()) {//买入（价格比较，当前价格币设定价格小，就可以买入）
                    continue;
                }
                if(buyLogger%60==0)
                    logger.info("手动买入价格适合币种:"+config.getSymbol());
                SellOrBuyInfo sellOrBuyInfo = api.buy(config.getSymbol(), num, price.getPrice());
                if (sellOrBuyInfo != null&&!StringUtil.isBlank(sellOrBuyInfo.getSide())) {//交易成功，记录日志，邮件通知
                    logger.info(String.format("手动交易成功，成功以%s价格买入%s",currentPrice,config.getSymbol()));
                    mailService.sendNotify(config.getSymbol(),price.getPrice(),1);
                    //买完之后，num设置为0，防止重复买
                  doFinishUpdateNum(configInfo,config);
                  UserInfoPanel.UpdateTradeMethod();
                  addAvailableSymbol(num,config.getSymbol());
                }else {
                    logger.info("手动买入交易失败");
                }
            }
        }
    }
    public static void main(String[] args){
        MailService mailService=new MailService();
        mailService.sendNotify("","",1);
    }

    private void doFinishUpdateNum(Map<String, String> configInfo,Config config){
        configInfo.put("num","0");
        config.setConfigInfo(JsonMapper.nonEmptyMapper().toJson(configInfo));
        List<Config> list2=new ArrayList<>();
        list2.add(config);
        configService.insertOrUpdateConfigs(list2);
    }

    private void addAvailableSymbol(Double num,String symbol){
        AvailableSymbol condition=new AvailableSymbol();
        condition.setSymbol(symbol);
        Long now=System.currentTimeMillis()/1000;
        try {
            List symbolList=modelDao.select(condition);
            if(symbolList!=null&&!symbolList.isEmpty()){
                AvailableSymbol availableSymbol= (AvailableSymbol) symbolList.get(0);
                Double n=NumberUtils.valueOf(availableSymbol.getQty());
                n=n+num-num*0.001;
                availableSymbol.setQty(NumberUtils.getNumberStr(n));
                availableSymbol.setUpdateTime(now);
                modelDao.insertOrUpdate(availableSymbol,new String[]{"qty","updateTime"});
            }else {
                AvailableSymbol availableSymbol=new AvailableSymbol();
                availableSymbol.setSymbol(symbol);
                num=num-num*0.001;
                availableSymbol.setQty(NumberUtils.getNumberStr(num));
                availableSymbol.setCreateTime(now);
                availableSymbol.setUpdateTime(now);
                modelDao.insert(availableSymbol);
            }
        } catch (CommonException e) {
            logger.error("数据库查找失败",e);
        }

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
        sellLogger++;
        List<Config> list1 = configService.getConfigs(1, QueryConstant.CONFIG_SELECTED_HAND_TYPE, QueryConstant.CONFIG_SELL_TYPE);
        if(list1!=null&&!list1.isEmpty()){//卖出配置
            if(sellLogger%60==0)
                logger.info("进入手动卖出程序...");
            List list=configService.getConfigSetList(QueryConstant.CONFIG_TYPE_PRE_SELL,1);
            if(list==null||list.isEmpty())
                return;
            Config[]configs= configService.sortConfigListByPriority(list);
            for (int i=0;i<configs.length;i++){
                Config config = configs[i];
                if(sellLogger%60==0)
                    logger.info("手动卖出币种查询:"+config.getSymbol());
                Map<String,String> configInfo = JsonMapper.nonDefaultMapper().fromJson(config.getConfigInfo(), Map.class);
                Double num=Double.valueOf(configInfo.get("num"));
                if(NumberUtils.isEquals(num,0.0))
                    continue;
                List<Price>prices=api.getMoneyPrice(config.getSymbol());
                if(prices==null)
                    continue;
                Price price=prices.get(0);
                Double currentPrice=Double.valueOf(price.getPrice());
                Double sellPrice=Double.valueOf(configInfo.get("sellPrice"));
                if(currentPrice.doubleValue()<sellPrice.doubleValue()){//卖出（价格比较，当前价格币设定价大，就可以卖出）
                    continue;
                }
                if(sellLogger%60==0)
                    logger.info("手动卖出价格合适币种:"+config.getSymbol());
                AvailableSymbol condition=new AvailableSymbol();
                condition.setSymbol(config.getSymbol());
                List symbolList= null;
                try {
                    symbolList = modelDao.select(condition);
                } catch (CommonException e) {
                    logger.error("手动卖出失败",e);
                }
                if(symbolList==null||symbolList.isEmpty())
                    continue;
                AvailableSymbol availableSymbol= (AvailableSymbol) symbolList.get(0);
                num=NumberUtils.valueOf(availableSymbol.getQty());
                if(sellLogger%60==0)
                    logger.info("手动卖出币种可用数量:"+num);
                if(num<=0)
                    continue;

                SellOrBuyInfo sellOrBuyInfo=api.sell(config.getSymbol(),num,price.getPrice());
                if(sellOrBuyInfo!=null&&!StringUtil.isBlank(sellOrBuyInfo.getSide())){//交易成功，记录日志，邮件通知
                    logger.info(String.format("手动交易成功，成功以%s价格卖出%s",currentPrice,config.getSymbol()));
                    mailService.sendNotify(config.getSymbol(),price.getPrice(),2);
                    doFinishUpdateNum(configInfo,config);
                    availableSymbol.setQty(NumberUtils.getNumberStr(0.0));
                    availableSymbol.setUpdateTime(System.currentTimeMillis()/1000);
                    try {
                        modelDao.insertOrUpdate(availableSymbol,new String[]{"qty","updateTime"});
                    } catch (CommonException e) {
                        logger.error("手动卖出更新可用币种数量失败",e);
                    }
                    UserInfoPanel.UpdateTradeMethod();
                }else {
                    logger.info("手动卖出交易失败");
                }
            }
        }
    }

    public void tradeAutoMethodSell_1(){//自动卖出策略1
        List<Config> list1 = configService.getConfigs(1, QueryConstant.CONFIG_SELECTED_AUTO_TYPE_1, QueryConstant.CONFIG_SELL_TYPE);
        if(list1!=null&&!list1.isEmpty()){//执行自动买入策略1
            List list = configService.getConfigSetList(QueryConstant.CONFIG_TYPE_PRE_SELL, 1);
            if(list==null||list.isEmpty())
                return;
            Config[] configs = configService.sortConfigListByPriority(list);
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
                configPrice.limitCount(AppConstant.TIME_MARGIN_DATA_COUNT);//获取时间段内多少条数据
                configPrice.orderBy("createTime asc");
                try {
                    List<Price>priceList=modelDao.select(configPrice);
                    if(priceList==null||priceList.isEmpty())
                        continue;
                    Double[] priceDoubles=coverPriceToDoubleArray(priceList);
                    priceDoubles[priceDoubles.length-1]=currentPrice;//将当前价格加入
                    Integer maxPosition=MathUtils.findMaxPosition(priceDoubles);//卖出去寻找最近两个小时最高价格
                    //触发条件，当前出现最低点在最新价格附近
                    if(maxPosition+ AppConstant.DISTANCE_THRESHOLD_MAX>=priceDoubles.length-1&&maxPosition+AppConstant.DISTANCE_THRESHOLD_MIN<=priceDoubles.length-1) {

                        Double setPrice=Double.valueOf(configInfo.get("price"));//还要判断价格
                        if(MathUtils.compareDouble(setPrice,0.0)>0) {
                            //如果设定价格非0,就得判断当前要买入的价格和设置的价格比较
                            if(MathUtils.compareDouble(currentPrice,setPrice)<0){//如果当前的价格比设置的价格小，就不卖出
                                continue;
                            }else{
                                //这里执行卖出操作
                                AvailableSymbol condition=new AvailableSymbol();
                                condition.setSymbol(config.getSymbol());
                                List symbolList=modelDao.select(condition);
                                if(symbolList==null||symbolList.isEmpty())
                                    continue;
                                AvailableSymbol availableSymbol= (AvailableSymbol) symbolList.get(0);
                                Double num=NumberUtils.valueOf(availableSymbol.getQty());
                                if(num<=0)
                                    continue;
                                SellOrBuyInfo sellOrBuyInfo=api.sell(config.getSymbol(),num,price.getPrice());
                                if(sellOrBuyInfo!=null&&!StringUtil.isBlank(sellOrBuyInfo.getSide())){//交易成功，记录日志，邮件通知
                                    logger.info(String.format("自动交易成功，成功以%s价格卖出%s",currentPrice,config.getSymbol()));
                                    mailService.sendNotify(config.getSymbol(),price.getPrice(),2);
                                    doFinishUpdateNum(configInfo,config);
                                    UserInfoPanel.UpdateTradeMethod();
                                    availableSymbol.setQty("0");
                                    availableSymbol.setUpdateTime(System.currentTimeMillis()/1000);
                                    modelDao.update(availableSymbol);
                                }else {
                                    logger.info("自动卖出交易失败");
                                }
                            }
                        }else {
                            //没有设置价钱，比买入价格高



                        }
                    }
                } catch (CommonException e) {
                    logger.error("执行自动策略获取数据库价格信息时候出错",e);
                }
            }
        }
    }



    public void tradeAutoMethodBuy_1(){//自动买入策略1
        List<Config> list1 = configService.getConfigs(1, QueryConstant.CONFIG_SELECTED_AUTO_TYPE_1, QueryConstant.CONFIG_BUY_TYPE);
        if(list1!=null&&!list1.isEmpty()){//执行自动买入策略1
            List list = configService.getConfigSetList(QueryConstant.CONFIG_TYPE_PRE_BUY, 1);
            if(list==null||list.isEmpty())
                return;
            Config[] configs = configService.sortConfigListByPriority(list);
            for (int i = 0; i < configs.length; i++) {
                Config config = configs[i];
                Map<String, String> configInfo = JsonMapper.nonDefaultMapper().fromJson(config.getConfigInfo(), Map.class);
                Double num=Double.valueOf(configInfo.get("num"));
                if(NumberUtils.isEquals(num,0.0))
                    continue;
                List<Price> prices = api.getMoneyPrice(config.getSymbol());
                if (prices == null)
                    continue;
                Price price = prices.get(0);//当前的价格
                Double currentPrice=Double.valueOf(price.getPrice());
                Price configPrice=new Price();
                configPrice.setSymbol(config.getSymbol());
                configPrice.limitCount(AppConstant.TIME_MARGIN_DATA_COUNT);
                configPrice.orderBy("createTime asc");
                try {
                    List<Price>priceList=modelDao.select(configPrice);
                    if(priceList==null||priceList.isEmpty())
                        continue;
                    Double[] priceDoubles=coverPriceToDoubleArray(priceList);
                    priceDoubles[priceDoubles.length-1]=currentPrice;//将当前价格加入
                    Integer minPosition=MathUtils.findMinPosition(priceDoubles);

                    //触发条件，当前出现最低点在最新价格附近
                    if(minPosition+AppConstant.DISTANCE_THRESHOLD_MAX>=priceDoubles.length-1&&minPosition+AppConstant.DISTANCE_THRESHOLD_MIN<=priceDoubles.length-1) {
                        Double setPrice=Double.valueOf(configInfo.get("price"));//还要判断价格
                        if(MathUtils.compareDouble(setPrice,0.0)>0) {
                            //如果设定价格非0,就得判断当前要买入的价格和设置的价格比较
                            if(MathUtils.compareDouble(currentPrice,setPrice)>0){//如果当前的价格比设置的价格大，就不买入
                                continue;
                            }else{
                                //这里执行买入操作
                                SellOrBuyInfo sellOrBuyInfo = api.buy(config.getSymbol(), num, price.getPrice());
                                if (sellOrBuyInfo != null&&!StringUtil.isBlank(sellOrBuyInfo.getSide())) {//交易成功，记录日志，邮件通知
                                    logger.info(String.format("自动交易成功，成功以%s价格买入%s",currentPrice,config.getSymbol()));
                                    mailService.sendNotify(config.getSymbol(),price.getPrice(),1);
                                    //买完之后，num设置为0，防止重复买
                                    doFinishUpdateNum(configInfo,config);
                                    UserInfoPanel.UpdateTradeMethod();
                                    addAvailableSymbol(num,config.getSymbol());
                                }else {
                                    logger.info("自动买入交易失败");
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


    /*
       生成我的交易策略
    */
    public Map<String, String> getTradeMethod() {
        Map<String, String> map = new HashMap<>();
        List<Config> list1 =configService. getConfigs(1, null, QueryConstant.CONFIG_BUY_TYPE);
        List<Config> list2 = configService.getConfigs(1, null, QueryConstant.CONFIG_SELL_TYPE);
        String buy = null;
        String sell = null;
        if (list1 != null) {
            Config config = list1.get(0);
            if (config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_HAND_TYPE)) {
                buy = getBuyMethod("当前使用手动买入策略！");
            } else if (config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_AUTO_TYPE_1)) {
                buy = getBuyMethod("当前使用自动买入策略方法1！");
            } else if (config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_AUTO_TYPE_2)) {
                buy = getBuyMethod("当前使用自动买入策略方法2！");
            }
        } else {
            buy = getBuyMethod("当前没有设置买入策略！");
        }
        if (list2 != null) {
            Config config = list2.get(0);
            if (config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_HAND_TYPE)) {
                sell=getSellMethod("当前使用手动卖出策略！");
            } else if (config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_AUTO_TYPE_1)) {
                sell=getSellMethod("当前使用自动卖出策略方法1！");
            } else if (config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_AUTO_TYPE_2)) {
                sell=getSellMethod("当前使用自动卖出策略方法2！");
            }
        } else {
           sell=getSellMethod("当前没有设置卖出策略！");

        }
        map.put("buy", buy);
        map.put("sell", sell);
        return map;
    }

    private String getSellMethod(String method){
        String sell = method;
        Map<String, Config> sellSets = configService.getConfigSets(QueryConstant.CONFIG_TYPE_PRE_SELL, 1);
        if(sellSets!=null) {
            Iterator it = sellSets.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                Config value = (Config) entry.getValue();
                Map<String, String> configInfo = JsonMapper.nonDefaultMapper().fromJson(value.getConfigInfo(), Map.class);
                Long time = Long.parseLong((String) configInfo.get("time"));
                String timeStr = time == 0 ? "无限" : DateUtil.format("yyyy-MM-dd hh:mm:ss", new Date(time * 1000));
                sell += "\r\n币种:" + key + "\r\n设置时币种价格:" + configInfo.get("price") + "\r\n设置卖出价格:" + configInfo.get("sellPrice") + "\r\n优先级:" + configInfo.get("priority") + " \r\n截止时间:" + timeStr;
            }
        }
        return  sell;
    }
    private String getBuyMethod(String method) {
        String buy = method;
        Map<String, Config> buySets =configService. getConfigSets(QueryConstant.CONFIG_TYPE_PRE_BUY, 1);
        if(buySets!=null) {
            Iterator it = buySets.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                Config value = (Config) entry.getValue();
                Map<String, String> configInfo = JsonMapper.nonDefaultMapper().fromJson(value.getConfigInfo(), Map.class);
                Long time = Long.parseLong((String) configInfo.get("time"));
                String timeStr = time == 0 ? "无限" : DateUtil.format("yyyy-MM-dd hh:mm:ss", new Date(time * 1000));
                buy += "\r\n币种:" + key + "\r\n设置时币种价格:" + configInfo.get("price") + "\r\n设置买入价格:" + configInfo.get("buyPrice") + "\r\n优先级:" + configInfo.get("priority") + "\r\n截止时间:" + timeStr + "\r\n交易数量:" + configInfo.get("num");
            }
        }
        return buy;

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
                        tradeAutoMethodBuy_1();
                        Thread.sleep(AppConstant.DO_BUY_TRADE_MARGIN_TIME);
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
                        tradeAutoMethodSell_1();
                        Thread.sleep(AppConstant.DO_SELL_TRADE_MARGIN_TIME);
                    } catch (Exception e) {
                        logger.error("线程执行卖出出错",e);
                    }
                }
            }
        }).start();

    }


}
