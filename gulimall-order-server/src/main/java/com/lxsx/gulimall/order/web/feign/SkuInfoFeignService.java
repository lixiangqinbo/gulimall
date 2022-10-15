package com.lxsx.gulimall.order.web.feign;

import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@FeignClient("gulimall-product")
public interface SkuInfoFeignService {
    /**
     * 根据skuId 查询 sku的价格
     * @param skuIds
     * @return
     */
    @RequestMapping("/product/skuinfo/querySkuPrice")
    R querySkuPrice(@RequestBody List<Long> skuIds);

    /**
     * 信息 spu_name spu_id spu_iamge brand_name catlog_id
     */
    @RequestMapping("/product/spuinfo/spuinfo")
     R getSpuinfoWithBrandName(@RequestBody List<Long> skuIds);
}
