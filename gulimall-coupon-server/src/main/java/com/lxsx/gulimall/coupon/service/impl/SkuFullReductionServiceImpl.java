package com.lxsx.gulimall.coupon.service.impl;

import com.lxsx.gulimall.coupon.entity.MemberPriceEntity;
import com.lxsx.gulimall.coupon.entity.SkuLadderEntity;
import com.lxsx.gulimall.coupon.service.MemberPriceService;
import com.lxsx.gulimall.coupon.service.SkuLadderService;
import com.lxsx.gulimall.to.MemberPrice;
import com.lxsx.gulimall.to.SkuReductionTo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.coupon.dao.SkuFullReductionDao;
import com.lxsx.gulimall.coupon.entity.SkuFullReductionEntity;
import com.lxsx.gulimall.coupon.service.SkuFullReductionService;

import javax.annotation.Resource;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Resource
    private SkuLadderService skuLadderService;
    @Resource
    private MemberPriceService memberPriceService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        //保存sku的优惠，满减等信息 gulimall_sms -> sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        if (skuReductionTo.getFullCount() > 0){
            skuLadderService.saveSkuLadder(skuLadderEntity);
        }
        //保存sms_sku_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        if (skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal(0))==1) {
            this.save(skuFullReductionEntity);
        }

        //保存sms_member_price
        List<MemberPrice> memberPrices = skuReductionTo.getMemberPrice();

        List<MemberPriceEntity> memberPriceEntities = memberPrices.stream().map(memberPrice -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberLevelId(memberPrice.getId());
            memberPriceEntity.setMemberLevelName(memberPrice.getName());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).filter(memberPriceEntity -> {
            return (memberPriceEntity.getMemberPrice()!=null && memberPriceEntity.getMemberPrice().compareTo(new BigDecimal(0))==1);
        }).collect(Collectors.toList());
        memberPriceService.saveMemberPriceEntities(memberPriceEntities);

    }

}