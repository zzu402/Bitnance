package com.hzz.cache;


import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by hongshuiqiao on 2017/10/12.
 */
public class RedisStringCacheService implements ICacheService {
    private StringRedisTemplate redisTemplate;

    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void set(String key, String value, long expire) {
        if(expire>0)
            redisTemplate.opsForValue().set(key, value, expire, TimeUnit.MILLISECONDS);
        else
            redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void lSet(String key, String value) {
        redisTemplate.opsForList().rightPush(key,value);
    }

    @Override
    public String lGet(String key, int index) {
        return redisTemplate.opsForList().index(key, index);
    }

    @Override
    public void lDel(String key, String value, int count) {
        redisTemplate.opsForList().remove(key, count, value);
    }

    @Override
    public long lLength(String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public List<String> lGetAll(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    @Override
    public void sSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    @Override
    public void sDel(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    @Override
    public boolean sContain(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public long sLength(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public Set<String> sGetAll(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public void mSet(String mapKey, String key, Object value) {
        redisTemplate.opsForHash().put(mapKey, key, value);
    }

    @Override
    public Object mGet(String mapKey, String key) {
        Object value = redisTemplate.opsForHash().get(mapKey, key);
        return value;
    }

    @Override
    public Long mIncrement(String mapKey, String key, long deltaValue) {
        return redisTemplate.opsForHash().increment(mapKey, key, deltaValue);
    }

    @Override
    public Double mIncrement(String mapKey, String key, double deltaValue) {
        return redisTemplate.opsForHash().increment(mapKey, key, deltaValue);
    }

    @Override
    public void mDel(String mapKey, String key) {
        redisTemplate.opsForHash().delete(mapKey,key);
    }

    @Override
    public Map mGetAll(String mapKey) {
        return redisTemplate.opsForHash().entries(mapKey);
    }

    @Override
    public long mLength(String mapKey) {
        return redisTemplate.opsForHash().size(mapKey);
    }

    @Override
    public void expire(String key, long expire) {
        redisTemplate.expire(key, expire, TimeUnit.MILLISECONDS);
    }
}
