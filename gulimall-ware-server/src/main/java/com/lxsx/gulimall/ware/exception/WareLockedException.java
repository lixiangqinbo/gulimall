package com.lxsx.gulimall.ware.exception;

public class WareLockedException extends Exception {

    public WareLockedException(Long skuId){
        super(skuId+"库存锁定失败！");
    }

}
