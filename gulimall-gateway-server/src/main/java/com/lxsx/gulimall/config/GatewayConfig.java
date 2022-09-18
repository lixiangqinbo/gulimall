//package com.lxsx.gulimall.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.Date;
//
//@Component
//@Slf4j
//public class GatewayConfig implements GlobalFilter, Ordered {
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        log.info("执行了全局的路由过滤器，执行时间"+new Date()+"可以完成全局日志，鉴权等操作！");
//        ServerHttpRequest request = exchange.getRequest();
//        exchange.getResponse().setStatusCode(HttpStatus.ACCEPTED);
//
//        log.info("request携带的信息request.getId()："+request.getId()+"\n"
//                +"request携带的信息request.getMethod()："+request.getMethod()+"\n"
//                +"request携带的信息request.getPath()："+request.getPath()+"\n"
//                +"request携带的信息request.getCookies()："+request.getCookies()+"\n"
//                +"request携带的信息request.getMethodValue()："+request.getMethodValue()+"\n"
//                +"request携带的信息request.getQueryParams()："+request.getQueryParams()+"\n"
//                +"request携带的信息request.getRemoteAddress()："+request.getRemoteAddress()+"\n"
//                +"request携带的信息request.getHeaders()："+request.getHeaders()+"\n"
//                +"request携带的信息request.getURI()："+request.getURI()+"\n"
//                +"request携带的信息request.getBody()："+request.getBody());
//        return chain.filter(exchange);
//    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//}
