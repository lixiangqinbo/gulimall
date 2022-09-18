package com.lxsx.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.lxsx.gulimall.product.vo.AttrEntityVo;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lxsx.gulimall.product.entity.AttrEntity;
import com.lxsx.gulimall.product.service.AttrService;



/**
 * 商品属性
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
@RestController
@RequestMapping("product/attr")
@Slf4j
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 列表:http://localhost:8085/api/product/attr/base/list/0?t=1663396019419&page=1&limit=10&key=
     * 还需要 所属分类名字 和 所属分组名字
     */
    @GetMapping("/list/{catId}/{type}")
   //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catId") Long catId,
                  @PathVariable("type")Long attrType){
//        PageUtils page = attrService.queryPage(params);
        log.info(params.toString()+"::"+catId);
        params.put("catId",catId);
        params.put("attrType",attrType);
        PageUtils page = attrService.queryPageForAttr(params);
        return R.ok().put("page", page);
    }



    /**http://localhost:8085/api/product/attr/info/1?t=1663401632771
     * 返回一个三级目录根路径，所属分组
     * 信息
     */
    @GetMapping("/info/{attrId}")
   //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){

        AttrEntityVo attr = attrService.getDetail(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     * {t: 1663398082310, attrName: "CPU型号", searchType: 1, valueType: 0, icon: "icon", valueSelect: "A14",…}
     * attrGroupId: ""
     * attrName: "CPU型号"
     * attrType: 1
     * catelogId: 225
     * enable: 1
     * icon: "icon"
     * searchType: 1
     * showDesc: 1
     * t: 1663398082310
     * valueSelect: "A14"
     * valueType: 0
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrEntityVo attr){
        log.info("attr"+attr.toString());
//		attrService.save(attr);
        attrService.cascadeSave(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrEntityVo attrVo){
		attrService.cascadeUpdateById(attrVo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.cascadeRemove(Arrays.asList(attrIds));

        return R.ok();
    }

}
