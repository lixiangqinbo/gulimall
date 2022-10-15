package com.lxsx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.product.vo.skuinfovo.SkuItemSaleAttrVo;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.product.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:55
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuSaleAttrValue(List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities);

    List<SkuItemSaleAttrVo> querySkuSaleValueBySkuId(Long skuId);

    List<String> querySkuSaleValuesBySkuId(Long skuId);


}

