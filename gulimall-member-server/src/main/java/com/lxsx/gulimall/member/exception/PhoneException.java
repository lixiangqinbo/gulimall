package com.lxsx.gulimall.member.exception;

public class PhoneException extends RuntimeException {

    public PhoneException(){
        super("注册手机已经存在，校验不通过");
    }
}
