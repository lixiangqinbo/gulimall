package com.lxsx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveProductAttrValues(List<ProductAttrValueEntity> productAttrValueEntities);

    List<ProductAttrValueEntity> querySpuAttrValueList(Long spuId);

    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities);
}

