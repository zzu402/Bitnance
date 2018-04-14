package com.hzz.service;

import com.hzz.common.dao.ModelDao;
import com.hzz.constant.AppConstant;
import com.hzz.constant.QueryConstant;
import com.hzz.exception.CommonException;
import com.hzz.main.Api;
import com.hzz.model.Config;
import com.hzz.model.Price;
import com.hzz.utils.DaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/4/2
 */
public class PriceService {
    private Logger logger = LoggerFactory.getLogger(PriceService.class);
    private ModelDao modelDao = DaoUtils.getDao(DaoUtils.getTemplate());
    private ConfigService configService=new ConfigService();
    public Api api = Api.getApi();

    /*
    获取实时价格
    */
    public List<Price> getPrices(String symbol) {
        return api.getMoneyPrice(symbol);
    }

    /*
         存储价格到数据库
   */
    public void savePrice(){
        if(AppConstant.PRICE_STORY_METHOD==0)
            return;
        List<Price> priceList=api.getMoneyPrice("");
        if(priceList==null)
            return;
        List<Price> storyPriceList=null;
        if(AppConstant.PRICE_STORY_METHOD==1) {
            for (int i = 0; i < priceList.size(); i++) {
                priceList.get(i).setCreateTime(System.currentTimeMillis() / 1000);
            }
            storyPriceList=priceList;
        }else if(AppConstant.PRICE_STORY_METHOD==2) {
            storyPriceList = new ArrayList<>();
            Map<String, Config> map = configService.getConfigSets(QueryConstant.CONFIG_TYPE_PRE_BUY, 1);
            Map<String, Config> map1 = configService.getConfigSets(QueryConstant.CONFIG_TYPE_PRE_SELL, 1);
            Price price = null;
            if (map != null) {
                map.putAll(map1);
                Iterator it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = (String) entry.getKey();
                    for (int i = 0; i < priceList.size(); i++) {
                        price = priceList.get(i);
                        if (!key.contains(price.getSymbol())) {
                            continue;
                        }
                        price.setCreateTime(System.currentTimeMillis() / 1000);
                        storyPriceList.add(price);
                    }
                }
            }
        }
        if(storyPriceList==null||storyPriceList.isEmpty())
            return;
        try {
            modelDao.batchInsert(storyPriceList);
        } catch (CommonException e) {
            logger.error("插入价格信息出错",e);
        }
    }

    public  List<Price> getPrice(String symbol,Integer buyPointType,Integer sellPointType){
        Price price=new Price();
        price.setSymbol(symbol);
        if(buyPointType>0)
            price.setPointType(buyPointType);
        if(sellPointType>0)
            price.setPointSellType(sellPointType);
        price.orderBy("createTime asc");//升序而不是降序
        ModelDao modelDao= DaoUtils.getDao(DaoUtils.getTemplate());
        try {
            List<Price> priceList=modelDao.select(price);
            return  priceList;
        } catch (CommonException e) {
            logger.error("查询数据库价格出错",e);
            return  null;
        }
    }



}
