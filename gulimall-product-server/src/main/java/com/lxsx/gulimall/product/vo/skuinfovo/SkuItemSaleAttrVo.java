package com.lxsx.gulimall.product.vo.skuinfovo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * sku的所有销售属性
 */
@ToString
@Data
public class SkuItemSaleAttrVo {
    /**
     * id
     */
    private Long attrId;
    /**
     * 属性名字
     */
    private String attrName;
    /**
     * 销售属性
     */
    private  String attrValues1;

    private List<AttrValueWithSkuIdVo> attrValues;
}
