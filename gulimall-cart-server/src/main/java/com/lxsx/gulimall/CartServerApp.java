package com.lxsx.gulimall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.lxsx.gulimall")
@EnableRedisHttpSession(redisNamespace = "gulimall.com:session")
public class CartServerApp {
    public static void main(String[] args) {
        SpringApplication.run(CartServerApp.class,args);
    }
}
