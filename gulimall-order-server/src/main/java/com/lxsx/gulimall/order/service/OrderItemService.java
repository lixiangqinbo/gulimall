package com.lxsx.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.order.web.to.SpuInfoTo;
import com.lxsx.gulimall.order.web.vo.CartItemVo;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.order.entity.OrderItemEntity;

import java.util.List;
import java.util.Map;

/**
 * 订单项信息
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 13:41:32
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveItems(List<CartItemVo> cartItemVos, String orderSn);

    List<OrderItemEntity> queryOrderItems(String orderSn);

    Map<Long, SpuInfoTo> getSpuinfo (List<CartItemVo> cartItemVos);

    SpuInfoTo getSpuinfoBySkuId (Long skuId);
}

