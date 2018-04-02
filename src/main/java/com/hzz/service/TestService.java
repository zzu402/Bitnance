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
import java.util.Iterator;
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



    public void imitateSell(List<Price> priceList){//自动卖出策略1
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
            Integer maxPosition=MathUtils.findMaxPosition(priceDoubles);//寻找五分钟内最高价格
            if(maxPosition+DISTANCE_THRESHOLD_MAX>=priceDoubles.length&&maxPosition+DISTANCE_THRESHOLD_MIN<=priceDoubles.length){
                //如果最小的价格在当前价格并且不是当前价格
                Double[] Ksub = MathUtils.getKsub(priceDoubles);
                //获取价格的ksub值,计算从最高位置到当前价格的k是递减的，则可以卖出
                Double sum = 0.0;
                for (int j = maxPosition-(Ksub.length-1-maxPosition); j < Ksub.length; j++) {
                    sum += Ksub[j];
                }
                if (sum < 0) {//当前总体是下降趋势，判断可以卖出
                    currentPrice.setPointSellType(1);
                    updateList.add(currentPrice);
                }
            }
        }
        try {
            logger.info("模拟卖出开始...");
            modelDao.batchInsertOrUpdate(updateList,new String[]{"id","pointSellType"});
            logger.info("模拟卖出结束...");
        } catch (CommonException e) {
            logger.error("模拟卖出出错",e);
        }
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
                //如果最小的价格在当前价格并且不是当前价格
                Double[] Ksub = MathUtils.getKsub(priceDoubles);
                //获取价格的ksub值,计算从最小位置到当前价格的k是递增的，则可以买入
                Double sum = 0.0;
                for (int j = minPosition-(Ksub.length-1-minPosition); j < Ksub.length; j++) {
                    sum += Ksub[j];
                }
                if (sum > 0) {//当前总体是上升趋势，判断可以买入
                    currentPrice.setPointType(1);
                    updateList.add(currentPrice);
                }
            }
        }
        try {
            logger.info("模拟买入开始...");
            modelDao.batchInsertOrUpdate(updateList,new String[]{"id","pointType"});
            logger.info("模拟买入结束...");
        } catch (CommonException e) {
            logger.error("模拟买入出错",e);
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


//    public static void main(String[]args){
//        TestService testService=new TestService();
//        testService.doImitate();
//    }

    public void doImitate(){
        logger.info("start imitate ...");
        Map<String, Config> map=configService.getConfigSets(QueryConstant.CONFIG_TYPE_PRE_BUY,1);
        Map<String,Config>	map1=configService.getConfigSets(QueryConstant.CONFIG_TYPE_PRE_SELL,1);
        map.putAll(map1);
        Iterator it=map.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            Price condition = new Price();
            condition.setSymbol(key);
            condition.groupBy("createTime desc");
            try {
                List<Price> priceList=modelDao.select(condition);
                imitateBuy(priceList);//对于整个symbol的价格进行
                imitateSell(priceList);
            } catch (CommonException e) {
                logger.error("执行模拟任务失败", e);
            }
        }
        logger.info("end imitate...");
    }
}
