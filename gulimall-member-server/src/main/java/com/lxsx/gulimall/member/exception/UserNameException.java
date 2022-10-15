package com.lxsx.gulimall.member.exception;

public class UserNameException extends RuntimeException{

   public UserNameException(){
        super("用户名已经存在，校验不通过");
    }
}
