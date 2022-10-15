package com.lxsx.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.lxsx.gulimall.ware.enumm.StockDetailEnum;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.ware.dao.WareOrderTaskDetailDao;
import com.lxsx.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.lxsx.gulimall.ware.service.WareOrderTaskDetailService;


@Service("wareOrderTaskDetailService")
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailDao, WareOrderTaskDetailEntity> implements WareOrderTaskDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskDetailEntity> page = this.page(
                new Query<WareOrderTaskDetailEntity>().getPage(params),
                new QueryWrapper<WareOrderTaskDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<WareOrderTaskDetailEntity> getByTaskId(Long wareOrderTaskEntityId) {
        List<WareOrderTaskDetailEntity> wareOrderTaskDetailEntities =
                this.baseMapper.selectList(new LambdaQueryWrapper<WareOrderTaskDetailEntity>()
                        .eq(WareOrderTaskDetailEntity::getTaskId, wareOrderTaskEntityId)
                        .eq(WareOrderTaskDetailEntity::getLockStatus, StockDetailEnum.SKU_LOCK_LOCKED.getCode()));

        return wareOrderTaskDetailEntities;
    }

}