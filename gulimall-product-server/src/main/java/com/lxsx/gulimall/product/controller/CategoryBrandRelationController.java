package com.lxsx.gulimall.product.controller;

import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.lxsx.gulimall.product.service.BrandService;
import com.lxsx.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.lxsx.gulimall.product.entity.CategoryBrandRelationEntity;
import com.lxsx.gulimall.product.service.CategoryBrandRelationService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.R;



/**
 * 品牌分类关联
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
@Slf4j
@RestController
@Transactional
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表,品牌关联目录
     */
    @RequestMapping("/list")
   //@RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){

        PageUtils page = categoryBrandRelationService.queryPage(params);
        return R.ok().put("data", page);
    }

    @GetMapping("catalog/list/{brandId}")
    public R catalogList(@PathVariable Long brandId){
        List<CategoryBrandRelationEntity> data =
                categoryBrandRelationService.getCatalogList(brandId);
        return R.ok().put("data", data);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   //@RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("data", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        log.info(categoryBrandRelation.toString());
		//获取品牌名字更具Id
        String BrandName = brandService.getNameByBrandId(categoryBrandRelation.getBrandId());
        //获取分类名字
        String catelogName = categoryService.getCatelogById(categoryBrandRelation.getCatelogId());

        categoryBrandRelation.setBrandName(BrandName);

        categoryBrandRelation.setCatelogName(catelogName);

        categoryBrandRelationService.save(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
