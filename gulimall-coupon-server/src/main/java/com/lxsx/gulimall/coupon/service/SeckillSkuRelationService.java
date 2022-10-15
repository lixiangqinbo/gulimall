package com.lxsx.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.coupon.entity.SeckillSkuRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:01:12
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAndRelation(SeckillSkuRelationEntity seckillSkuRelation);

    List<SeckillSkuRelationEntity> queryBySessinIds(List<Long> killSessinIds);
}

