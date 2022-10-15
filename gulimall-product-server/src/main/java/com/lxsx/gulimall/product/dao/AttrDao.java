package com.lxsx.gulimall.product.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lxsx.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxsx.gulimall.product.vo.skuinfovo.SpuBaseAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 * 
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    IPage<AttrEntity> queryAttrList(IPage<AttrEntity> page,@Param("params") Map<String, Object> params);

    List<AttrEntity> selectByIds(@Param("attrIds") List<Long> attrIds);

    AttrEntity selectByAttrIdAttrType(@Param("attrId") Long attrId, @Param("attrType") int attrType);

    List<SpuBaseAttrVo> selectBaseAttrByGroupIds(@Param("groupIds")List<Long> groupIds, @Param("attrType") int attrType, @Param("spuId") Long spuId);


}
