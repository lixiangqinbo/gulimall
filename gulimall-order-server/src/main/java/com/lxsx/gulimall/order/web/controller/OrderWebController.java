package com.lxsx.gulimall.order.web.controller;

import com.alipay.api.AlipayApiException;
import com.lxsx.gulimall.order.constants.OrderConstants;
import com.lxsx.gulimall.order.web.service.OrderWebService;
import com.lxsx.gulimall.order.web.vo.*;
import com.lxsx.gulimall.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@Controller
@Slf4j
public class OrderWebController {

    @Resource
    private OrderWebService orderWebService;

    /**订单信息页
     * http://order.gulimall.com/toTrade
     * GET
     * @return
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = orderWebService.queryOrderConfirm();
        model.addAttribute("confirmOrderData",orderConfirmVo);
        return "confirm";
    }

    /**
     * Request URL: http://order.gulimall.com/submitOrder
     * Request Method: POST
     * 结算结账页面：查看所有的要选中结账的购物项数据
     * 送货地址
     * 发票等...
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes attributes){
        SubmitOrderResponseVo submitOrderResponseVo = orderWebService.submitOrder(orderSubmitVo);
        //查无地址：去地址添加
        switch (submitOrderResponseVo.getCode()){
            case OrderConstants.MEMBER_ADRR_EMPTY:
                attributes.addFlashAttribute("msg", "没有查询到会员地址,请添加会员地址");
                return "redirect:http://order.gulimall.com/toTrade";
            case OrderConstants.MEMBER_ADRR_FAIL:
                attributes.addFlashAttribute("msg", "会员地址获取异常！");
                return "redirect:http://order.gulimall.com/toTrade";
            case OrderConstants.CHECK_STOKE_ERROR:
                attributes.addFlashAttribute("msg", "库存锁定异常！");
                return "redirect:http://order.gulimall.com/toTrade";
            case OrderConstants.CHECK_TOKEN_FAIL:
                attributes.addFlashAttribute("msg", "令牌订单信息过期，请刷新再次提交");
                return "redirect:http://order.gulimall.com/toTrade";
        }

        model.addAttribute("submitOrderResp",submitOrderResponseVo);
        return "pay";
    }

    /**
     * http://order.gulimall.com/aliPayOrder?orderSn=08548c513ce19a7fc6e659d547dc1df2
     * GET
     * @param orderSn 订单号
     * @return 返回一个页面
     */
    @ResponseBody
    @GetMapping(value = "aliPayOrder",produces = "text/html")
    public String aliPayOrder(@RequestParam String orderSn){
        try {
            String res = orderWebService.aliPayOrder(orderSn);
            return res;
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }


    /**
     * // todo 前端无接口
     * (统一收单交易关闭接口)
     * 关闭订单 不是退款
     * @param orderSn
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/closeOrder")
    public R closeOrder(@RequestParam String orderSn) throws AlipayApiException {
        boolean res = orderWebService.closeOrder(orderSn);
        if (res) {
            return R.ok("已成功取消订单:"+orderSn);
        }
        return R.error("取消订单失败:"+orderSn);
    }

    /**
     * // todo 前端无接口
     * (统一收单线下交易查询)
     * 查询订单
     * @param orderSn
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/queryOrder")
    public R queryOrder(@RequestParam String orderSn) throws AlipayApiException {
        String res = orderWebService.queryOrder(orderSn);
       return R.ok().put("data",res);
    }
}
