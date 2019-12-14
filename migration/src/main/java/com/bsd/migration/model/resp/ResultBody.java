package com.bsd.migration.model.resp;

import com.bsd.migration.constants.ErrorCode;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author: linrongxin
 * @Date: 2019/9/30 10:17
 */
@Data
public class ResultBody<T> implements Serializable {
    /**
     * 响应编码
     */
    private int code = 0;
    /**
     * 提示消息
     */
    private String message;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 响应数据
     */
    private T data;

    /**
     * http状态码
     */
    private int httpStatus;

    /**
     * 附加数据
     */
    private Map<String, Object> extra;

    /**
     * 响应时间
     */
    private long timestamp = System.currentTimeMillis();

    public static ResultBody ok() {
        return new ResultBody().code(ErrorCode.OK.getCode()).msg(ErrorCode.OK.getMessage());
    }

    public static ResultBody failed() {
        return new ResultBody().code(ErrorCode.FAIL.getCode()).msg(ErrorCode.FAIL.getMessage());
    }

    public ResultBody code(int code) {
        this.code = code;
        return this;
    }

    public ResultBody msg(String message) {
        this.message = message;
        return this;
    }
}
