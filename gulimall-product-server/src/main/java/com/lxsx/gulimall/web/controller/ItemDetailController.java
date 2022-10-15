package com.lxsx.gulimall.web.controller;

import com.lxsx.gulimall.product.service.SkuInfoService;
import com.lxsx.gulimall.product.vo.skuinfovo.SkuItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@Controller
public class ItemDetailController {

    @Resource
    private SkuInfoService skuInfoService;
    /**
     * 展示skuId的详情
     * @param skuId sku的id
     * @param model 响应模型
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String itemDetail(@PathVariable("skuId") Long skuId, Model model){
        SkuItemVo skuItemVo = null;
        try {
            skuItemVo = skuInfoService.querySkuAllinfo(skuId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("item",skuItemVo);
        return "item";
    }



}
