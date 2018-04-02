package com.hzz.cache;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by hongshuiqiao on 2017/10/10.
 */
public class DelayItem<T> implements Delayed {
    private long expire;
    private T value;

    public DelayItem(long expire, T value) {
        this.expire = System.currentTimeMillis()+expire;
        this.value = value;
    }

    public void expire(long expire){
        this.expire=System.currentTimeMillis()+expire;
    }

    public T getValue() {
        return value;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert((expire-System.currentTimeMillis()),TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long delay1 = this.getDelay(TimeUnit.MILLISECONDS);
        long delay2 = o.getDelay(TimeUnit.MILLISECONDS);
        if(delay1>delay2)
            return 1;
        else if(delay1<delay2)
            return -1;
        return 0;
    }
}
