package com.hzz.service;

import com.hzz.common.dao.ModelDao;
import com.hzz.constant.QueryConstant;
import com.hzz.exception.CommonException;
import com.hzz.main.Api;
import com.hzz.model.Account;
import com.hzz.model.Balance;
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
public class AccountService {

    private Logger logger = LoggerFactory.getLogger(AccountService.class);
    private ModelDao modelDao = DaoUtils.getDao(DaoUtils.getTemplate());
    public Api api = Api.getApi();

    /*
     获取我的账户
     */
    public List<Account> getAccount() {

        try {
            List<Account>list=modelDao.select(new Account());
            return list;
        } catch (CommonException e) {
            logger.error("查询我的账户出错",e);
            return new ArrayList<Account>();
        }
    }

    /*
      获取目前我拥有的资产
     */
    public Map<String, Balance> getBalances() {
        return api.getAllMoneyFree();
    }


    /*
        计算总资产并存储到数据库
     */
    public void saveAccount() {
        Account account = api.getAccountInfo();
        Map<String, Balance> map = api.getAllMoneyFree();
        Iterator it = map.entrySet().iterator();
        Double moneyCount = 0.0;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            Balance value = (Balance) entry.getValue();

            //KEY 是币名 ,查找价格均以BTC换算
            if(key.equals("BTC")){
                Double all = Double.valueOf(value.getFree()) + Double.valueOf(value.getLocked());
                moneyCount += 1 * all;
            } else{
                List prices = api.getMoneyPrice(key + QueryConstant.DEFAULT_TRADE_CONVERT_CON);
                if (prices == null)
                    continue;
                Price price = (Price) prices.get(0);
                if (price.getPrice() == null)
                    continue;
                Double currentPrice = Double.valueOf(price.getPrice());
                Double all = Double.valueOf(value.getFree()) + Double.valueOf(value.getLocked());
                moneyCount += currentPrice * all;
            }
        }
        List prices = api.getMoneyPrice("BTCUSDT");
        if (prices == null)
            return;
        Price price = (Price) prices.get(0);
        if (price.getPrice() == null)
            return;
        Double currentPrice = Double.valueOf(price.getPrice());
        moneyCount = moneyCount*currentPrice*6.2862;
        account.setMoneyCount(moneyCount.longValue());
        account.setUpdateTime(System.currentTimeMillis()/1000);
        try {
            modelDao.insert(account);
        } catch (CommonException e) {
            logger.error("插入账户信息出错",e);
        }
    }






}
