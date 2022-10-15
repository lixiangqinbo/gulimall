package com.lxsx.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.lxsx.gulimall.mq.MQConstants;
import com.lxsx.gulimall.to.LockWareSkuTo;
import com.lxsx.gulimall.to.SkuWareTo;
import com.lxsx.gulimall.ware.exception.WareLockedException;
import com.lxsx.gulimall.ware.vo.WareSkuLockVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lxsx.gulimall.ware.entity.WareSkuEntity;
import com.lxsx.gulimall.ware.service.WareSkuService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.R;

import javax.annotation.Resource;


/**
 * 商品库存
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:04:31
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * ware/waresku
     * 检查是都有库存
     * @param skuIds
     * @return
     */
    @PostMapping("/getWaresku")
    public R getWaresku(@RequestBody List<Long> skuIds){
        List<SkuWareTo> skuWareTos = wareSkuService.getWareskuByskuId(skuIds);
        return R.ok().setData(skuWareTos);
    }
    /**
     * 锁库存
     * @param lockWareSkuTo
     * @return
     */
    @PostMapping("/lockWaresku")
    R lockWaresku(@RequestBody List<LockWareSkuTo> lockWareSkuTo){
        List<LockWareSkuTo> skuWareTos = wareSkuService.lockWareskuByskuId(lockWareSkuTo);
        return R.ok().setData(skuWareTos);
    }

    /**
     * 锁库存
     * @param wareSkuLockVo
     * @return
     */
    @PostMapping("/newlockWaresku")
    R newLockWaresku(@RequestBody WareSkuLockVo wareSkuLockVo) {
        Boolean res = null;
        try {
            res = wareSkuService.lockWaresku(wareSkuLockVo);
            return R.ok().setData(res);
        } catch (WareLockedException e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }

    }

    /**
     * 列表
     */
    @RequestMapping("/list")
   //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
