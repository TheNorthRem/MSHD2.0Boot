package com.bupt.mshd2_0boot.config;

import com.bupt.mshd2_0boot.utils.Interceptor.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 前置拦截器 order 0 先执行
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).
                order(0);
    }
}