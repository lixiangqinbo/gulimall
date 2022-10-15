package com.lxsx.gulimall.mq;

import lombok.Data;

@Data
public class StockLockedTo {

    private Long id;//库存工单的ID

    private StockDetailTo detailTo;
}
