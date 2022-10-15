package com.lxsx.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxsx.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.lxsx.gulimall.ware.enumm.StockDetailEnum;
import com.lxsx.gulimall.ware.service.WareOrderTaskDetailService;
import com.lxsx.gulimall.ware.service.WareSkuService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.ware.dao.WareOrderTaskDao;
import com.lxsx.gulimall.ware.entity.WareOrderTaskEntity;
import com.lxsx.gulimall.ware.service.WareOrderTaskService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("wareOrderTaskService")
public class WareOrderTaskServiceImpl extends ServiceImpl<WareOrderTaskDao, WareOrderTaskEntity> implements WareOrderTaskService {

    @Resource
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Resource
    private WareSkuService wareSkuService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskEntity> page = this.page(
                new Query<WareOrderTaskEntity>().getPage(params),
                new QueryWrapper<WareOrderTaskEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public WareOrderTaskEntity getByOrderSn(String orderSn) {

        WareOrderTaskEntity wareOrderTaskEntity =
                this.baseMapper.selectOne(new LambdaQueryWrapper<WareOrderTaskEntity>().eq(WareOrderTaskEntity::getOrderSn, orderSn));

        return wareOrderTaskEntity;
    }



    @Override
    @Transactional
    public void updateTaskAndWare(String orderSn) {
        //读库存任务单
        WareOrderTaskEntity wareOrderTaskEntity =
                this.baseMapper.selectOne(new LambdaQueryWrapper<WareOrderTaskEntity>().eq(WareOrderTaskEntity::getOrderSn, orderSn));
        //任务单找到详情单
        Long wareOrderTaskEntityId = wareOrderTaskEntity.getId();
        List<WareOrderTaskDetailEntity> wareOrderTaskDetailEntities = wareOrderTaskDetailService.getByTaskId(wareOrderTaskEntityId);

        for (WareOrderTaskDetailEntity wareOrderTaskDetailEntity : wareOrderTaskDetailEntities) {
            //确认库存是否是锁定状态
            if (wareOrderTaskDetailEntity.getLockStatus()== StockDetailEnum.SKU_LOCK_LOCKED.getCode()) {
                //扣减库存
                Integer changeRow = wareSkuService.updateWareSku(wareOrderTaskDetailEntity.getSkuId(),
                        wareOrderTaskDetailEntity.getSkuNum(),
                        wareOrderTaskDetailEntity.getWareId());
                    //修改为以扣减库存状态
                    wareOrderTaskDetailEntity.setLockStatus(StockDetailEnum.SKU_LOCK_DECREASE.getCode());
                    wareOrderTaskDetailService.updateById(wareOrderTaskDetailEntity);

            }

        }

    }

}