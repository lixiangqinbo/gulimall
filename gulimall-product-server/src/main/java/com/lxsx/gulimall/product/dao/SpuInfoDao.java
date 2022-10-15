package com.lxsx.gulimall.product.dao;

import com.lxsx.gulimall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxsx.gulimall.product.to.SpuInfoTo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu信息
 * 
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:55
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    List<SpuInfoTo> selectSpuinfoWithBrandName(@Param("skuIds") List<Long> skuIds);
}
