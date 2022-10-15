package com.lxsx.gulimall.auth.config;

import com.lxsx.gulimall.auth.constant.AuthServerContants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * 配置RedisSession
 * 1，配置全局的session作用域
 * 2. 配置session序列化到Redis的格式
 */
@Configuration
public class SessionConfig {

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
