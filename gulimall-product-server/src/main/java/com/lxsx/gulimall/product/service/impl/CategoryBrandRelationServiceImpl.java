package com.lxsx.gulimall.product.service.impl;

import com.lxsx.gulimall.product.dao.BrandDao;
import com.lxsx.gulimall.product.entity.BrandEntity;
import com.lxsx.gulimall.product.entity.CategoryEntity;
import com.lxsx.gulimall.product.service.CategoryService;
import com.lxsx.gulimall.product.vo.BrandEntityVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.product.dao.CategoryBrandRelationDao;
import com.lxsx.gulimall.product.entity.CategoryBrandRelationEntity;
import com.lxsx.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {
    @Resource
    private CategoryBrandRelationDao categoryBrandRelationDao;
    @Resource
    private BrandDao brandDao;
    @Autowired
    private CategoryService categoryService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryBrandRelationEntity> getCatalogList(Long brandId) {

        return categoryBrandRelationDao.getCatalogList(brandId);
    }

    @Override
    public List<BrandEntity> querybrandsBycatId(Long catId) {
        /**
         * 1.关系表--->brandId
         * 2.brand--->brands
         */
        //1.关系表--->brandId
        List<CategoryBrandRelationEntity> CategoryBrandRelationEntities = categoryBrandRelationDao.selectBrandByCatId(catId);
        //2.brand--->brands
        if (CategoryBrandRelationEntities==null || CategoryBrandRelationEntities.size()==0) {
            return null;
        }
        List<Long> brandIds = CategoryBrandRelationEntities.stream().map(categoryBrandRelationEntity -> {
            return categoryBrandRelationEntity.getBrandId();
        }).collect(Collectors.toList());

        if (brandIds==null||brandIds.size()==0) {
            return null;
        }

        List<BrandEntity> brandEntities = brandDao.selectBrandByIds(brandIds);

        return brandEntities;
    }

//    @Caching(evict = {
//            @CacheEvict(value = "category",key = "'getCatelogsTree'"),
//            @CacheEvict(value = "category",key = "'queryLeve1Catalog'"),
//    })
    @CacheEvict(value = "category",allEntries = true)
    @Transactional
    @Override
    public void updateBrandRelation(CategoryEntity category) {
        categoryService.updateById(category);

        CategoryBrandRelationEntity categoryBrandRelationEntity =new
                CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setCatelogName(category.getName());
        categoryBrandRelationEntity.setCatelogId(category.getCatId());
        categoryBrandRelationDao.updateBrandRelation(categoryBrandRelationEntity);
    }

}