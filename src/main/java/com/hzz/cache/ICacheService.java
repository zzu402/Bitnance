package com.hzz.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hongshuiqiao on 2017/10/10.
 */
public interface ICacheService {
    /**
     * 删除缓存
     * @param key
     */
    void delete(String key);

    /**
     * 设置缓存
     * @param key
     * @param value
     * @param expire
     */
    void set(String key, String value, long expire);

    /**
     * 设置缓存
     * @param key
     * @param value
     */
    void set(String key, String value);

    /**
     * 获取缓存
     * @param key
     * @return
     */
    String get(String key);

    /**
     * 缓存到一个集合中
     * @param key
     * @param value
     */
    void lSet(String key, String value);

    /**
     * 查询缓存集合中第几个元素
     * @param key
     * @param index
     * @return
     */
    String lGet(String key, int index);

    /**
     * 删除集合中的指定元素
     * @param key
     * @param value
     * @param count
     */
    void lDel(String key, String value, int count);

    /**
     * 集合缓存的大小
     * @param key
     * @return
     */
    long lLength(String key);

    /**
     * 返回整个缓存集合
     * @param key
     * @return
     */
    List<String> lGetAll(String key);

    /**
     * 缓存到一个Set中
     * @param key
     * @param value
     */
    void sSet(String key, String value);

    /**
     * 删除Set中的指定元素
     * @param key
     * @param value
     */
    void sDel(String key, String value);

    /**
     * 返回是否已经包含指定值
     * @param key
     * @param value
     * @return
     */
    boolean sContain(String key, String value);

    /**
     * Set缓存的大小
     * @param key
     * @return
     */
    long sLength(String key);

    /**
     * 返回整个Set集合
     * @param key
     * @return
     */
    Set<String> sGetAll(String key);

    /**
     * 缓存成一个Hash
     * @param mapKey
     * @param key
     * @param value
     */
    void mSet(String mapKey, String key, Object value);

    /**
     * 查询一个Hash缓存的指定键的值
     * @param mapKey
     * @param key
     */
    Object mGet(String mapKey, String key);

    /**
     * 对Hash的某个值进行递增或递减操作
     * @param mapKey
     * @param key
     * @param deltaValue
     */
    Long mIncrement(String mapKey, String key, long deltaValue);

    /**
     * 对Hash的某个值进行递增或递减操作
     * @param mapKey
     * @param key
     * @param deltaValue
     */
    Double mIncrement(String mapKey, String key, double deltaValue);

    /**
     * 删除Hash的一个键
     * @param mapKey
     * @param key
     */
    void mDel(String mapKey, String key);

    /**
     * 查询整个Hash缓存
     * @param mapKey
     * @return
     */
    Map mGetAll(String mapKey);

    /**
     * Hash缓存的大小
     * @param mapKey
     * @return
     */
    long mLength(String mapKey);

    /**
     * 刷新缓存
     * @param key
     * @param expire
     */
    void expire(String key, long expire);
}
