package com.lxsx.gulimall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@MapperScan(basePackages ="com.lxsx.gulimall.coupon.dao" )
@EnableDiscoveryClient
@EnableFeignClients
public class CouponServerAPP {

    public static void main(String[] args) {
        SpringApplication.run(CouponServerAPP.class, args);
    }
}
