package com.lxsx.gulimall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

//默认路径实在启动类所在的位置
@SpringBootApplication(scanBasePackages ="com.lxsx.gulimall" )
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.lxsx.gulimall")
@EnableRedisHttpSession(redisNamespace = "gulimall.com:session")
public class MemberServerApp {
    public static void main(String[] args) {
        SpringApplication.run(MemberServerApp.class, args);
    }
}
