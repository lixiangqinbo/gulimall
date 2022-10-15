package com.lxsx.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.lxsx.gulimall.product.to.SkuPriceTo;
import com.lxsx.gulimall.to.SkuInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lxsx.gulimall.product.entity.SkuInfoEntity;
import com.lxsx.gulimall.product.service.SkuInfoService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.R;



/**
 * sku信息
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:55
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   //@RequiresPermissions("product:skuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
   //@RequiresPermissions("product:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:skuinfo:save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:skuinfo:update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:skuinfo:delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

    /**
     * 查询指定id的sku_info
     */
    @PostMapping("/querySkuInfoEntityById")
    //@RequiresPermissions("product:skuinfo:delete")
    public R querySkuInfoEntityById(@RequestBody SkuInfoTo skuInfoTo){
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuInfoTo.getSkuId());
        return R.ok().put("data",skuInfoEntity);
    }

    /**
     * 根据skuId 查询 sku的价格
     * @param skuIds
     * @return
     */
    @RequestMapping("/querySkuPrice")
    public R querySkuPrice(@RequestBody List<Long> skuIds){
        List<SkuPriceTo> priceTos = skuInfoService.querySkuPriceByskuIds(skuIds);
        return R.ok().setData(priceTos);
    }
    /**
     * 根据skuIds 批量查询 sku信息
     * @param skuIds
     * @return
     */
    @RequestMapping("/querySkuInfoBySkuIds")
    public R querySkuInfoBySkuIds(@RequestBody List<Long> skuIds){
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.querySkuInfoBySkuIds(skuIds);
        return R.ok().setData(skuInfoEntities);
    }

}
