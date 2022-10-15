package com.lxsx.gulimall.feign;

import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient("gulimall-product")
public interface SkuInfoFeignService {

    /**
     * 信息
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R querySkuinfo(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skusaleattrvalue/attr/{skuId}")
    R skuSaleAttrValus(@PathVariable("skuId")Long skuId);
}
