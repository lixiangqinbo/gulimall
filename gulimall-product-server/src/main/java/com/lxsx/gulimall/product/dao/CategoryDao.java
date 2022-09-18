package com.lxsx.gulimall.product.dao;

import com.lxsx.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品三级分类
 * 
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {


    public void save(CategoryEntity category);

    CategoryEntity getCatelogById(@Param("catId") Long catId);

    Long getParentCid(Long categoryId);

}
