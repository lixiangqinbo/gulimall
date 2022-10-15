package com.lxsx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.product.vo.Catelog2Vo;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //查询带树形的商品目录
    List<CategoryEntity> listWithTree();

    //保存目录
    public boolean saveCategory(CategoryEntity category);

    String getCatelogById(Long catelogId);


    Long[] getCatelogPath(Long attrGroupId);


    Map<String,List<Catelog2Vo>> getCatelogsTree();

    List<CategoryEntity> queryLeve1Catalog();
}

