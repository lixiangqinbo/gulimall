package com.lxsx.gulimall.order.web.service;

import com.alipay.api.AlipayApiException;
import com.lxsx.gulimall.order.web.vo.OrderConfirmVo;
import com.lxsx.gulimall.order.web.vo.OrderSubmitVo;
import com.lxsx.gulimall.order.web.vo.PayVo;
import com.lxsx.gulimall.order.web.vo.SubmitOrderResponseVo;

import java.util.concurrent.ExecutionException;

public interface OrderWebService {


    OrderConfirmVo queryOrderConfirm() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo);

    String aliPayOrder(String orderSn) throws AlipayApiException;

    boolean closeOrder(String orderSn) throws AlipayApiException;

    String queryOrder(String orderSn) throws AlipayApiException;
}
