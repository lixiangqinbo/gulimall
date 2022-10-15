package com.lxsx.gulimall.exception;

public enum BizCodeEnume {

    UNKONW_EXCETION(10000,"系统未知错误！"),
    VALID_EXCEPTION(10001,"参数格式校验失败！"),
    TO_MANY_REQUEST(10002,"请求流量过大，请稍后再试"),
    SMS_CODE_EXCEPTION(10002,"验证码频率获取太高！"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常！"),
    ACCOUNT_PASSWORD_ERROR(15003,"账号或者密码错误！");
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
