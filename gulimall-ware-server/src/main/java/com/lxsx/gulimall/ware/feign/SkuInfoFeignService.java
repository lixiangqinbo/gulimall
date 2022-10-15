package com.lxsx.gulimall.ware.feign;

import com.lxsx.gulimall.to.SkuInfoTo;
import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "gulimall-product")
public interface SkuInfoFeignService {

    @PostMapping("/product/skuinfo/querySkuInfoEntityById")
    R querySkuInfoEntityById(@RequestBody SkuInfoTo skuInfoTo);
}
