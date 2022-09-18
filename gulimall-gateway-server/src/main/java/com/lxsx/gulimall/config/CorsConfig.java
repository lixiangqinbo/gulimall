//package com.lxsx.gulimall.config;
//
//import org.springframework.boot.SpringBootConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//
///**
// * 开发阶段的跨域解决方案 可优化到配置文件的形式
// */
//@Configuration
//public class CorsConfig {
//    @Bean
//    public CorsWebFilter addCorsMappings(){
//        // 基于url跨域，选择reactive包下的
//        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource
//                = new UrlBasedCorsConfigurationSource();
//        //跨域配置信息 也可以以配置文件的形式
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        //配置允许跨域的请求头
//        corsConfiguration.addAllowedHeader("*");
//        //配置允许跨域的请求方式
//        corsConfiguration.addAllowedMethod("*");
//        //允许跨域的请求来源
//        corsConfiguration.addAllowedOrigin("*");
//        // 是否允许携带cookie跨域
//        corsConfiguration.setAllowCredentials(true);
//        // 任意url都要进行跨域配置
//        urlBasedCorsConfigurationSource.registerCorsConfiguration(
//                "/**",corsConfiguration);
//        return new CorsWebFilter(urlBasedCorsConfigurationSource);
//    }
//}
