package com.lxsx.gulimall.ware.feign;

import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-order")
public interface OrderFeignService {

    @RequestMapping("/order/order/order/{orderSn}")
    R infoByOrderSn(@PathVariable("orderSn")String orderSn);
}
