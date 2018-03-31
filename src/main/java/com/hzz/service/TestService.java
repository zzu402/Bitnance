package com.hzz.service;

import com.hzz.common.dao.ConditionOp;
import com.hzz.common.dao.ModelDao;
import com.hzz.common.dao.OpType;
import com.hzz.constant.QueryConstant;
import com.hzz.exception.CommonException;
import com.hzz.main.Api;
import com.hzz.model.Config;
import com.hzz.model.Price;
import com.hzz.model.SellOrBuyInfo;
import com.hzz.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.SqlParameterValue;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/31
 */
public class TestService {

    private Logger logger= LoggerFactory.getLogger(TestService.class);
    private Api api=new Api();
    private CommonService commonService=new CommonService();
    private MailService mailService=new MailService();
    private ModelDao modelDao= DaoUtils.getDao(DaoUtils.getTemplate());

    private static Integer DISTANCE_THRESHOLD_MAX=18;//距离阈值，最小值距离当前价格的位置,一个单位代表10秒
    private static Integer DISTANCE_THRESHOLD_MIN=6;//距离阈值，最小值距离当前价格的位置,一个单位代表10秒

    public void imitateSell(Long time){//自动卖出策略1
            List list = commonService.getConfigSetList(QueryConstant.CONFIG_TYPE_PRE_SELL, 1);
            Config[] configs = commonService.sortConfigListByPriority(list);
            for (int i = 0; i < configs.length; i++) {
                Config config = configs[i];
                Map<String, String> configInfo = JsonMapper.nonDefaultMapper().fromJson(config.getConfigInfo(), Map.class);
                try {
                    Price condition=new Price();
                    condition.setCreateTime(time);
                    List<Price> prices = modelDao.select(condition);
                    if (prices == null)
                        continue;
                    Price price = prices.get(0);//当前的价格
                    Double currentPrice=Double.valueOf(price.getPrice());
                    Price configPrice=new Price();
                    configPrice.setSymbol(config.getSymbol());
                    config.where().add(new ConditionOp("createTime", OpType.LT, new SqlParameterValue(Types.BIGINT, "BIGINT", time)));
                    configPrice.limitCount(2*360);//获取最近两个小时的数据
                    configPrice.groupBy("createTime desc");
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
                        for (int j = maxPosition-(Ksub.length-1-maxPosition); j < Ksub.length; j++) {
                            sum += Ksub[j];
                        }
                        if (sum < 0) {//当前总体是下降趋势，判断可以卖出
                            Double setPrice=Double.valueOf(configInfo.get("price"));//还要判断价格
                            if(MathUtils.compareDouble(setPrice,0.0)>0) {
                                //如果设定价格非0,就得判断当前要买入的价格和设置的价格比较
                                if(MathUtils.compareDouble(currentPrice,setPrice)<0){//如果当前的价格比设置的价格小，就不卖出
                                    continue;
                                }else{
                                    //这里执行卖出操作
                                    price.setPointType(2);
                                    modelDao.update(price);
                                }
                            }
                        }
                    }
                } catch (CommonException e) {
                    logger.error("执行自动策略获取数据库价格信息时候出错",e);
                }
            }
    }

    public void imitateSellBuy(Long time){//自动买入策略1
            List list = commonService.getConfigSetList(QueryConstant.CONFIG_TYPE_PRE_BUY, 1);
            Config[] configs = commonService.sortConfigListByPriority(list);
            for (int i = 0; i < configs.length; i++) {
                Config config = configs[i];
                Map<String, String> configInfo = JsonMapper.nonDefaultMapper().fromJson(config.getConfigInfo(), Map.class);
                try {
                    Price condition=new Price();
                    condition.setCreateTime(time);
                    List<Price> prices = modelDao.select(condition);
                    if (prices == null)
                        continue;
                    Price price = prices.get(0);//当前的价格
                    Double currentPrice=Double.valueOf(price.getPrice());
                    Price configPrice=new Price();
                    config.where().add(new ConditionOp("createTime", OpType.LT, new SqlParameterValue(Types.BIGINT, "BIGINT", time)));
                    configPrice.setSymbol(config.getSymbol());
                    configPrice.limitCount(2*360);//获取最近两个小时的数据
                    configPrice.groupBy("createTime desc");
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
                        for (int j = minPosition-(Ksub.length-1-minPosition); j < Ksub.length; j++) {
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
                                    price.setPointType(1);
                                    modelDao.update(price);
                                }
                            }
                        }
                    }
                } catch (CommonException e) {
                    logger.error("执行自动策略获取数据库价格信息时候出错",e);
                }

        }
    }
    private void doFinishUpdateNum(Map<String, String> configInfo,Config config){
        configInfo.put("num","0");
        config.setConfigInfo(JsonMapper.nonEmptyMapper().toJson(configInfo));
        List<Config> list2=new ArrayList<>();
        list2.add(config);
        commonService.insertOrUpdateConfigs(list2);
    }

    private Double[] coverPriceToDoubleArray(List<Price> priceList) {
        Double[]doubles=new Double[priceList.size()+1];
        for(int i=0;i<priceList.size();i++){
            doubles[i]=Double.valueOf(priceList.get(i).getPrice());
        }
        return doubles;
    }


    public void doImitate(){
        List<Config> list = commonService.getConfigSetList(QueryConstant.CONFIG_TYPE_PRE_SELL, 1);
        for(int i=0;i<list.size();i++) {
            Price condition = new Price();
            condition.setSymbol(list.get(i).getSymbol());
            condition.groupBy("createTime desc");
            try {
                List<Price> priceList=modelDao.select(condition);
                for(int j=720;j<priceList.size();j++){
                    imitateSell(priceList.get(i).getCreateTime());
                    imitateSellBuy(priceList.get(i).getCreateTime());
                }
            } catch (CommonException e) {
                logger.error("执行模拟任务失败", e);
            }
        }
        AlertUtils.showMessage("模拟成功，请查看！");
    }










}
