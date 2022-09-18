package com.lxsx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lxsx.gulimall.product.dao.CategoryBrandRelationDao;
import com.lxsx.gulimall.product.entity.CategoryBrandRelationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.product.dao.BrandDao;
import com.lxsx.gulimall.product.entity.BrandEntity;
import com.lxsx.gulimall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Resource
    private BrandDao brandDao;

    @Resource
    private CategoryBrandRelationDao categoryBrandRelationDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(key),"brand_id",key)
                .or(StringUtils.isNotBlank(key))
                .like(StringUtils.isNotBlank(key), "name",key );
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),wrapper);

        return new PageUtils(page);
    }

    @Override
    public String getNameByBrandId(Long brandId) {

        return brandDao.getBrandById(brandId).getName();
    }

    @Override
    @Transactional
    public void updateDetail(BrandEntity brand) {
        //保证其表的冗余字段一致
        brandDao.updateById(brand);
        if (!StringUtils.isBlank(brand.getName())) {
            //同步更新关联表的信息
            CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
            categoryBrandRelationEntity.setBrandId(brand.getBrandId());
            categoryBrandRelationEntity.setBrandName(brand.getName());
            categoryBrandRelationDao.updateBrandRelation(categoryBrandRelationEntity);

            //TODO 后续需要更新的表

        }


    }

}