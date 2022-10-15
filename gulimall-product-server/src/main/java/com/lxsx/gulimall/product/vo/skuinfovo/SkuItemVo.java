package com.lxsx.gulimall.product.vo.skuinfovo;

import com.lxsx.gulimall.product.entity.SkuImagesEntity;
import com.lxsx.gulimall.product.entity.SkuInfoEntity;
import com.lxsx.gulimall.product.entity.SpuInfoDescEntity;
import com.lxsx.gulimall.web.vo.SeckillInfoVo;
import lombok.Data;

import java.util.List;

/**
 * 一个完整的sku所包含的所有信息
 * 1，sku的基本信息
 * 2.图片
 * 3.销售属性
 * 4。规格参数也就是基础属性
 */
@Data
public class SkuItemVo {
    /**
     * 销售的基本属性
     */
    SkuInfoEntity info;
    /**
     *
     * sku的图片 pms_sku_images 一个sku 对应多个记录
     */
    List<SkuImagesEntity> images;
    /**
     * 所有SKU对应的介绍都是属于SPU的介绍
     *spu的介绍
     */
    private SpuInfoDescEntity desc;
    /**
     * 销售属性
     */
    private List<SkuItemSaleAttrVo> saleAttr;
    /**
     * spu的技术基础属性
     */
    private List<SpuItemGroupAttrVo> groupAttrs;
    /**
     * 当前商品的秒杀优惠
     */
    private SeckillInfoVo seckillSkuVo;
    /**
     * 是否又库存
     */
    private boolean hasStock = true;

}
