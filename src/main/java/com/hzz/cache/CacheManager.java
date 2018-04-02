package com.hzz.cache;

import com.fasterxml.jackson.databind.JavaType;
import com.hzz.utils.JsonMapper;
import com.hzz.utils.StringLockRunner;
import com.hzz.utils.StringUtil;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hongshuiqiao on 2017-08-06.
 */
public class CacheManager {
    private static Map<String, CacheItem> itemMap = new ConcurrentHashMap<>();
    private static ICacheService cacheService;
    public static ICacheService LOCAL_CACHE_SERVICE = new LocalStringCacheService();

    public static ICacheService getCacheService(){
        if(null == cacheService)
            return LOCAL_CACHE_SERVICE;
        return cacheService;
    }

    public static void setCacheService(ICacheService cacheService) {
        CacheManager.cacheService = cacheService;
    }

    public static boolean checkCache(String cacheKey){
        return itemMap.containsKey(cacheKey);
    }

    public static void addCache(String cacheKey, Type valueType, CacheInitHandler initHandler){
        addCache(cacheKey, 0, valueType, initHandler);
    }

    public static void addCache(String cacheKey, long expire, Type valueType, CacheInitHandler initHandler){
        StringLockRunner.syncRun(cacheKey, new Runnable() {
            @Override
            public void run() {
                itemMap.put(cacheKey, new CacheItem(valueType, getVersionKey(cacheKey), getValueKey(cacheKey), expire, initHandler));
            }
        });
    }

    private static String getValueKey(String cacheKey) {
        return String.format("%s_VALUE", cacheKey);
    }

    private static String getVersionKey(String cacheKey) {
        return String.format("%s_VERSION", cacheKey);
    }

    public static void removeCache(final String cacheKey){
        StringLockRunner.syncRun(cacheKey, new Runnable() {
            @Override
            public void run() {
                CacheItem item = itemMap.remove(cacheKey);
                if(null != item){
                    getCacheService().delete(getVersionKey(cacheKey));
                    getCacheService().delete(getValueKey(cacheKey));
                }
            }
        });
    }

    public static synchronized void clearCaches(){
        Iterator<String> iterator = itemMap.keySet().iterator();
        while (iterator.hasNext()){
            String cacheKey = iterator.next();
            getCacheService().delete(getVersionKey(cacheKey));
            getCacheService().delete(getValueKey(cacheKey));
        }
        itemMap.clear();
    }

    public static Map<String,Long> getCacheVersions(){
        Set<String> keys = itemMap.keySet();
        Map<String,Long> map = new LinkedHashMap<>();
        for (String key : keys) {
            Long version = getCacheVersion(key);
            if(null==version)
                continue;
            map.put(key, version);
        }
        return map;
    }

    public static Long getCacheVersion(String cacheKey){
        CacheItem item = itemMap.get(cacheKey);
        if(null==item)
            return null;
        return item.getVersion();
    }

    public static <T> T getCacheValue(String cacheKey){
        CacheItem item = itemMap.get(cacheKey);
        if(null==item)
            return null;

        String versionKey = getVersionKey(cacheKey);
        String valueKey = getValueKey(cacheKey);
        String version = getCacheService().get(versionKey);
        String value = getCacheService().get(valueKey);
        final String cacheValue = value;
        if(StringUtil.isBlank(version) || StringUtil.isBlank(cacheValue) || Long.parseLong(version) > item.getVersion()){
            StringLockRunner.syncRun(cacheKey, new Runnable() {
                @Override
                public void run() {
                    if(StringUtil.isBlank(version) || StringUtil.isBlank(cacheValue) || Long.parseLong(version) > item.getVersion()){
                        String initValue = item.getInitHandler().initCache();
                        long newVersion=System.currentTimeMillis()/1000;
                        item.setVersion(newVersion);
                        if(null != initValue){
                            getCacheService().set(valueKey,initValue, item.getExpire());
                            getCacheService().set(versionKey, Long.toString(newVersion), item.getExpire());
                        }else{
                            getCacheService().delete(versionKey);
                            getCacheService().delete(valueKey);
                        }
                    }
                }
            });
            value = getCacheService().get(valueKey);
        }
        if(null==value)
            return null;

        JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
        JavaType javaType = jsonMapper.constructType(item.getValueType());
        if(javaType.getRawClass()==String.class)
            return (T) value;
        else {
            return jsonMapper.fromJson(value, javaType);
        }
    }
}

class CacheItem<T> {
    private long version;
    private Type valueType;
    private String versionKey;
    private String valueKey;
    private long expire;
    private CacheInitHandler initHandler;

    public CacheItem(Type valueType, String versionKey, String valueKey, long expire, CacheInitHandler initHandler) {
        this.valueType = valueType;
        this.versionKey = versionKey;
        this.valueKey = valueKey;
        this.expire = expire;
        this.initHandler = initHandler;
    }

    public long getExpire() {
        return expire;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Type getValueType() {
        return valueType;
    }

    public String getVersionKey() {
        return versionKey;
    }

    public String getValueKey() {
        return valueKey;
    }

    public CacheInitHandler getInitHandler() {
        return initHandler;
    }
}
