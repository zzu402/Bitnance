package com.hzz.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/7
 */
public class SqlUtils<T> {

    public String createInsertSql(T entity) {
        String sql = "Insert into ";
        String column = ""; // 列
        String c_values = ""; // 列值
        List<Map<String, Object>> list = getFiledsInfo(entity);
        sql += list.get(0).get("obj_name").toString() + " ";
        for (int i = 0; i < list.size(); i++) {
            //id可以自增长
            if (list.get(i).get("f_name").toString() == "id"&&list.get(i).get("f_value") == null)
                i++;
            if (list.get(i).get("f_value") != null) {
                column += list.get(i).get("f_name") + ",";
                c_values += "'" + list.get(i).get("f_value") + "',";
            }
        }
        sql += "(" + column.substring(0, column.length() - 1) + ") values ("
                + c_values.substring(0, c_values.length() - 1) + ");";

        return sql;
    }

    /**
     * 根据属性名获取属性值
     * */
    protected Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 类名(obj_name)获取属性类型(f_type)，属性名(f_name)，属性值(f_value)的map组成的list
     * */
    protected List getFiledsInfo(Object o) {
        String obj_name = o.getClass().getSimpleName().toString();
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        List<Map> list = new ArrayList();
        Map<String, Object> infoMap;

        for (int i = 0; i < fields.length; i++) {
            infoMap = new HashMap<String, Object>();
            infoMap.put("obj_name", obj_name);
            infoMap.put("f_type", fields[i].getType().toString());
            infoMap.put("f_name", fields[i].getName());
            infoMap.put("f_value", getFieldValueByName(fields[i].getName(), o));
            list.add(infoMap);
        }
        return list;
    }


}
