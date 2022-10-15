package com.lxsx.gulimall.product.dao;

import com.lxsx.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxsx.gulimall.product.vo.skuinfovo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:55
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {


    List<SkuItemSaleAttrVo> selectSkuSaleValueBySkuId(@Param("spuId") Long spuId);

    List<String> selectSkuSaleValuesBySkuId(@Param("skuId") Long skuId);
}
