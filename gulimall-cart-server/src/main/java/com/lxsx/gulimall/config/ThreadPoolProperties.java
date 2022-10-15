package com.lxsx.gulimall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix = "gulimall.thread")
@Data
public class ThreadPoolProperties {

    private Integer coreSize;
    private Integer keepAlicveTime;
    private Integer maxSize;
}
