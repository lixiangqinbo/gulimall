package com.lxsx.gulimall.seckill.controller;

import com.lxsx.gulimall.exception.NotFindInfoException;
import com.lxsx.gulimall.exception.SecKillRuleException;
import com.lxsx.gulimall.seckill.service.SecKillSkuService;
import com.lxsx.gulimall.seckill.vo.SeckillSkuRelationEntityVo;
import com.lxsx.gulimall.utils.R;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class SecKillController {

    @Resource
    private SecKillSkuService secKillSkuService;

    @ResponseBody
    @GetMapping("/currentSecKillSkus")
    public R getCurrentSecKillSkus(){
        try {
            List<SeckillSkuRelationEntityVo> seckillSkuRelationEntityVos = secKillSkuService.queryCurrentSeckillSkus();
            return R.ok().put("data",seckillSkuRelationEntityVos);
        } catch (NotFindInfoException e) {
            e.printStackTrace();
        }
        return R.error();
    }
    @ResponseBody
    @GetMapping("/getSeckillSku/{skuId}")
    R getSeckillSku(@PathVariable("skuId") Long skuId){
        SeckillSkuRelationEntityVo seckillSkuRelationEntityVo = secKillSkuService.querySeckillSkuBySkuId(skuId);
        return R.ok().setData(seckillSkuRelationEntityVo);
    }

    //http://seckill.gulimall.com/kill?killId=2-50&key=ce52cdaa00e14fc8a67a42e560bf3690&num=1
    @GetMapping("/kill")
    public String killSku(@RequestParam("killId") String killId,
                     @RequestParam("key") String key,
                     @RequestParam("num") Integer num,
                     Model model){

        try {
            SeckillSkuRelationEntityVo seckillSkuRelationEntityVo = secKillSkuService.killSuk(killId, key, num);
            model.addAttribute("orderSn", seckillSkuRelationEntityVo.getOrderSn());

        } catch (NotFindInfoException e) {
            e.printStackTrace();

        } catch (SecKillRuleException e) {
            e.printStackTrace();

        }
        return "succ";
    }
}
