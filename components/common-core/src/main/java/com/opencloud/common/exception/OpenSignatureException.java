package com.opencloud.common.exception;

/**
 * 签名异常
 *
 * @author admin
 */
public class OpenSignatureException extends OpenException {
    private static final long serialVersionUID = 4908906410210213271L;

    public OpenSignatureException() {
    }

    public OpenSignatureException(String msg) {
        super(msg);
    }

    public OpenSignatureException(int code, String msg) {
        super(code, msg);
    }

    public OpenSignatureException(int code, String msg, Throwable cause) {
        super(code, msg, cause);
    }
}
