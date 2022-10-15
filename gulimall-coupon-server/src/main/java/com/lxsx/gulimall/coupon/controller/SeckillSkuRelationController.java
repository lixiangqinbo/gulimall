package com.lxsx.gulimall.coupon.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lxsx.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.lxsx.gulimall.coupon.service.SeckillSkuRelationService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.R;



/**
 * 秒杀活动商品关联
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:01:12
 */
@RestController
@RequestMapping("coupon/seckillskurelation")
public class SeckillSkuRelationController {
    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    /**
     *查询最近3天的秒杀场次
     * 所有有关联的关系信息
     */
    @RequestMapping("/querySecKillSessionRelation")
    public R querySecKillSessionRelation(@RequestBody List<Long> killSessinIds){
        List<SeckillSkuRelationEntity> seckillSkuRelationEntities = seckillSkuRelationService.queryBySessinIds(killSessinIds);

        return R.ok().setData(seckillSkuRelationEntities);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
   //@RequiresPermissions("coupon:seckillskurelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = seckillSkuRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   //@RequiresPermissions("coupon:seckillskurelation:info")
    public R info(@PathVariable("id") Long id){
		SeckillSkuRelationEntity seckillSkuRelation = seckillSkuRelationService.getById(id);

        return R.ok().put("seckillSkuRelation", seckillSkuRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("coupon:seckillskurelation:save")
    public R save(@RequestBody SeckillSkuRelationEntity seckillSkuRelation){

		seckillSkuRelationService.saveAndRelation(seckillSkuRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("coupon:seckillskurelation:update")
    public R update(@RequestBody SeckillSkuRelationEntity seckillSkuRelation){
		seckillSkuRelationService.updateById(seckillSkuRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("coupon:seckillskurelation:delete")
    public R delete(@RequestBody Long[] ids){
		seckillSkuRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
