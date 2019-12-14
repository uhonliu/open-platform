package com.bsd.comment.server.exception;

import com.opencloud.common.constants.ErrorCode;
import com.opencloud.common.exception.OpenGlobalExceptionHandler;
import com.opencloud.common.model.ResultBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: linrongxin
 * @Date: 2019/9/9 17:13
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class CustomerOpenGlobalExceptionHandler extends OpenGlobalExceptionHandler {
    /**
     * validate异常处理
     *
     * @param bindException
     * @return
     */
    @ExceptionHandler(BindException.class)
    public Object bindException(BindException bindException) {
        FieldError fieldError = bindException.getBindingResult().getFieldError();
        assert fieldError != null;
        log.error(fieldError.getField() + ":" + fieldError.getDefaultMessage());
        return ResultBody.failed().code(ErrorCode.ALERT.getCode()).msg(fieldError.getDefaultMessage());
    }

    /**
     * validate异常处理
     *
     * @param methodArgumentNotValidException
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object methodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        FieldError fieldError = methodArgumentNotValidException.getBindingResult().getFieldError();
        assert fieldError != null;
        log.error(fieldError.getField() + ":" + fieldError.getDefaultMessage());
        return ResultBody.failed().code(ErrorCode.ALERT.getCode()).msg(fieldError.getDefaultMessage());
    }
}
