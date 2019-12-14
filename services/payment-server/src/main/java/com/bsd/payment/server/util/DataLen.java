package com.bsd.payment.server.util;

import java.lang.annotation.*;

/**
 * @Author wangyankai
 * @Description 批量校验字段长度工具类
 * @Date 2019-8-28
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DataLen {
    int value();
}
