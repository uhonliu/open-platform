package com.bsd.comment.server.validation.constraints;

import com.bsd.comment.server.validation.TopicTypeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 主题类型注解
 *
 * @Author: linrongxin
 * @Date: 2019/9/9 17:30
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Constraint(validatedBy = TopicTypeValidator.class)
public @interface TopicType {
    String message() default "topicType num is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
