package com.lxsx.gulimall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.lxsx.gulimall")
@EnableRedisHttpSession(redisNamespace = "gulimall.com:session")
@EnableDiscoveryClient
public class SecKillApp {
    public static void main(String[] args) {
        SpringApplication.run(SecKillApp.class, args);
    }
}
