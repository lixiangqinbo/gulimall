package com.lxsx.gulimall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession(redisNamespace = "gulimall.com:session")//开启springSession 将seeesion存储到redis
@SpringBootApplication(scanBasePackages = "com.lxsx.gulimall")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.lxsx.gulimall")
public class ProductServerApp {

    public static void main(String[] args) {
        SpringApplication.run(ProductServerApp.class, args);
    }

}
