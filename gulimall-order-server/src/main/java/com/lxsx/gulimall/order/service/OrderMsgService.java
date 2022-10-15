package com.lxsx.gulimall.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.order.entity.OrderMsgEntity;

public interface OrderMsgService extends IService<OrderMsgEntity> {

    void updateStatus(String orderSn,String cause,Integer res);
}
