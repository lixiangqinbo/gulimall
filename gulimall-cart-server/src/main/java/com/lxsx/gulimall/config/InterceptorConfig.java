package com.lxsx.gulimall.config;

import com.lxsx.gulimall.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * web的配置类
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    /**
     * 注册自己的拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}
