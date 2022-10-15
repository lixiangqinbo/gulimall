package com.lxsx.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.coupon.dao.SeckillSkuRelationDao;
import com.lxsx.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.lxsx.gulimall.coupon.service.SeckillSkuRelationService;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                new LambdaQueryWrapper<SeckillSkuRelationEntity>().eq(SeckillSkuRelationEntity::getPromotionSessionId, params.get("promotionSessionId"))
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAndRelation(SeckillSkuRelationEntity seckillSkuRelation) {
        this.baseMapper.insert(seckillSkuRelation);

    }

    @Override
    public List<SeckillSkuRelationEntity> queryBySessinIds(List<Long> killSessinIds) {
        List<SeckillSkuRelationEntity> seckillSkuRelationEntities =
                this.baseMapper.selectBySessinIds(killSessinIds);
        return seckillSkuRelationEntities;
    }

}