package com.lxsx.gulimall.order.enume;

public enum  OrderStatusEnum {
    CREATE_NEW(0,"待付款"),
    PAYED(1,"已付款"),
    SENDED(2,"已发货"),
    RECIEVED(3,"已完成"),
    CANCLED(4,"已取消"),
    SERVICING(5,"售后中"),
    SERVICED(6,"售后完成");
    private Integer code;
    private String msg;

    OrderStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public enum  OrderPayedStatusEnum{

        TRADE_FINISHED("TRADE_FINISHED","交易结束，不可退款"),
        TRADE_SUCCESS("TRADE_SUCCESS","交易支付成功"),
        TRADE_CLOSED("TRADE_CLOSED","未付款交易超时关闭，或支付完成后全额退款"),
        WAIT_BUYER_PAY("WAIT_BUYER_PAY","交易创建，等待买家付款");

        private String res;
        private String msg;

        OrderPayedStatusEnum(String res, String msg) {
            this.res = res;
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        public String getRes() {
            return res;
        }
    }
    public enum  OrderMsgStatusEnum{

        MQ_NEW(0,"新建消息"),
        MQ_CONSUMER(1,"消息已消费"),
        MQ_DELIVERY_FAIL(2,"投递失败"),
        MQ_DELIVERY_SUCCESS(3,"投递成功");


        private Integer code;
        private String msg;

        OrderMsgStatusEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        public Integer getCode() {
            return code;
        }
    }

}

