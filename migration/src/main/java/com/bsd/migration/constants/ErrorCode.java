package com.bsd.migration.constants;

/**
 * @Author: linrongxin
 * @Date: 2019/9/30 10:20
 */
public enum ErrorCode {
    OK(0, "success"),
    FAIL(1000, "fail"),
    ALERT(1001, "alert"),
    ERROR(5000, "error");

    private int code;
    private String message;

    ErrorCode() {
    }

    private ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorCode getResultEnum(int code) {
        for (ErrorCode type : ErrorCode.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return ERROR;
    }

    public static ErrorCode getResultEnum(String message) {
        for (ErrorCode type : ErrorCode.values()) {
            if (type.getMessage().equals(message)) {
                return type;
            }
        }
        return ERROR;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
