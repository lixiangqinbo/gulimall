package com.lxsx.gulimall.order.config;

import com.lxsx.gulimall.order.web.interceptor.OrderInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MVCConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/detail.html").setViewName("detail.html");
        registry.addViewController("/createForWxNative.html").setViewName("createForWxNative.html");
        registry.addViewController("/confirm.html").setViewName("confirm.html");
        registry.addViewController("/pay.html").setViewName("pay.html");
        registry.addViewController("/list.html").setViewName("list.html");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new OrderInterceptor()).addPathPatterns("/**").excludePathPatterns("/error");
    }
}
