package com.lxsx.gulimall.web.controller;

import com.alibaba.fastjson.TypeReference;
import com.lxsx.gulimall.member.constant.RpcStatusCode;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.R;
import com.lxsx.gulimall.web.controller.feign.OrderFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@Slf4j
public class WebOrderController {

    @Resource
    private OrderFeignService orderFeignService;

    @RequestMapping("/memberOrder.html")
    public String OrderList(@RequestParam Map<String,Object> params, Model model){
        R res = orderFeignService.orderList(params);
        if (res.getCode()== RpcStatusCode.RPC_SUCCESS_CODE) {
            PageUtils data = res.getData(new TypeReference<PageUtils>() {
            });
            model.addAttribute("orders",data);
        }

        return "orderList";
    }
}
