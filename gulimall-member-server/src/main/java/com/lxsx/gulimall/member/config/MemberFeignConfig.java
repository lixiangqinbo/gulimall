package com.lxsx.gulimall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * member服务远程调用其他服务丢失请求头等信息
 *
 */
@Configuration
public class MemberFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                ServletRequestAttributes requestAttributes =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();
                    String cookie = request.getHeader("cookie");
                    requestTemplate.header("cookie", cookie);
                }
            }
        };
        return requestInterceptor;

    }
}
