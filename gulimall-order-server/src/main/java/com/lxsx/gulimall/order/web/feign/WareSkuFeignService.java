package com.lxsx.gulimall.order.web.feign;

import com.lxsx.gulimall.order.web.vo.WareSkuLockVo;
import com.lxsx.gulimall.to.LockWareSkuTo;
import com.lxsx.gulimall.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareSkuFeignService {

    /**
     * 检查是都有库存
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/getWaresku")
    R getWaresku(@RequestBody List<Long> skuIds);

    /**
     * 锁库存
     * @param lockWareSkuTo
     * @return
     */
    @PostMapping("/ware/waresku/lockWaresku")
    R lockWaresku(@RequestBody List<LockWareSkuTo> lockWareSkuTo);

    /**
     * 锁库存
     * @param wareSkuLockVo
     * @return
     */
    @PostMapping("/ware/waresku/newlockWaresku")
    R newLockWaresku(@RequestBody WareSkuLockVo wareSkuLockVo) ;

    /**
     * 减库存库存
     * @param orderSn
     * @return
     */
    @RequestMapping("/ware/wareordertask/updateTaskAndWare/{orderSn}")
    R updateTaskAndWare(@PathVariable("orderSn")String orderSn);

}
