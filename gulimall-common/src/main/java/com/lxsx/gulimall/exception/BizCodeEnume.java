package com.lxsx.gulimall.exception;

public enum BizCodeEnume {

    UNKONW_EXCETION(10000,"系统未知错误！"),
    VALID_EXCEPTION(10001,"参数格式校验失败！");
    private int code;
    private String msg;


    private BizCodeEnume(int code, String msg){
        this.code=code;
        this.msg=msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
