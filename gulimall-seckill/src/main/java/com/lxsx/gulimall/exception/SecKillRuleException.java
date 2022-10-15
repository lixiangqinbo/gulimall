package com.lxsx.gulimall.exception;

public class SecKillRuleException extends Exception{

    public SecKillRuleException() {
    }

    public SecKillRuleException(String message) {
        super(message);
    }

    public SecKillRuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecKillRuleException(Throwable cause) {
        super(cause);
    }

    public SecKillRuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
