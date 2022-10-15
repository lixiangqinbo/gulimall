package com.lxsx.gulimall.order.web.vo;

import com.lxsx.gulimall.order.entity.OrderEntity;
import lombok.Data;

import java.util.Map;

@Data
public class SubmitOrderResponseVo {

    private OrderEntity order;

    /** 错误状态码 **/
    private Integer code;

    /**
     * 存库不足的 购物车项key:sku 名字  value：msg
     */
    private Map<Long,String> failCartItems;

}
