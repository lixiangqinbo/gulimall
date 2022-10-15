package com.lxsx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.mq.StockDetailTo;
import com.lxsx.gulimall.to.LockWareSkuTo;
import com.lxsx.gulimall.to.SkuWareTo;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.ware.entity.WareSkuEntity;
import com.lxsx.gulimall.ware.exception.WareLockedException;
import com.lxsx.gulimall.ware.vo.PurchaseDetailsVo;
import com.lxsx.gulimall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:04:31
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(List<PurchaseDetailsVo> collect);


    List<SkuWareTo> getWareskuByskuId(List<Long> skuIds);

    List<LockWareSkuTo> lockWareskuByskuId(List<LockWareSkuTo> lockWareSkuTo);

    Boolean lockWaresku(WareSkuLockVo wareSkuLockVo) throws WareLockedException;

    void updateLockedSku(StockDetailTo detailTo);

    Integer updateWareSku(Long skuId, Integer skuNum, Long wareId);
}

