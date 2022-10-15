package com.lxsx.gulimall.seckill.feign;

import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-coupon")
public interface SecKillSessionFeignService {
    /**
     *查询最近3天的秒杀场次
     *
     */
    @RequestMapping("/coupon/seckillsession/querySecKillSession")
    R querySecKillSession();
    /**
     *查询最近3天的秒杀场次
     * 所有有关联的关系信息
     */
    @RequestMapping("/coupon/seckillskurelation/querySecKillSessionRelation")
    R querySecKillSessionRelation(@RequestBody List<Long> killSessinIds);


}
