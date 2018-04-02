package com.hzz.service;

import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.main.Api;
import com.hzz.model.Price;
import com.hzz.utils.DaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/4/2
 */
public class PriceService {
    private Logger logger = LoggerFactory.getLogger(PriceService.class);
    private ModelDao modelDao = DaoUtils.getDao(DaoUtils.getTemplate());
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

    public  List<Price> getPrice(String symbol,Integer buyPointType,Integer sellPointType){
        Price price=new Price();
        price.setSymbol(symbol);
        if(buyPointType>0)
            price.setPointType(buyPointType);
        if(sellPointType>0)
            price.setPointSellType(sellPointType);
        price.groupBy("createTime desc");
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
