package com.lxsx.gulimall.product.dao;

import com.lxsx.gulimall.product.entity.SkuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxsx.gulimall.product.to.SkuPriceTo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku信息
 * 
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:55
 */
@Mapper
public interface SkuInfoDao extends BaseMapper<SkuInfoEntity> {

    List<SkuPriceTo> selectSkuPriceToList(@Param("skuIds") List<Long> skuIds);

    List<SkuInfoEntity> selectSkuInfoBySkuIdsskuIds(@Param("skuIds") List<Long> skuIds);
}
