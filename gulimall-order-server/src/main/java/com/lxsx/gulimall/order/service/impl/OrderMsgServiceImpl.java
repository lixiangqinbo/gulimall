package com.lxsx.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.order.dao.OrserMsgDao;
import com.lxsx.gulimall.order.entity.OrderMsgEntity;
import com.lxsx.gulimall.order.service.OrderMsgService;
import org.springframework.stereotype.Service;

@Service("orderMsgService")
public class OrderMsgServiceImpl extends ServiceImpl<OrserMsgDao, OrderMsgEntity> implements OrderMsgService {

    @Override
    public void updateStatus(String orderSn,String cause,Integer res) {
        OrderMsgEntity orderMsgEntity =
                this.baseMapper.selectOne(new LambdaQueryWrapper<OrderMsgEntity>().eq(OrderMsgEntity::getOrderSn, orderSn));
        orderMsgEntity.setMsgStatus(res);
        orderMsgEntity.setCause(cause);
        this.updateById(orderMsgEntity);
    }
}
