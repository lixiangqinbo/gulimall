package com.lxsx.gulimall.ware.config;

import com.lxsx.gulimall.ware.interceptor.WareInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WareMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      //  registry.addInterceptor(new WareInterceptor()).addPathPatterns("/**");
    }
}
