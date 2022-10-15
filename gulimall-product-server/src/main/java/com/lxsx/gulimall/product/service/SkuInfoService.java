package com.lxsx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.product.to.SkuPriceTo;
import com.lxsx.gulimall.product.vo.skuinfovo.SkuItemVo;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.product.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:55
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    SkuItemVo querySkuAllinfo(Long skuId) throws ExecutionException, InterruptedException;

    List<SkuPriceTo> querySkuPriceByskuIds(List<Long> skuIds);

    List<SkuInfoEntity> querySkuInfoBySkuIds(List<Long> skuIds);
}

