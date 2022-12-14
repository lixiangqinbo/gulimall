package com.lxsx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.product.to.SpuInfoTo;
import com.lxsx.gulimall.product.vo.productvo.SpuSaveVo;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.product.entity.SpuInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * spu信息
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:55
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void spuUp(Long spuId);

    List<SpuInfoTo> getSpuinfoWithBrandName(List<Long> skuIds);
}

