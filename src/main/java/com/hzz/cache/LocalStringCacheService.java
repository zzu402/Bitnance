package com.hzz.cache;
import com.hzz.exception.CommonExceptionHelper;
import com.hzz.utils.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

/**
 * Created by hongshuiqiao on 2017/10/10.
 */
public class LocalStringCacheService implements ICacheService {
    private Map<String, DelayItem> cacheMap = new ConcurrentHashMap<>();
    private DelayQueue<DelayItem> queue = new DelayQueue<>();

    @Override
    public synchronized void delete(String key) {
        DelayItem item = cacheMap.get(key);
        if (null != item) {
            synchronized (item) {
                item = cacheMap.get(key);
                if (null != item) {
                    queue.remove(item);
                    cacheMap.remove(key);
                }
            }
        }
    }

    @Override
    public synchronized void set(String key, String value, long expire) {
        DelayItem<String> item = cacheMap.get(key);
        if (null != item) {
            delete(key);
        } else {
            item = new DelayItem<>(expire, value);
            cacheMap.put(key, item);
            if (expire > 0) {
                queue.put(item);
            }
        }
    }

    @Override
    public void set(String key, String value) {
        set(key, value, 0L);
    }

    @Override
    public String get(String key) {
        DelayItem item = cacheMap.get(key);
        if (null != item) {
            Object value = item.getValue();
            if (null == value)
                return null;
            if (value instanceof String) {
                return (String) value;
            }
            return JsonMapper.nonEmptyMapper().toJson(value);
        }
        return null;
    }

    @Override
    public void lSet(String key, String value) {
        DelayItem item = cacheMap.get(key);
        if (null != item) {
            Object list = item.getValue();
            synchronized (item) {
                if (list instanceof List) {
                    ((List<String>) list).add(value);
                } else {
                    throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是集合", key), null);
                }
            }
        }
    }

    @Override
    public String lGet(String key, int index) {
        DelayItem item = cacheMap.get(key);
        if (null != item) {
            Object list = item.getValue();
            if (list instanceof List) {
                return ((List<String>) list).get(index);
            } else {
                throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是集合", key), null);
            }
        }
        return null;
    }

    @Override
    public void lDel(String key, String value, int count) {
        DelayItem item = cacheMap.get(key);
        if (null != item) {
            Object list = item.getValue();
            synchronized (item) {
                if (list instanceof List) {
                    if(count<=1){
                        ((List<String>) list).remove(value);
                    }else{
                        for (int i =0; i<count; i++){
                            ((List<String>) list).remove(value);
                        }
                    }
                } else {
                    throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是集合", key), null);
                }
            }
        }
    }

    @Override
    public long lLength(String key) {
        DelayItem item = cacheMap.get(key);
        if (null != item) {
            Object list = item.getValue();
            if (list instanceof List) {
                return ((List<String>) list).size();
            } else {
                throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是集合", key), null);
            }
        }
        return 0;
    }

    @Override
    public List<String> lGetAll(String key) {
        DelayItem item = cacheMap.get(key);
        if (null != item) {
            Object list = item.getValue();
            if (list instanceof List) {
                return ((List<String>) list);
            } else {
                throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是集合", key), null);
            }
        }
        return null;
    }

    @Override
    public void sSet(String key, String value) {
        DelayItem item = cacheMap.get(key);
        if (null != item) {
            Object set = item.getValue();
            synchronized (item) {
                if (set instanceof Set) {
                    ((Set<String>) set).add(value);
                } else {
                    throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是Set", key), null);
                }
            }
        }
    }

    @Override
    public void sDel(String key, String value) {
        DelayItem item = cacheMap.get(key);
        if (null != item) {
            Object set = item.getValue();
            synchronized (item) {
                if (set instanceof Set) {
                    ((Set<String>) set).remove(value);
                } else {
                    throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是Set", key), null);
                }
            }
        }
    }

    @Override
    public boolean sContain(String key, String value) {
        DelayItem item = cacheMap.get(key);
        if (null != item) {
            Object set = item.getValue();
            if (set instanceof Set) {
                return ((Set<String>) set).contains(value);
            } else {
                throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是Set", key), null);
            }
        }
        return false;
    }

    @Override
    public long sLength(String key) {
        DelayItem item = cacheMap.get(key);
        if (null != item) {
            Object set = item.getValue();
            if (set instanceof Set) {
                return ((Set<String>) set).size();
            } else {
                throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是Set", key), null);
            }
        }
        return 0;
    }

    @Override
    public Set<String> sGetAll(String key) {
        DelayItem item = cacheMap.get(key);
        if (null != item) {
            Object set = item.getValue();
            if (set instanceof Set) {
                return ((Set<String>) set);
            } else {
                throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是Set", key), null);
            }
        }
        return null;
    }

    @Override
    public void mSet(String mapKey, String key, Object value) {
        DelayItem item = cacheMap.get(mapKey);
        if (null != item) {
            Object map = item.getValue();
            synchronized (item) {
                if (map instanceof Map) {
                    ((Map) map).put(key, value);
                } else {
                    throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是HASH", mapKey), null);
                }
            }
        }
    }

    @Override
    public Object mGet(String mapKey, String key) {
        DelayItem item = cacheMap.get(mapKey);
        if (null != item) {
            Object map = item.getValue();
            if (map instanceof Map) {
                Object value = ((Map) map).get(key);
                return value;
            } else {
                throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是HASH", mapKey), null);
            }
        }
        return null;
    }

    @Override
    public Long mIncrement(String mapKey, String key, long deltaValue) {
        DelayItem item = cacheMap.get(mapKey);
        if (null != item) {
            Object object = item.getValue();
            synchronized (item) {
                if (object instanceof Map) {
                    Map map = (Map) object;
                    Object value = map.get(key);
                    if (null == value)
                        throw CommonExceptionHelper.cacheRuntimeException(String.format("%s.%s对应的缓存不存在", mapKey, key), null);
                    if (value instanceof Long) {
                        Long longValue = (Long) value + deltaValue;
                        map.put(key, longValue);
                        return longValue;
                    } else {
                        throw CommonExceptionHelper.cacheRuntimeException(String.format("%s.%s对应的缓存不是long类型", mapKey, key), null);
                    }
                } else {
                    throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是HASH", mapKey), null);
                }
            }
        }
        throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不存在", mapKey), null);
    }

    @Override
    public Double mIncrement(String mapKey, String key, double deltaValue) {
        DelayItem item = cacheMap.get(mapKey);
        if (null != item) {
            Object object = item.getValue();
            synchronized (item) {
                if (object instanceof Map) {
                    Map map = (Map) object;
                    Object value = map.get(key);
                    if (null == value)
                        throw CommonExceptionHelper.cacheRuntimeException(String.format("%s.%s对应的缓存不存在", mapKey, key), null);
                    if (value instanceof Double) {
                        Double doubleValue = (Double) value + deltaValue;
                        map.put(key, doubleValue);
                        return doubleValue;
                    } else {
                        throw CommonExceptionHelper.cacheRuntimeException(String.format("%s.%s对应的缓存不是double类型", mapKey, key), null);
                    }
                } else {
                    throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是HASH", mapKey), null);
                }
            }
        }
        throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不存在", mapKey), null);
    }

    @Override
    public void mDel(String mapKey, String key) {
        DelayItem item = cacheMap.get(mapKey);
        if (null != item) {
            Object map = item.getValue();
            if (map instanceof Map) {
                ((Map) map).remove(key);
            } else {
                throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是HASH", key), null);
            }
        }
    }

    @Override
    public Map mGetAll(String mapKey) {
        DelayItem item = cacheMap.get(mapKey);
        if (null != item) {
            Object map = item.getValue();
            if (map instanceof Map) {
                return ((Map) map);
            } else {
                throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是HASH", mapKey), null);
            }
        }
        return null;
    }

    @Override
    public long mLength(String mapKey) {
        DelayItem item = cacheMap.get(mapKey);
        if (null != item) {
            Object map = item.getValue();
            if (map instanceof Map) {
                return ((Map) map).size();
            } else {
                throw CommonExceptionHelper.cacheRuntimeException(String.format("%s对应的缓存不是HASH", mapKey), null);
            }
        }
        return 0;
    }

    @Override
    public void expire(String key, long expire) {
        DelayItem<String> item = cacheMap.get(key);
        if (null != item) {
            synchronized (item) {
                item = cacheMap.get(key);
                if (null != item) {
                    item.expire(expire);
                }
            }
        }
    }
}
