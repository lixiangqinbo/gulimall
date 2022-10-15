package com.lxsx.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxsx.gulimall.constant.WareConstant;
import com.lxsx.gulimall.ware.entity.PurchaseDetailEntity;
import com.lxsx.gulimall.ware.service.PurchaseDetailService;
import com.lxsx.gulimall.ware.service.WareSkuService;
import com.lxsx.gulimall.ware.vo.MergeVo;
import com.lxsx.gulimall.ware.vo.PurchaseDetailsVo;
import com.lxsx.gulimall.ware.vo.PurchaseFinishVo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.ware.dao.PurchaseDao;
import com.lxsx.gulimall.ware.entity.PurchaseEntity;
import com.lxsx.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Resource
    private WareSkuService wareSkuService;
    @Resource
    private PurchaseDetailService purchaseDetailService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询未分配的采购单
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPurchaseUnreceiveList(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PurchaseEntity::getStatus,WareConstant.PurchaseStatusEnum.CREATE.getType()).or()
                .eq(PurchaseEntity::getStatus,WareConstant.PurchaseStatusEnum.ASSIGNED.getType());
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),queryWrapper);
        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void mergePurchase(MergeVo mergeVo) {

        List<Long> purchaseDetailEntityIds = mergeVo.getItems();
        List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailEntityIds.stream().map(purchaseDetailEntityId -> {
            //找到提交id的记录
            PurchaseDetailEntity purchaseDetailEntity = purchaseDetailService.getById(purchaseDetailEntityId);
            return purchaseDetailEntity;

        }).filter((purchaseDetailEntity -> {
            //如果状态不是新建  ，就不能再被合并
            return purchaseDetailEntity != null && (purchaseDetailEntity.getStatus() == WareConstant.PurchaseStatusEnum.CREATE.getType()||
                    purchaseDetailEntity.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getType());
        })).collect(Collectors.toList());

        if (purchaseDetailEntities==null || purchaseDetailEntities.size()==0) {

            throw new RuntimeException("提交数据异常！");
        }
        Long newPurchaseId =mergeVo.getPurchaseId();
        //如果无采购的id 就需要新建一个
        if (mergeVo.getPurchaseId()==null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATE.getType());
            purchaseEntity.setPriority(WareConstant.PurchaseStatusEnum.DEFAULT_PRIOR.getType());
            this.save(purchaseEntity);
            newPurchaseId = purchaseEntity.getId(); //如果新建立的采购单id 需要保存下面使用
        }

        final Long purchaseId = newPurchaseId;
        List<PurchaseDetailEntity> collect = purchaseDetailEntities.stream().map(purchaseDetailEntity -> {
            purchaseDetailEntity.setPurchaseId(purchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getType());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(collect);
        //更行修改时间
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setUpdateTime(new Date());
        purchaseEntity.setId(purchaseId);
        this.updateById(purchaseEntity);
    }

    @Override
    public void updatePurchaseStatusById(Long[] purchaseIds) {
        Stream<Long> stream = Arrays.stream(purchaseIds);

        List<PurchaseEntity> purchaseEntities = stream.map(purchaseId -> {
            return  this.getById(purchaseId);
        }).filter(purchaseEntity -> {

            return purchaseEntity!=null &&
                    (purchaseEntity.getStatus()==WareConstant.PurchaseStatusEnum.ASSIGNED.getType()||
                            purchaseEntity.getStatus()==WareConstant.PurchaseStatusEnum.CREATE.getType());

        }).map(purchaseEntity -> {
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.ACKONWLEGE.getType());
            return purchaseEntity;
        }).collect(Collectors.toList());

        this.updateBatchById(purchaseEntities);

        //改变采购项的状态
        purchaseDetailService.updatePurchaseDetailStatus(purchaseIds);

    }

    @Override
    @Transactional
    public void updatePurchaseDetailStatus(PurchaseFinishVo purchaseFinishVo) {
       /* {
            "id":3, //Purchase id
            "items":[
                {"itemId":1,"status":3,"reason":""},
                {"itemId":2,"status":4,"reason":"没钱了"}
            ]
        }*/
        List<PurchaseDetailsVo> purchaseDetails = purchaseFinishVo.getItems();
        boolean parchaseRes = true; //决定purchase 表中状态
        for (PurchaseDetailsVo purchaseDetail : purchaseDetails) {
            if (purchaseDetail.getStatus()== WareConstant.PurchaseDetailStatusEnum.HASERROR.getType()) {
                parchaseRes = false;
            }
            PurchaseDetailEntity purchaseDetailEntity = purchaseDetailService.getById(purchaseDetail.getItemId());
            if (purchaseDetailEntity==null) {
                continue;
            }
            purchaseDetailEntity.setStatus(purchaseDetail.getStatus());
            purchaseDetailService.updateById(purchaseDetailEntity);
        }

        PurchaseEntity purchaseEntity = this.getById(purchaseFinishVo.getId());
        if (parchaseRes) {
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.FINISH.getType());
        }else {
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.HASERROR.getType());
        }
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

        //入库
        //过滤出已经成功完成采购的记录
        List<PurchaseDetailsVo> collect = purchaseDetails.stream().filter(item -> {
            return item.getStatus() == WareConstant.PurchaseDetailStatusEnum.FINISH.getType();
        }).collect(Collectors.toList());

        //入库
        wareSkuService.addStock(collect);

    }

}