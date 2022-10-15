package com.lxsx.gulimall.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单消息表
 */
@Data
@TableName("oms_order_msg")
public class OrderMsgEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 消息Id
     */
    private String orderSn;
    /**
     * 消息内容
     */
    private String msgJson;
    /**
     * 消息状态
     * 0新建消息
     * 1以消费
     * 2投递失败
     * 3投递成功
     */
    private  Integer msgStatus;
    /**
     * 原因
     */
    private String cause;
}
