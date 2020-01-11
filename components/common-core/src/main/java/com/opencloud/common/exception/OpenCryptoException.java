package com.opencloud.common.exception;

/**
 * 加解密异常
 *
 * @author admin
 */
public class OpenCryptoException extends OpenException {
    private static final long serialVersionUID = 4908906410210213271L;

    public OpenCryptoException() {
    }

    public OpenCryptoException(String msg) {
        super(msg);
    }

    public OpenCryptoException(int code, String msg) {
        super(code, msg);
    }

    public OpenCryptoException(int code, String msg, Throwable cause) {
        super(code, msg, cause);
    }
}
