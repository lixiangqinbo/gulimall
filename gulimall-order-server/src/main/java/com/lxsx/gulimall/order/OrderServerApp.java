package com.lxsx.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRabbit
@SpringBootApplication(scanBasePackages = "com.lxsx.gulimall.order")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.lxsx.gulimall.order")
//开启springSession 将seeesion存储到redis
@EnableRedisHttpSession(redisNamespace = "gulimall.com:session")
public class OrderServerApp {
    public static void main(String[] args) {
        SpringApplication.run(OrderServerApp.class, args);
    }
}
