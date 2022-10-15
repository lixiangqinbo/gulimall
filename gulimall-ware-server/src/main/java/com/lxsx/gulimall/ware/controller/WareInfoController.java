package com.lxsx.gulimall.ware.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import com.lxsx.gulimall.to.MemberEntityTo;
import com.lxsx.gulimall.ware.constants.AuthServerContants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lxsx.gulimall.ware.entity.WareInfoEntity;
import com.lxsx.gulimall.ware.service.WareInfoService;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.R;

import javax.servlet.http.HttpSession;


/**
 * 仓库信息
 *
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-13 14:04:31
 */
@RestController
@RequestMapping("ware/wareinfo")
public class WareInfoController {
    @Autowired
    private WareInfoService wareInfoService;




    /**
     * 根据收获地址计算运费
     * Request URL: http://gulimall.com/api/ware/wareinfo/fare?addrId=2
     * Request Method: GET
     * @param addrId
     * @return
     */
    //TODO NULL
    @GetMapping("/fare")
    public R getFare(@RequestParam("addrId") Long addrId, HttpSession session){
        /**
         * 浏览器直接访问：http://gulimall.com/api/ware/wareinfo/fare?addrId=2 可以拿到session数据
         * themeleaf：发送请求
         *  $.get("http://gulimall.com/api/ware/wareinfo/fare?addrId=" + addrId, function (resp) {
         *  拿不到seesion数据 @空指针异常
         */
        MemberEntityTo memberEntityTo = (MemberEntityTo) session.getAttribute(AuthServerContants.LOGIN_USER);
        if (memberEntityTo==null) {
            BigDecimal fare = wareInfoService.queryFare(memberEntityTo.getId());
            return R.ok().put("data",fare);
        }
       return R.error();

    }

    /**
     * 列表
     */
    @RequestMapping("/list")
   //@RequiresPermissions("ware:wareinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareInfoService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   //@RequiresPermissions("ware:wareinfo:info")
    public R info(@PathVariable("id") Long id){
		WareInfoEntity wareInfo = wareInfoService.getById(id);

        return R.ok().put("wareInfo", wareInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:wareinfo:save")
    public R save(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.save(wareInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:wareinfo:update")
    public R update(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.updateById(wareInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:wareinfo:delete")
    public R delete(@RequestBody Long[] ids){
		wareInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
