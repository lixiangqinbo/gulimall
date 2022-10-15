package com.lxsx.gulimall;


import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@EnableFeignClients
@EnableRedisHttpSession(redisNamespace = "gulimall.com:session")
@SpringBootApplication
@EnableDiscoveryClient
public class ElasticSearchApp {
    public static void main(String[] args) {
        SpringApplication.run(ElasticSearchApp.class, args);
    }
}



