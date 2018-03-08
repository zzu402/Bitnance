package com.hzz.common.dao.annotation;

import java.lang.annotation.*;

/**
 * 标识属性对应的列
 * Created by hongshuiqiao on 2017-06-09.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    /**
     * columnName，默认为fieldName
     * @return
     */
    String value() default "";

    /**
     * 是否主键
     * @return
     */
    boolean pk() default false;
}
