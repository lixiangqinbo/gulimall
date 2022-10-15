package com.lxsx.gulimall.exception;

/**
 * 查询无信息异常
 */
public class NotFindInfoException extends Exception{
    public NotFindInfoException() {
    }

    public NotFindInfoException(String message) {
        super(message);
    }

    public NotFindInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFindInfoException(Throwable cause) {
        super(cause);
    }

    public NotFindInfoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
