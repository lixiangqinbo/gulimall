package com.lxsx.gulimall.to;

import lombok.Data;

@Data
public class SkuWareTo {

    private  Long skuId;
    /**
     * 库存数
     */
    private Integer stock;
    /**
     * 是否有库存
     */
    private boolean hasStock;
    /**
     * 是否锁定库存
     */
    private boolean isLock;
}
