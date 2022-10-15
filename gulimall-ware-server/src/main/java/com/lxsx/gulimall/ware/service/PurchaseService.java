package com.lxsx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.ware.entity.PurchaseEntity;
import com.lxsx.gulimall.ware.vo.MergeVo;
import com.lxsx.gulimall.ware.vo.PurchaseFinishVo;

import java.util.Map;

/**
 * 采购信息
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:04:31
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPurchaseUnreceiveList(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void updatePurchaseStatusById(Long[] purchaseIds);

    void updatePurchaseDetailStatus(PurchaseFinishVo purchaseFinishVo);
}

