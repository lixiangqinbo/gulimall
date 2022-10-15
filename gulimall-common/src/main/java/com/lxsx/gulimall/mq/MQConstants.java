package com.lxsx.gulimall.mq;

public class MQConstants {
    /**
     * 交换机：订单事件
     */
    public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";
    /**
     * 订单关闭队列 死信队列：30min
     */
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    /**
     * 订单关闭队列
     */
    public static final String ORDER_RELEASE_ORDER_QUEUE = "order.release.order.queue";
    /**
     * 订单创建路由键
     */
    public static final String ORDER_CREATE_ORDER = "order.create.order";
    /**
     * 订单关闭路由键
     */
    public static final String ORDER_RELEASE_ORDER = "order.release.order";
    /**
     * 死信时间
     */
    public static final Long MESSAGE_TTL = 60000*5L;



    /**
     * 交换机：库存
     */
    public static final String STOCK_EVENT_EXCHANGE = "stock-event-exchange";

    /**
     * 库存解锁队列 死信队列：50min
     */
    public static final String STOCK_DELAY_QUEUE = "stock.delay.queue";
    /**
     * 锁库存队列
     */
    public static final String STOCK_RELEASE_STOCK_QUEUE = "stock.release.stock.queue";
    /**
     * 已锁库存创建路由键
     */
    public static final String STOCK_LOCKED = "stock.locked";
    /**
     * 释放库存闭路由键
     */
    public static final String STOCK_RELEASE = "stock.release";
    /**
     * 死信时间
     */
    public static final Long STOCK_MESSAGE_TTL = 120000*5L;
    /**
     * 主动关闭点单的路由键
     */
    public static final String STOCK_RELEASE_OTHER = "stock.release.other.#";

    /**
     * 库存释放队列
     */
    public static final String STOCK_REDUCE_STOCK_QUEUE = "stock.reduce.stock.queue";
    /**
     * 库存释放队列路由
     */
    public static final String STOCK_REDUCE = "stock.reduce";

    /**
     * 秒杀订单已经创建
     */
    public static final String SECKILL_ORDER_QUEUE = "seckill.order.queue";
    /**
     * 秒杀订单 路由键
     */
    public static final String SECKILL_ORDER_CREATE = "seckill.order.create";
}
