package com.lxsx.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lxsx.gulimall.constant.WareConstant;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.ware.dao.PurchaseDetailDao;
import com.lxsx.gulimall.ware.entity.PurchaseDetailEntity;
import com.lxsx.gulimall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();
        String status = (String)params.get("status");
        String wareId = (String)params.get("wareId");
        String key = (String)params.get("key");
        //AND ... = ?
        wrapper.eq(StringUtils.isNotBlank(status), PurchaseDetailEntity::getStatus, status);
        wrapper.eq(StringUtils.isNotBlank(wareId),PurchaseDetailEntity::getWareId,wareId);
        //AND (... LIKE  ?)
        wrapper.and(StringUtils.isNotBlank(key),w -> {
            w.like(StringUtils.isNotBlank(key),PurchaseDetailEntity::getSkuNum,key).or();
            w.like(StringUtils.isNotBlank(key),PurchaseDetailEntity::getWareId,key).or();
            w.like(StringUtils.isNotBlank(key),PurchaseDetailEntity::getSkuNum,key).or();
            w.like(StringUtils.isNotBlank(key),PurchaseDetailEntity::getSkuId,key).or();
            w.like(StringUtils.isNotBlank(key),PurchaseDetailEntity::getPurchaseId,key);
        });

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

    @Override
    public void updatePurchaseDetailStatus(Long[] purchaseIds) {

        Arrays.stream(purchaseIds).forEach(purchaseId -> {
            LambdaQueryWrapper<PurchaseDetailEntity> wrapper =
                    new LambdaQueryWrapper<PurchaseDetailEntity>().eq(PurchaseDetailEntity::getPurchaseId, purchaseId);
            List<PurchaseDetailEntity> purchaseDetailEntities = this.baseMapper.selectList(wrapper);

            List<PurchaseDetailEntity> collect = purchaseDetailEntities.stream().map(purchaseDetailEntity -> {
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getType());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());

            this.updateBatchById(collect);
        });
    }

}