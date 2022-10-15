package com.lxsx.gulimall.order.listener;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.lxsx.gulimall.order.component.AlipayTemplate;
import com.lxsx.gulimall.order.service.OrderService;
import com.lxsx.gulimall.order.web.vo.PayAsyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@Slf4j
public class OrderPayedListener {

    @Resource
    private OrderService orderService;
    @Resource
    private  AlipayTemplate alipayTemplate;

    /**
     * @param
     * @return
     *    @RequestParam
     *    @RequestBody 不行
     *    @PathVariable
     *
     */
    @ResponseBody
    @PostMapping("/order/pay/alipay/success")
    public String handllerAlipayed(@RequestParam Map<String,String> prarms){
       // Map<String, String[]> parameterMap = request.getParameterMap();
        String toJSONString = JSON.toJSONString(prarms);
        PayAsyncVo payAsyncVo = JSON.parseObject(toJSONString, PayAsyncVo.class);
        log.info("支付宝支付异步数据:"+payAsyncVo.toString());

        try {
            Boolean res = checkPayed(payAsyncVo, prarms);
            if (res) {
                return "success";
            }else{
                return "fail";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    public Boolean checkPayed(PayAsyncVo payAsyncVo,Map<String,String> params) throws UnsupportedEncodingException, AlipayApiException {
        //获取支付宝POST过来反馈信息
/*        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }*/
        //验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params,
                alipayTemplate.getAlipay_public_key(),
                alipayTemplate.getCharset(),
                alipayTemplate.getSign_type()); //调用SDK验证签名

        if(signVerified) {//验证成功
            Boolean res = orderService.orderPayedHanlder(payAsyncVo);
            return res;
        }else {//验证失败
            return false;
        }
    }
    /**
     * 支付宝支付异步数据gmt_create:2022-10-05 20:40:42
     * 支付宝支付异步数据charset:utf-8
     * 支付宝支付异步数据gmt_payment:2022-10-05 20:40:48
     * 支付宝支付异步数据notify_time:2022-10-05 20:40:49
     * 支付宝支付异步数据subject:Apple iPhone 13 (A2634) 256GB 星光色 支持移动联通电信5G 双卡双待手机 白色 256GB
     * 支付宝支付异步数据sign:gbmcJL3TOsG7HpG6OAU2t+mPgZo4JubjO8L0snlMn5gEiwoc2RbGHqGfZqEt2D43SAww0CafbD6W+WCiTwU/7
     * +Ms7AjJEN4JX+CLcB2fUhcjsOUIc81TU2ZgWNPhLbY/vzr8ofc2PfZq6B4utld7Bt5obFXBgb6VOQuEbPlNUJvJP30PK6QtzI4Bg0z0xdZ
     * 7NeYkynFmAPlpUOF7YRhK1R7U9fAFl0qQkzs1Csa+gMBU/9XgNtMaiMsXeF5YDTLKPihGE0m67XS4HopkOTcuob1cIaxZn60as8
     * /kQRHCoRiIpbcqSFQqKAgb3WETnfj2QkCRF+NjhLTX5sbV2hxR+A==
     * 支付宝支付异步数据buyer_id:2088622991035962
     * 支付宝支付异步数据body:颜色:白色;版本:256GB
     * 支付宝支付异步数据invoice_amount:6799.00
     * 支付宝支付异步数据version:1.0
     * 支付宝支付异步数据notify_id:2022100500222204048035960521825650
     * 支付宝支付异步数据fund_bill_list:[{"amount":"6799.00","fundChannel":"ALIPAYACCOUNT"}]
     * 支付宝支付异步数据notify_type:trade_status_sync
     * 支付宝支付异步数据out_trade_no:3af0161fe5249e4e25c7f6ef59508c6b
     * 支付宝支付异步数据total_amount:6799.00
     * 支付宝支付异步数据trade_status:TRADE_SUCCESS
     * 支付宝支付异步数据trade_no:2022100522001435960502040669
     * 支付宝支付异步数据auth_app_id:2021000121677277
     * 支付宝支付异步数据receipt_amount:6799.00
     * 支付宝支付异步数据point_amount:0.00
     * 支付宝支付异步数据app_id:2021000121677277
     * 支付宝支付异步数据buyer_pay_amount:6799.00
     * 支付宝支付异步数据sign_type:RSA2
     * 支付宝支付异步数据seller_id:2088621993663755
     */
}

