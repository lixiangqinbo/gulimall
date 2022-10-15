package com.lxsx.gulimall.product.service.impl;

import com.lxsx.gulimall.product.vo.skuinfovo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.product.dao.SkuSaleAttrValueDao;
import com.lxsx.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.lxsx.gulimall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuSaleAttrValue(List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities) {
        this.saveBatch(skuSaleAttrValueEntities);
    }

    @Override
    public List<SkuItemSaleAttrVo> querySkuSaleValueBySkuId(Long spuId) {
        List<SkuItemSaleAttrVo> skuItemSaleAttrVos =
        this.baseMapper.selectSkuSaleValueBySkuId(spuId);

        return skuItemSaleAttrVos;
    }

    @Override
    public List<String> querySkuSaleValuesBySkuId(Long skuId) {

        return this.baseMapper.selectSkuSaleValuesBySkuId(skuId);
    }


}