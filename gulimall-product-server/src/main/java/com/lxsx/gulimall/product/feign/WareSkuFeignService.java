package com.lxsx.gulimall.product.feign;

import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "gulimall-ware")
public interface WareSkuFeignService {

    @PostMapping("/ware/waresku/getWaresku")
    R getWaresku(@RequestBody List<Long> skuIds);

}
