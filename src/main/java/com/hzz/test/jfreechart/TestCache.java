package com.hzz.test.jfreechart;

import com.fasterxml.jackson.databind.JavaType;
import com.hzz.cache.CacheInitHandler;
import com.hzz.cache.CacheManager;
import com.hzz.cache.ICacheService;
import com.hzz.model.Price;
import com.hzz.utils.JsonMapper;

import java.util.*;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/4/2
 */
public class TestCache {

    public static void main(String[]args){
        ICacheService cacheService=CacheManager.getCacheService();
//        cacheService.set("user","hzz");
//        System.out.println(cacheService.get("user"));



//        JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
//        JavaType javaType = jsonMapper.constructMapType(LinkedHashMap.class, Long.class, Price.class);
//
//        CacheManager.addCache("PRICE", javaType, new CacheInitHandler() {
//            @Override
//            public String initCache() {
//                    Map<Long, Price> priceMap = new LinkedHashMap<>();
//                    Price price=new Price();
//                    price.setSymbol("BTC");
//                    priceMap.put(1L,price);
//                    return JsonMapper.nonEmptyMapper().toJson(priceMap);
//            }
//        });
//        System.out.println((Map<Long, Price>)CacheManager.getCacheValue("PRICE"));



        JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
        JavaType javaType = jsonMapper.constructCollectionType(ArrayList.class, Price.class);
        CacheManager.addCache("PRICE", javaType, new CacheInitHandler() {
            @Override
            public String initCache() {
                    List<Price> priceList = new ArrayList<>();
                    Price price=new Price();
                    price.setSymbol("BTC");
                    priceList.add(price);
                    return JsonMapper.nonEmptyMapper().toJson(priceList);
            }
        });

        System.out.println((List<Price>)CacheManager.getCacheValue("PRICE"));

        //无效
        List<Price> list=CacheManager.getCacheValue("PRICE");
        Price price=new Price();
        price.setSymbol("TRX");
        list.add(price);
        System.out.println((List<Price>)CacheManager.getCacheValue("PRICE"));

    }


}
