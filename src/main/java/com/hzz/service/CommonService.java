package com.hzz.service;

import com.hzz.common.dao.ModelDao;
import com.hzz.constant.QueryConstant;
import com.hzz.exception.CommonException;
import com.hzz.main.Api;
import com.hzz.model.Config;
import com.hzz.model.Price;
import com.hzz.utils.DaoUtils;
import com.hzz.utils.DateUtil;
import com.hzz.utils.JsonMapper;
import com.hzz.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/26
 */
public class CommonService {

    private Logger logger = LoggerFactory.getLogger(CommonService.class);
    private ModelDao modelDao = DaoUtils.getDao(DaoUtils.getTemplate());
    ;
    public static Api api = new Api();

    /*
     * 获取手动买入/卖出币种设置的配置信息
     */
    public Map<String, Config> getConfigSets(String configTypePre, Integer status) {
        Config config = new Config();
        config.setType(configTypePre + "SetConfig");
        if (status != null && status > 0)
            config.setStatus(status);
        List<Config> list = null;
        try {
            list = modelDao.select(config);
        } catch (CommonException e) {
            logger.error("查询币种配置信息异常", e);
        }
        if (list == null || list.isEmpty())
            return null;
        Map<String, Config> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++)
            map.put(list.get(i).getSymbol(), list.get(i));
        return map;
    }

    /**
     * 获取配置信息
     *
     * @param status
     * @param symbol
     * @param type
     * @return
     */
    public List<Config> getConfigs(Integer status, String symbol, String type) {

        Config config = new Config();
        config.setType(type);
        if (!StringUtil.isBlank(symbol))
            config.setSymbol(symbol);
        if (status != null && status > 0)
            config.setStatus(status);
        List<Config> list = null;
        try {
            list = modelDao.select(config);
        } catch (CommonException e) {
            logger.error("查询配置信息异常", e);
        }
        if (list == null || list.isEmpty())
            return null;
        return list;
    }

    public void insertOrUpdateConfigs(List<Config> list) {
        if (list == null || list.isEmpty())
            return;
        try {
            modelDao.batchInsertOrUpdate(list, new String[]{"symbol", "type", "configInfo", "description", "status", "createTime"});
        } catch (CommonException e) {
            logger.error("插入更新配置信息异常", e);
        }

    }

    public Map<String, String> getTradeMethod() {
        Map<String, String> map = new HashMap<>();
        List<Config> list1 = getConfigs(1, null, QueryConstant.CONFIG_BUY_TYPE);
        List<Config> list2 = getConfigs(1, null, QueryConstant.CONFIG_SELL_TYPE);
        String buy = null;
        String sell = null;
        if (list1 != null) {
            Config config = list1.get(0);
            if (config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_HAND_TYPE)) {
                buy = "当前使用手动买入策略:";
                Map<String, Config> buySets=getConfigSets(QueryConstant.CONFIG_TYPE_PRE_BUY,1);
                Iterator it = buySets.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = (String) entry.getKey();
                    Config value = (Config) entry.getValue();
                    Map<String,String> configInfo=JsonMapper.nonDefaultMapper().fromJson(value.getConfigInfo(),Map.class);
                    Long time=Long.parseLong((String) configInfo.get("time"));
                    String timeStr=time==0?"无限": DateUtil.format("yyyy-MM-dd hh:mm:ss",new Date(time*1000));
                    buy+="\r\n币种:"+key+"\r\n设置时币种价格:"+configInfo.get("price")+"\r\n设置买入价格:"+configInfo.get("buyPrice")+" \r\n截止时间:"+timeStr;
                }
            } else if (config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_AUTO_TYPE_1)) {
                buy = "当前使用自动买入策略1！";
            } else if (config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_AUTO_TYPE_2)) {
                buy = "当前使用自动买入策略2！";
            }
        } else {
            buy = "当前没有设置买入策略！";
        }
        if (list2 != null) {
            Config config = list2.get(0);
            if (config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_HAND_TYPE)) {
                sell = "当前使用手动卖出策略:";
                Map<String, Config> sellSets=getConfigSets(QueryConstant.CONFIG_TYPE_PRE_SELL,1);
                Iterator it = sellSets.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = (String) entry.getKey();
                    Config value = (Config) entry.getValue();
                    Map<String,String> configInfo=JsonMapper.nonDefaultMapper().fromJson(value.getConfigInfo(),Map.class);
                    Long time=Long.parseLong((String) configInfo.get("time"));
                    String timeStr=time==0?"无限": DateUtil.format("yyyy-MM-dd hh:mm:ss",new Date(time*1000));
                    sell+="\r\n币种:"+key+"\r\n设置时币种价格:"+configInfo.get("price")+"\r\n设置卖出价格:"+configInfo.get("sellPrice")+" \r\n截止时间:"+timeStr;
                }

                } else if (config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_AUTO_TYPE_1)) {
                sell = "当前使用自动卖出策略1!";
            } else if (config.getSymbol().equals(QueryConstant.CONFIG_SELECTED_AUTO_TYPE_2)) {
                sell = "当前使用自动卖出策略2!";
            }
        } else {
            sell = "当前没有设置卖出策略!";
        }
        map.put("buy", buy);
        map.put("sell", sell);
        return map;
    }


    public List<Price> getPrices(String symbol) {
        return getTestPrices(symbol);
    }

    public List<Price> getTestPrices(String symbol) {
        List<Price> priceList = new ArrayList<>();
        if (StringUtil.isBlank(symbol)) {
            for (int i = 0; i < 60; i++) {
                Price price = new Price();
                price.setPrice("154.32" + i);
                price.setSymbol("BTC_" + i);
                priceList.add(price);
            }
        } else {
            Price price = new Price();
            price.setPrice("154.32");
            price.setSymbol(symbol);
            priceList.add(price);
        }
        return priceList;
    }


}
