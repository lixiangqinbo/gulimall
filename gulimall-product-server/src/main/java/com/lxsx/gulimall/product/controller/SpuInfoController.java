package com.lxsx.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.lxsx.gulimall.product.to.SpuInfoTo;
import com.lxsx.gulimall.product.vo.productvo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lxsx.gulimall.product.entity.SpuInfoEntity;
import com.lxsx.gulimall.product.service.SpuInfoService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.R;



/**
 * spu信息
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:55
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;


    /**
     * 信息 spu_name spu_id spu_iamge brand_name catlog_id
     */
    @RequestMapping("/spuinfo")
    public R getSpuinfoWithBrandName(@RequestBody List<Long> skuIds){
        List<SpuInfoTo> spuInfos = spuInfoService.getSpuinfoWithBrandName(skuIds);
        return R.ok().setData(spuInfos);
    }


    /**
     * 列表
     * Request URL: http://localhost:8085/api/product/spuinfo/list?t=1663608172966&status=&key=&brandId=0&catelogId=0&page=1&limit=10
     * Request Method: GET
     */
    @RequestMapping("/list")
   //@RequiresPermissions("product:spuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   //@RequiresPermissions("product:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:spuinfo:save")
    public R save(@RequestBody SpuSaveVo vo){
		//spuInfoService.save(spuInfo);
        spuInfoService.saveSpuInfo(vo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /*equest URL: http://localhost:8085/api/product/spuinfo/{spuId}/up
    Request Method: POST*/
    @RequestMapping("/{spuId}/up")
    //@RequiresPermissions("product:spuinfo:delete")
    public R delete(@PathVariable Long spuId){
        spuInfoService.spuUp(spuId);

        return R.ok();
    }

}
