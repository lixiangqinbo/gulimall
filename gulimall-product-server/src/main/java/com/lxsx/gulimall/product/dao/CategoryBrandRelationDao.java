package com.lxsx.gulimall.product.dao;

import com.lxsx.gulimall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 品牌分类关联
 * 
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    List<CategoryBrandRelationEntity> getCatalogList(@Param("brandId") Long brandId);

    void updateBrandRelation(CategoryBrandRelationEntity brandRelationEntity);
}
