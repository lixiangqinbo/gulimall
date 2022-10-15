package com.lxsx.gulimall.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootConfiguration
public class RedissonConfig {

    /**
     *
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown")
    RedissonClient redissonClient() throws IOException {

        Config config = new Config();
        //"redis://127.0.0.1:7000"
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}
