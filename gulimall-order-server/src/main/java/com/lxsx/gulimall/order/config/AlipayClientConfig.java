package com.lxsx.gulimall.order.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayClientConfig {

    @Bean
    public AlipayClient alipayClient(AlipayProperties alipay){

        return new DefaultAlipayClient(alipay.getGatewayUrl(),
                alipay.getApp_id(),
                alipay.getMerchant_private_key()
                , "json",
                alipay.getCharset(),
                alipay.getAlipay_public_key(),
                alipay.getSign_type());
    }
}
