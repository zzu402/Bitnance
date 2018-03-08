package com.hzz.common.dao.annotation;

import java.lang.annotation.*;

/**
 * Created by hongshuiqiao on 2017/6/10.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Version {
    /**
     * 对应的属性名
     * @return
     */
    String value() default "";
}
