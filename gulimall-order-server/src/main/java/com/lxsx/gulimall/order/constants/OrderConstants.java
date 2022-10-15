package com.lxsx.gulimall.order.constants;

public class OrderConstants {

    public static final int AUTO_CONFIRM_DAY= 7;
    public static final String RESUBMIT_TOKEN_PRE = "order:token";
    public static final String FAIL_SKU_MSG = "库存不足！";
    public static final Long TOKEN_TIME_OUT = 10L;
    //1、验证令牌是否合法【令牌的对比和删除必须保证原子性】
    public static final String DELETE_TOKEN_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    public static final int MEMBER_ADRR_FAIL = 1;
    public static final int ORDER_BUILDER_SUCCESS = 0;
    public static final int MEMBER_ADRR_EMPTY = 4;
    public static final int CHECK_TOKEN_FAIL=5;
    public static final int CHECK_STOKE_FAIL=2;
    public static final int CHECK_STOKE_ERROR=3;
}
