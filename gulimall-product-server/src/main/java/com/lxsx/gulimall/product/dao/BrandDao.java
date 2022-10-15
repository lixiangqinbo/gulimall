package com.lxsx.gulimall.product.dao;

import com.lxsx.gulimall.product.entity.BrandEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 品牌
 * 
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {

    BrandEntity getBrandById(@Param("brandId") Long brandId);

    List<BrandEntity> selectBrandByIds(@Param("brandIds")List<Long> brandIds);
}
