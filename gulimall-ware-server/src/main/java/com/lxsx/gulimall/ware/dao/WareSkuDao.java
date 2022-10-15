package com.lxsx.gulimall.ware.dao;

import com.lxsx.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:04:31
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {


    List<WareSkuEntity> selectWareSkuListByskuIds(@Param("skuIds") List<Long> skuIds);

    Integer lockWaresku(@Param("skuId") Long skuId, @Param("wareId") Long wareId ,@Param("num") Integer num);

    void updateLockedSku(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Integer updateWareSku(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum, @Param("wareId") Long wareId);
}
