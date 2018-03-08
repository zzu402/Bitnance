package com.hzz.common.dao;


import com.hzz.common.dao.AbstractModel;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hongshuiqiao on 2017/6/19.
 */
public class ModelHelper {
    private static Map<Class, Map<String, Field>> modelFields = new ConcurrentHashMap<Class, Map<String, Field>>();
    private static List<String> modelProperties = new ArrayList<String>(Arrays.asList(new String[]{"groupBy","orderBy","limitCount","limitOffset","where","having"}));

    public static Object getFieldValue(Object model, String fieldName) throws RuntimeException {
        if(null == model)
            throw new RuntimeException("模型不能为空",null);
        Field field = getField(model.getClass(), fieldName);
        if(null == field)
            return null;
        return getFieldValue(model, field);
    }

    public static Object getFieldValue(Object model, Field field) throws RuntimeException {
        try {
            if(null != field)
                return field.get(model);//取得obj对象这个Field上的值
            return null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("获取%s的属性%s时出错", model.getClass().getName(), field.getName()), e);
        }
    }

    public static void setFieldValue(Object model, Field field, Object fieldValue) throws RuntimeException {
        try {
            field.set(model, fieldValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("设置%s的属性%s时出错", model.getClass().getName(), field.getName()), e);
        }
    }

    public static void mergeModels(Object targetModel, String[] containProperties, String[] filterProperties,  Object... models) throws RuntimeException {
        if(null == models || models.length==0) {
            return;
        }
        List<String> filterList = new ArrayList<String>();
        List<String> containList = new ArrayList<String>();
        if(null!=filterProperties)
            filterList.addAll(Arrays.asList(filterProperties));
        if(null!=containProperties)
            containList.addAll(Arrays.asList(containProperties));
        for (Object model : models) {
            if(targetModel.getClass().isArray()){
                Object[] subModels = (Object[]) model;
                for (Object subModel : subModels) {
                    doMergeModel(targetModel, containList, filterList, subModel);
                }
            }else if (model instanceof Collection) {
                Iterator iterator = ((Collection) model).iterator();
                while (iterator.hasNext()){
                    doMergeModel(targetModel, containList, filterList, iterator.next());
                }
            }else{
                doMergeModel(targetModel, containList, filterList, model);
            }
        }
    }

    public static void mergeModels(Object targetModel, Object... models) throws RuntimeException {
        mergeModels(targetModel, new String[0], new String[0], models);
    }

    private static  <T> void doMergeModel(T targetModel, List<String> containProperties, List<String> filterProperties, Object model) throws RuntimeException {
        if(model instanceof Map) {
            Map map = (Map) model;
            Iterator<Map.Entry> iterator = map.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry next = iterator.next();
                Object key = next.getKey();
                if (key instanceof String) {
                    String propertyName = (String) key;

                    if(!checkPropertyName(propertyName, containProperties, filterProperties))
                        continue;
                    Object value = next.getValue();
                    setFieldValue(targetModel, propertyName, value);
                }
            }
        }else{
            Map<String, Field> fieldMap = getFields(model.getClass());
            Field[] fields = fieldMap.values().toArray(new Field[fieldMap.size()]);
            for (Field field : fields) {
                String propertyName = field.getName();
                if(!checkPropertyName(propertyName, containProperties, filterProperties))
                    continue;
                if(AbstractModel.class.isAssignableFrom(model.getClass()) && modelProperties.contains(propertyName))
                    continue;
                setFieldValue(targetModel, field.getName(), getFieldValue(model, field));
            }
        }
    }

    private static boolean checkPropertyName(String propertyName, List<String> containProperties, List<String> filterProperties){
        if(filterProperties.isEmpty() && !containProperties.isEmpty() && !containProperties.contains(propertyName))
            return false;
        if(containProperties.isEmpty() && !filterProperties.isEmpty() && filterProperties.contains(propertyName))
            return false;
        if(!containProperties.isEmpty() && !filterProperties.isEmpty() && filterProperties.contains(propertyName))
            return false;
        if(!containProperties.isEmpty() && !filterProperties.isEmpty() && !containProperties.contains(propertyName))
            return false;
        return true;
    }

    public static <T> void setFieldValue(T targetModel, String propertyName, Object value) throws RuntimeException {
        if (targetModel instanceof Map) {
            Map map = (Map) targetModel;
            map.put(propertyName, value);
        } else {
            Field field = getField(targetModel.getClass(), propertyName);
            if(null != field)
                setFieldValue(targetModel, field, value);
        }
    }

    public static Object getValue(Object model, String propertyName) throws RuntimeException {
        if (model instanceof Map) {
            Map map = (Map) model;
            return map.get(propertyName);
        } else {
            Field field = getField(model.getClass(), propertyName);
            if(null != field)
                return getFieldValue(model, field);
            return null;
        }
    }

    public static Field getField(Class type, String fieldName) {
        Map<String, Field> fieldMap = getFields(type);
        return fieldMap.get(fieldName);
    }

    public static Map<String, Field> getFields(Class type) {
        Map<String, Field> fieldMap = modelFields.get(type);
        if(null == fieldMap){
            fieldMap = new HashMap<String, Field>();
            Class tType = type;
            while (null != tType && tType != Object.class){
                Field[] fields = tType.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    fieldMap.put(field.getName(), field);
                }
                tType = tType.getSuperclass();
            }
            modelFields.put(type, fieldMap);
        }
        return fieldMap;
    }

    public static <T> T newInstance(Class<T> type) throws RuntimeException {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(String.format("实例例化%s时出错", type.getName()), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("实例例化%s时出错", type.getName()), e);
        }
    }

    public static boolean checkModel(Object model1, Object model2, String[] checkProperties) throws RuntimeException {
        for (String checkProperty : checkProperties) {
            Object value1 = getFieldValue(model1, checkProperty);
            Object value2 = getFieldValue(model2, checkProperty);
            if(null == value1 && null != value2)
                return false;
            if(null != value1 && null == value2)
                return false;
            if(null != value1 && null != value2 && !value1.equals(value2))
                return false;
        }
        return true;
    }

    public static <T> List<T> filterProperties(List models, String property, Class<T> propertyType) throws RuntimeException {
        List<T> list = new ArrayList<T>();
        for (Object model : models) {
            Object fieldValue = getFieldValue(model, property);
            list.add((T) fieldValue);
        }
        return list;
    }

    public static List<Map<String,Object>> copyProperties(List models, String[] containProperties, String[] filterProperties) throws RuntimeException {
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        for (Object model : models) {
            Map<String,Object> map = new LinkedHashMap<String,Object>();
            mergeModels(map, containProperties, filterProperties, model);
            list.add(map);
        }
        return list;
    }
}
