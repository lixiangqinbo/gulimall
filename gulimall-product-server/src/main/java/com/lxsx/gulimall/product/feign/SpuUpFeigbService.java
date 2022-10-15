package com.lxsx.gulimall.product.feign;

import com.lxsx.gulimall.to.es.SkuEsModel;
import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
@FeignClient(value = "gulimall-search")
public interface SpuUpFeigbService {

    @PostMapping("/search/save/product")
    R spuStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
