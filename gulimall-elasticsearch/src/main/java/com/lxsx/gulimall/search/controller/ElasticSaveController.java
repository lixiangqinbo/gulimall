package com.lxsx.gulimall.search.controller;

import com.lxsx.gulimall.exception.BizCodeEnume;
import com.lxsx.gulimall.search.service.SpuUpService;
import com.lxsx.gulimall.to.es.SkuEsModel;
import com.lxsx.gulimall.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search/save")
@Slf4j
public class ElasticSaveController {

    @Autowired
    private SpuUpService spuUpService;

    @PostMapping("/product")
    public R spuStatusUp(@RequestBody List<SkuEsModel> skuEsModels){
        boolean res = false;
        try {
            res = spuUpService.spuStatusUp(skuEsModels);
        } catch (Exception e) {
            log.info("SearchController商品上架错误！：{}",e);
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (res) {
            return R.ok();
        }else {
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }
    }

}
