package com.lxsx.gulimall.seckill.feign;

import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * 根据skuIds 批量查询 sku信息
     * @param skuIds
     * @return
     */
    @RequestMapping("/product/skuinfo/querySkuInfoBySkuIds")
    R querySkuInfoBySkuIds(@RequestBody List<Long> skuIds);

}
