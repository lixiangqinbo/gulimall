package com.lxsx.gulimall.product.feign;

import com.lxsx.gulimall.product.feign.impl.CouponFallback;
import com.lxsx.gulimall.to.SkuReductionTo;
import com.lxsx.gulimall.to.SpuBoundsTo;
import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "gulimall-coupon",fallback = CouponFallback.class)
public interface CouponFeignService {

    /**
     * 保存
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
