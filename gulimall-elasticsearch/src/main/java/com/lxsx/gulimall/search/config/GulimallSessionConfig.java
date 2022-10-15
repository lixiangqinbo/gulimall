package com.lxsx.gulimall.search.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.lxsx.gulimall.constant.AuthServerContants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;


/**
 * @Description: springSession配置类
 * @createTime: 2020-06-29 13:36
 **/

@Configuration
public class GulimallSessionConfig {

    //配置JSESSIONID的作用域
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setCookieName(AuthServerContants.JSESSION_NAME);
        defaultCookieSerializer.setDomainName(AuthServerContants.COOKIE_DOMAIN); //将JSESSIONID作用域提高到父域名
        return defaultCookieSerializer;
    }

    //配置序列化类型为JSON
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }

}
