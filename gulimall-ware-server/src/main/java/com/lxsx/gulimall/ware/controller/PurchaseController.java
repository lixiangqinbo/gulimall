package com.lxsx.gulimall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import com.lxsx.gulimall.ware.vo.MergeVo;
import com.lxsx.gulimall.ware.vo.PurchaseFinishVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lxsx.gulimall.ware.entity.PurchaseEntity;
import com.lxsx.gulimall.ware.service.PurchaseService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.R;



/**
 * 采购信息
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:04:31
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   //@RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }
//
//    Request URL: http://localhost:8085/api/ware/purchase/unreceive/list?t=1663658145289
//    Request Method: GET
    /**
     * 删除
     */
    @GetMapping("/unreceive/list")
    //@RequiresPermissions("ware:purchase:delete")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPurchaseUnreceiveList(params);

        return R.ok().put("page", page);
    }

//    Request URL: http://localhost:8085/api/ware/purchase/merge
//    Request Method: POST

    @PostMapping("/merge")
    //@RequiresPermissions("ware:purchase:delete")
    public R merge(@RequestBody MergeVo mergeVo){

        purchaseService.mergePurchase(mergeVo);

        return R.ok();
    }

    /**
     * 领取采购单
     * @param purchaseIds
     * @return
     */
    @PostMapping("/received")
    //@RequiresPermissions("ware:purchase:delete")
    public R received(@RequestBody Long[] purchaseIds){

        purchaseService.updatePurchaseStatusById(purchaseIds);

        return R.ok();
    }

    // http://localhost:8085/api/ware/purchase/done
    // POST
    /**
     * 领取采购单
     * @param purchaseFinishVo
     * @return
     */
    @PostMapping("/done")
    //@RequiresPermissions("ware:purchase:delete")
    public R done(@RequestBody PurchaseFinishVo purchaseFinishVo){

        purchaseService.updatePurchaseDetailStatus(purchaseFinishVo);

        return R.ok();
    }
}
