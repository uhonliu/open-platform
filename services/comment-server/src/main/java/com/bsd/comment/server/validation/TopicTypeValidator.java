package com.bsd.comment.server.validation;

import com.bsd.comment.server.enums.TopicTypeEnum;
import com.bsd.comment.server.validation.constraints.TopicType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 主题类型校验器
 *
 * @Author: linrongxin
 * @Date: 2019/9/9 17:32
 */
public class TopicTypeValidator implements ConstraintValidator<TopicType, String> {
    @Override
    public void initialize(TopicType constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        for (TopicTypeEnum topicTypeEnum : TopicTypeEnum.values()) {
            if (topicTypeEnum.getCode().equals(s)) {
                return true;
            }
        }
        return false;
    }
}
