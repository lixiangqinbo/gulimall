package com.lxsx.gulimall.ware.vo;

import lombok.Data;

@Data
public class PurchaseDetailsVo {
    /**
     * 采购PurchaseDetail id
     */
    private Long itemId;
    /**
     * 状态
     */
    private  Integer status;
    /**
     * 原因
     */
    private  String reason;
}
