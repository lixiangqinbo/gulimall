package com.lxsx.gulimall.web.feign;

import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-seckill")
public interface SecKillFeignService {

    @GetMapping("/getSeckillSku/{skuId}")
    R getSeckillSku(@PathVariable("skuId") Long skuId);
}
