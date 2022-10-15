package com.lxsx.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lxsx.gulimall.utils.R;
import com.lxsx.gulimall.ware.constants.RpcStatusCode;
import com.lxsx.gulimall.ware.feign.MemberFeignService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.ware.dao.WareInfoDao;
import com.lxsx.gulimall.ware.entity.WareInfoEntity;
import com.lxsx.gulimall.ware.service.WareInfoService;

import javax.annotation.Resource;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Resource
    private MemberFeignService memberFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        LambdaQueryWrapper<WareInfoEntity> wrapper = new LambdaQueryWrapper();
        String key = (String)params.get("key");
        if (StringUtils.isNotBlank(key)) {
            wrapper.like(WareInfoEntity::getAddress,key).or()
                    .like(WareInfoEntity::getId,key).or()
                    .like(WareInfoEntity::getName,key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

    @Override
    public BigDecimal queryFare(Long memberId) {
        R fare = memberFeignService.fare(memberId);
        BigDecimal data =new BigDecimal(9.9);
        if (fare.getCode()== RpcStatusCode.RPC_SUCCESS_CODE) {
            data = fare.getData(new TypeReference<BigDecimal>() {
            });
        }
        return data;
    }

}