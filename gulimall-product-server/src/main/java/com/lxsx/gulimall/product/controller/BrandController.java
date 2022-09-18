package com.lxsx.gulimall.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.lxsx.gulimall.valid.AddGroup;
import com.lxsx.gulimall.valid.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lxsx.gulimall.product.entity.BrandEntity;
import com.lxsx.gulimall.product.service.BrandService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
   //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@Validated(value = {AddGroup.class}) @RequestBody BrandEntity brand /*,BindingResult bindingResult*/){
        Map<String,Object> errors = new HashMap<>();

//        if (bindingResult.hasErrors()) { //验证是都有不合法字符
//            //获取校验的结果集
//            bindingResult.getFieldErrors().forEach(fieldError ->{
//                //那个出错的字段
//                String field = fieldError.getField();
//                //出错字段的提示消息
//                String defaultMessage = fieldError.getDefaultMessage();
//                errors.put(field, defaultMessage);
//            });
//            return R.error(400,"请检查自己的提交").put("error", errors);
//        }
		brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改: 由于表中的冗余子字段需要更新多个表中的数据
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated(value = {UpdateGroup.class})@RequestBody BrandEntity brand){
		brandService.updateDetail(brand);


        return R.ok();
    }
    /**
     * 修改状态
     */
    @RequestMapping("/updataStatus")
    //@RequiresPermissions("product:brand:update")
    public R updataStatue(@RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
