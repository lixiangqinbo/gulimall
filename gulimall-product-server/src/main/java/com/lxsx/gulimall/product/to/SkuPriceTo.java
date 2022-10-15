package com.lxsx.gulimall.product.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuPriceTo {
    /**
     * 商品skuID
     */
    private Long SKUId;
    /**
     * 商品的价格
     */
    private BigDecimal price;
}
