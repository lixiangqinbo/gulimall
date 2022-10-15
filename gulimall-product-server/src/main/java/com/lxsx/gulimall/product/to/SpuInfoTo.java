package com.lxsx.gulimall.product.to;

import lombok.Data;

@Data
public class SpuInfoTo {

    /**
     * SELECT t3.sku_id, psi.id AS spu_id,psi.catalog_id,psi.spu_name,pb.`name` AS brand_name FROM pms_spu_info AS psi
     * LEFT JOIN pms_brand AS pb ON psi.brand_id = pb.brand_id
     * LEFT JOIN pms_sku_info AS t3 ON t3.spu_id = psi.id
     * WHERE t3.sku_id IN ( ? )
     */
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
