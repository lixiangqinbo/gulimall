package com.lxsx.gulimall.order.component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.lxsx.gulimall.order.config.AlipayProperties;
import com.lxsx.gulimall.order.web.vo.PayVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Component
public class AliPayTemplates {
    @Resource
    private AlipayClient alipayClient;
    @Resource
    private AlipayProperties alipayProperties;

    /**
     * 支付方法
     */
    public  String pay(PayVo vo) throws AlipayApiException {
        /**
         *2、创建一个支付请求
         * 设置请求参数
         */
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(alipayProperties.getReturn_url());
        alipayRequest.setNotifyUrl(alipayProperties.getNotify_url());
        /**
         * 商户订单号，商户网站订单系统中唯一订单号，必填
         */
        String out_trade_no = vo.getOut_trade_no();
        /**
         * 付款金额，必填
         */
        String total_amount = vo.getTotal_amount();
        /**
         *订单名称，必填
         */
        String subject = vo.getSubject();
        /**
         *商品描述，可空
         */
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+alipayProperties.getTimeout()+"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();
        /**
         * 会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
         */
        System.out.println("支付宝的响应："+result);

        return result;

    }

    /**
     * alipay.trade.page.pay(统一收单下单并支付页面接口)
     * @param vo
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradePagePayResponse payOrder(PayVo vo) throws AlipayApiException {

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayProperties.getNotify_url());
        request.setReturnUrl(alipayProperties.getReturn_url());
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", vo.getOut_trade_no());
        bizContent.put("total_amount", vo.getTotal_amount());
        bizContent.put("subject", vo.getSubject());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        bizContent.put("timeout_express", alipayProperties.getTimeout());

        //// 商品明细信息，按需传入
        //JSONArray goodsDetail = new JSONArray();
        //JSONObject goods1 = new JSONObject();
        //goods1.put("goods_id", "goodsNo1");
        //goods1.put("goods_name", "子商品1");
        //goods1.put("quantity", 1);
        //goods1.put("price", 0.01);
        //goodsDetail.add(goods1);
        //bizContent.put("goods_detail", goodsDetail);

        //// 扩展信息，按需传入
        //JSONObject extendParams = new JSONObject();
        //extendParams.put("sys_service_provider_id", "2088511833207846");
        //bizContent.put("extend_params", extendParams);

        request.setBizContent(bizContent.toString());
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        return response;
    }
    /**
     * https://opendocs.alipay.com/open/028wob
     * alipay.trade.close(统一收单交易关闭接口)
     * 关闭订单
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    public boolean closeOrder(String orderSn) throws AlipayApiException {

        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderSn);
        request.setBizContent(bizContent.toString());
        AlipayTradeCloseResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            return true;
        } else {
            return false;
        }

    }
    /**
     * https://opendocs.alipay.com/open/028woa
     * alipay.trade.query(统一收单线下交易查询)
     * 查询订单
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradeQueryResponse queryOrder(String orderSn) throws AlipayApiException {

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderSn);
        //bizContent.put("trade_no", "2014112611001004680073956707");
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = alipayClient.execute(request);

        return response;
    }

    /**
     * alipay.trade.refund(统一收单交易退款接口)
     * https://opendocs.alipay.com/open/028sm9
     * @param orderSn
     * @param money
     * @param reason
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradeRefundResponse refundOrder(String orderSn, BigDecimal money,String reason) throws AlipayApiException {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        JSONObject bizContent = new JSONObject();
       // bizContent.put("trade_no", orderSn);
        bizContent.put("refund_amount", money);
        bizContent.put("out_trade_no", orderSn);
        /**
         * 退款原因
         */
        bizContent.put("refund_reason", reason);
        /**
         * 退款请求号。
         * 标识一次退款请求，需要保证在交易号下唯一，如需部分退款，则此参数必传。
         * 注：针对同一次退款请求，如果调用接口失败或异常了，重试时需要保证退款请求号不能变更，防止该笔交易重复退款。支付宝会保证同样的退款请求号多次请求只会退一次。
         */
        bizContent.put("out_request_no", orderSn);

        // 返回参数选项，按需传入
        JSONArray queryOptions = new JSONArray();
        /**
         * 交易在支付时候的门店名称
         */
        queryOptions.add("store_name");
        /**
         * 买家在支付宝的用户id
         */
        queryOptions.add("buyer_user_id");
        /**
         * 本次商户实际退回金额。
         * 说明：如需获取该值，需在入参query_options中传入 refund_detail_item_list。
         */
        queryOptions.add("send_back_fee");
        /**
         * 退款使用的资金渠道。
         * 只有在签约中指定需要返回资金明细，或者入参的query_options中指定时才返回该字段信息。
         */
        queryOptions.add("refund_detail_item_list");
        bizContent.put("query_options", queryOptions);

        request.setBizContent(bizContent.toString());
        AlipayTradeRefundResponse response = alipayClient.execute(request);
        return response;
    }

    /**
     * alipay.trade.fastpay.refund.query(统一收单交易退款查询)
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradeFastpayRefundQueryResponse queryRefundOrder(String orderSn) throws AlipayApiException{
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderSn);
       // bizContent.put("out_request_no", "HZ01RF001");

        //// 返回参数选项，按需传入
        //JSONArray queryOptions = new JSONArray();
        //queryOptions.add("refund_detail_item_list");
        //bizContent.put("query_options", queryOptions);

        request.setBizContent(bizContent.toString());
        AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);

        return response;
    }
}
