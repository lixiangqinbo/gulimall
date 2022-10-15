package com.lxsx.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.lxsx.gulimall.product.dao.CategoryBrandRelationDao;
import com.lxsx.gulimall.product.entity.CategoryBrandRelationEntity;
import com.lxsx.gulimall.product.service.CategoryBrandRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lxsx.gulimall.product.entity.CategoryEntity;
import com.lxsx.gulimall.product.service.CategoryService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.R;

import javax.annotation.Resource;


/**
 * 商品三级分类
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
@RestController
@RequestMapping("product/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Resource
    private CategoryBrandRelationDao categoryBrandRelationDao;
    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 查出所有分类以及子分类，以树形结构组装起来
     */
    @RequestMapping("/list")
   //@RequiresPermissions("product:category:list")
    public R listWithTree(){

        List<CategoryEntity> data = categoryService.listWithTree();

        return R.ok().put("data",data);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
   //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
        log.info("新增的信息为："+category.toString());
        boolean res = categoryService.saveCategory(category);
        //categoryService.save(category);
        if (!res){
            return R.error(404, "已经存在改类目！");
        }
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
        categoryBrandRelationService.updateBrandRelation(category);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
		categoryService.removeByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
