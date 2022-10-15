package com.lxsx.gulimall.ware;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
@EnableRabbit
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableRedisHttpSession(redisNamespace = "gulimall.com:session")
//@EnableAutoDataSourceProxy    // 开启seata
public class WareServerAPP {
    public static void main(String[] args) {
        SpringApplication.run(WareServerAPP.class, args);
    }
}
