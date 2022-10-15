package com.lxsx.gulimall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 解决Feign 远程调用丢失请求头问题
 *
 */
@Configuration
public class OrderFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                ServletRequestAttributes requestAttributes =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes!=null) {
                    HttpServletRequest request = requestAttributes.getRequest();
                   /* *
                     * 将之前的老请求头cookie 复制到新RequestTemplate中
                     * 避免feign调用丢失请求头*/

                    String cookie = request.getHeader("cookie");
                    requestTemplate.header("cookie", cookie);
                }
            }
        };

        return requestInterceptor;
    }
}
