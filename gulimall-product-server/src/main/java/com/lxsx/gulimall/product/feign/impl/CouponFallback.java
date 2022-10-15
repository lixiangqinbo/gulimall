package com.lxsx.gulimall.product.feign.impl;

import com.lxsx.gulimall.product.feign.CouponFeignService;
import com.lxsx.gulimall.to.SkuReductionTo;
import com.lxsx.gulimall.to.SpuBoundsTo;
import com.lxsx.gulimall.utils.R;
import org.springframework.stereotype.Component;

@Component
public class CouponFallback  implements CouponFeignService {


    @Override
    public R saveSpuBounds(SpuBoundsTo spuBoundsTo) {
        return R.error("服务不可用！");
    }

    @Override
    public R saveSkuReduction(SkuReductionTo skuReductionTo) {
        return R.error("服务不可用！");
    }
}
