package com.hzz.utils;

import com.hzz.cache.DelayItem;
import com.hzz.exception.CommonException;
import com.hzz.exception.CommonExceptionHelper;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * 因为同一个字符串不同创建过程可能产生多个对象，最终结果很可能并不是同一个对象
 * 此方法目的是为了避免出现锁对象不一致
 * 注意：这是单机版，如果是多服务器的情况，则还需另外处理
 * Created by hongshuiqiao on 2017/8/30.
 */
public class StringLockRunner {
    //用于放置锁对象
    private static Map<String, LockItem<Object>> lockMap = new ConcurrentHashMap<>();
    private static DelayQueue<LockItem<Object>> queue = new DelayQueue<>();
    static {
        Thread thread = new Thread("StringLock"){
            @Override
            public void run() {
                while (true){
                    LockItem<Object> item = null;
                    try {
                        item = queue.poll(500, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        if(Thread.currentThread().isInterrupted())
                            break;
                    }
                    if(null == item)
                        continue;
                    removeLock(item.getKey());
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public static void syncRun(String lockKey, Runnable runnable){
        Object lock = getLock(lockKey);
        synchronized (lock){
            runnable.run();
        }
    }

    public static <T> T syncCall(String lockKey, Callable<T> callable) throws CommonException {
        Object lock = getLock(lockKey);
        synchronized (lock){
            try {
                return callable.call();
            } catch (Exception e) {
                if (e instanceof CommonException) {
                    throw (CommonException) e;
                }else{
                    throw CommonExceptionHelper.commonException("同步调用时出错",e);
                }
            }
        }
    }

    private static Object getLock(String lockKey) {
        LockItem<Object> lockItem = lockMap.get(lockKey);
        if(null==lockItem) {
            synchronized (StringLockRunner.class){
                lockItem = lockMap.get(lockKey);
                if(null==lockItem) {
                    //5分钟内没有再次被使用则删除
                    lockItem = new LockItem<Object>(1000L*5*60,lockKey,new Object());
                    queue.put(lockItem);
                    lockMap.put(lockKey,lockItem);
                }
            }
        }else{
            //刷新
            synchronized (lockItem.getValue()){
                queue.remove(lockItem);
                lockItem.expire(1000L*5*60);
                queue.put(lockItem);
            }
        }
        return lockItem.getValue();
    }

    private static void removeLock(String lockKey) {
        LockItem<Object> lockItem = lockMap.get(lockKey);
        if(null!=lockItem) {
            synchronized (lockItem.getValue()){
                queue.remove(lockItem);
                lockMap.remove(lockItem);
            }
        }
    }
}

class LockItem<T> extends DelayItem<T> {
    private String key;
    public LockItem(long expire, String key, T value) {
        super(expire, value);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}