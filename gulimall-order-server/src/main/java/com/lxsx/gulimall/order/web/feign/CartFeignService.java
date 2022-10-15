package com.lxsx.gulimall.order.web.feign;

import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-cart")
public interface CartFeignService {

    @GetMapping("/queryCartItems/{memberId}")
    R queryCartItems(@PathVariable("memberId") Long memberId);
}
