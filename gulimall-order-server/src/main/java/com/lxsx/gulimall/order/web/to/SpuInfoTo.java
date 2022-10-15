package com.lxsx.gulimall.order.web.to;

import lombok.Data;

@Data
public class SpuInfoTo {

    private Long skuId;
    /**
     * spu_id
     */
    private Long spuId;
    /**
     * spu_name
     */
    private String spuName;
    /**
     * spu_pic
     */
    private String spuPic;
    /**
     * 品牌
     */
    private String brandName;
    /**
     * 商品分类id
     */
    private Long catalogId;
}
