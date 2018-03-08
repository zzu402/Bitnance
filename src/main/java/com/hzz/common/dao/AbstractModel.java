package com.hzz.common.dao;

import com.hzz.exception.CommonExceptionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hongshuiqiao on 2017/6/14.
 */
public abstract class AbstractModel<T> implements Cloneable {
    private String groupBy;
    private String orderBy;
    private Integer limitCount;
    private Integer limitOffset;
    private ConditionAnd where = new ConditionAnd();
    private ConditionAnd having = new ConditionAnd();
    //要显示的列，没设置表示显示所有
    private List<String> columns = new ArrayList<>();

    @Override
    public T clone() throws CloneNotSupportedException {
        T clone = null;
        try {
            clone = (T) this.getClass().newInstance();
        } catch (Exception e) {
            throw CommonExceptionHelper.commonRuntimeException("克隆模型时发生异常", e);
        }
        return copyModel(clone,null);
    }

    public T copyDataModel() {
        T clone = null;
        try {
            clone = (T) this.getClass().newInstance();
        } catch (Exception e) {
            throw CommonExceptionHelper.commonRuntimeException("克隆模型时发生异常", e);
        }
        return copyModel(clone, Arrays.asList(new String[]{"groupBy","orderBy","limitCount","limitOffset","where","having","columns"}));
    }

    public T copyModel(T clone, List<String> filterFields) {
        Class<?> type = clone.getClass();
        Field[] fields = type.getDeclaredFields();
        try {
            for (Field field : fields) {
                if(null != filterFields && !filterFields.isEmpty() && filterFields.contains(field.getName()))
                    continue;
//                Class<?> fieldType = field.getType();
                field.setAccessible(true);
                Object fieldValue = field.get(this);
                field.set(clone, fieldValue);
            }
        } catch (IllegalAccessException e) {
            throw CommonExceptionHelper.commonRuntimeException(String.format("克隆模型[%s]时发生异常", type.getName()), e);
        }
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        Class<?> type = obj.getClass();
        if(type != this.getClass())
            return false;
        Field[] fields = type.getDeclaredFields();
        try {
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
                if(!fieldType.isPrimitive()){
                    field.setAccessible(true);
                    Object tValue = field.get(this);
                    Object oValue = field.get(obj);
                    if(null == tValue && null == oValue)
                        continue;
                    else if(null != tValue && null != oValue && tValue.equals(oValue))
                        continue;
                    else
                        return false;
                }
            }
        } catch (IllegalAccessException e) {
            throw CommonExceptionHelper.commonRuntimeException(String.format("比较模型[%s]时发生异常",type.getName()), e);
        }
        return true;
    }

    public String groupBy() {
        return groupBy;
    }

    public String orderBy() {
        return orderBy;
    }

    public Integer limitCount() {
        return limitCount;
    }

    public Integer limitOffset() {
        return limitOffset;
    }

    public void groupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public void orderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public void limitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }

    public void limitOffset(Integer limitOffset) {
        this.limitOffset = limitOffset;
    }

    public List<String> columns() {
        return columns;
    }

    public ConditionAnd where() {
        return where;
    }

    public ConditionAnd having() {
        return having;
    }
}
