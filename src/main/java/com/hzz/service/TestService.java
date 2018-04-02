package com.hzz.service;

import com.hzz.common.dao.ModelDao;
import com.hzz.constant.QueryConstant;
import com.hzz.exception.CommonException;
import com.hzz.model.Config;
import com.hzz.model.Price;
import com.hzz.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private ModelDao modelDao= DaoUtils.getDao(DaoUtils.getTemplate());
    private ConfigService configService=new ConfigService();

    private static Integer DISTANCE_THRESHOLD_MAX=18;//距离阈值，最小值距离当前价格的位置,一个单位代表10秒
    private static Integer DISTANCE_THRESHOLD_MIN=6;//距离阈值，最小值距离当前价格的位置,一个单位代表10秒



    public void imitateSell(Long time){//自动卖出策略1

    }

    public void imitateBuy(List<Price> priceList){//自动买入策略1
        List<Price> updateList=new ArrayList<>();
        Integer margin=30;
        Price currentPrice;
        List<Price> marginPrice=new ArrayList<>(30);
        Double current;
        for(int i=30;i<priceList.size();i++) {
            currentPrice=priceList.get(i);
            current=Double.valueOf(currentPrice.getPrice());
            marginPrice.clear();
            for(int j=i-margin;j<i;j++){

                marginPrice.add(priceList.get(j));
            }
            Double[] priceDoubles=coverPriceToDoubleArray(marginPrice);
            priceDoubles[priceDoubles.length-1]=current;//将当前价格加入
            Integer minPosition=MathUtils.findMinPosition(priceDoubles);//买入去寻找最近5分钟最低价格
            if(minPosition+DISTANCE_THRESHOLD_MAX>=priceDoubles.length&&minPosition+DISTANCE_THRESHOLD_MIN<=priceDoubles.length){
                currentPrice.setPointType(1);
                updateList.add(currentPrice);
            }
        }
        try {
            logger.info("开始更新...");
            modelDao.batchInsertOrUpdate(updateList,new String[]{"id","pointType"});
            logger.info("结束更新...");
        } catch (CommonException e) {
            logger.error("数据库插入出错",e);
        }

    }
    private void doFinishUpdateNum(Map<String, String> configInfo,Config config){
        configInfo.put("num","0");
        config.setConfigInfo(JsonMapper.nonEmptyMapper().toJson(configInfo));
        List<Config> list2=new ArrayList<>();
        list2.add(config);
        configService.insertOrUpdateConfigs(list2);
    }

    private Double[] coverPriceToDoubleArray(List<Price> priceList) {
        Double[]doubles=new Double[priceList.size()+1];
        for(int i=0;i<priceList.size();i++){
            doubles[i]=Double.valueOf(priceList.get(i).getPrice());
        }
        return doubles;
    }


    public static void main(String[]args){
        TestService testService=new TestService();
        testService.doImitate();
    }

    public void doImitate(){
        logger.info("start imitate ...");
        List<Config> list = configService.getConfigSetList(QueryConstant.CONFIG_TYPE_PRE_BUY, 1);
        for(int i=0;i<list.size();i++) {
            Price condition = new Price();
            condition.setSymbol(list.get(i).getSymbol());
            condition.groupBy("createTime desc");
            try {
                List<Price> priceList=modelDao.select(condition);
                imitateBuy(priceList);//对于整个symbol的价格进行
            } catch (CommonException e) {
                logger.error("执行模拟任务失败", e);
            }
        }
        logger.info("end imitate...");
    }










}
