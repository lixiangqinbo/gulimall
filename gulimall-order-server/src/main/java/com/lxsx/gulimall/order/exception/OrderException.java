package com.lxsx.gulimall.order.exception;

public class OrderException extends Exception {

    public OrderException(){
        super("订单验证不通过！");
    }
}
