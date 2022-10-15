package com.lxsx.gulimall.ware.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class CartItemVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * SKU 信息
     */
    private Long skuId;
    //是否被选中 为结算
    private Boolean check = true;

    private String title;

    private String image;
    /**
     * 商品套餐属性
     */
    private List<String> skuAttrValues;
    //单价
    private BigDecimal price;
    //购买的数量
     private Integer count;
    //总价
    private BigDecimal totalPrice;

    /** 商品重量 **/
    private BigDecimal weight = new BigDecimal("0.085");


}
