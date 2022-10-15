package com.lxsx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.product.entity.SpuImagesEntity;

import java.util.Map;

/**
 * spu图片
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuImage(SpuImagesEntity spuImagesEntity);
}

