package com.hzz.common.dao.annotation;

import java.lang.annotation.*;

/**
 * 标识模型对应的表
 * Created by hongshuiqiao on 2017-06-09.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    /**
     * 对应的表名，默认与类名称一致
     * @return
     */
    String value() default "";
}
