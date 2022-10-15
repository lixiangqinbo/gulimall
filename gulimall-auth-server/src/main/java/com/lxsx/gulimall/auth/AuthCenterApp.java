package com.lxsx.gulimall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession(redisNamespace = "gulimall.com:session") //开启springSession 将seeesion存储到redis
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AuthCenterApp {

    public static void main(String[] args) {
        SpringApplication.run(AuthCenterApp.class,args);
    }
}
