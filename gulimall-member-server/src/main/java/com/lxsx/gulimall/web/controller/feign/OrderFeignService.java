package com.lxsx.gulimall.web.controller.feign;

import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient("gulimall-order")
public interface OrderFeignService {

    /**
     * 列表
     */
    @RequestMapping("/order/order/orderlist")
    R orderList(@RequestBody Map<String, Object> params);
}
