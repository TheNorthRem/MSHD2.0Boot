package com.bupt.mshd2_0boot.config;

import com.bupt.mshd2_0boot.utils.Interceptor.LoginInterceptor;
import com.bupt.mshd2_0boot.utils.Interceptor.RefreshTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 跨域配置，下面的url就是前端对应的url
 */
@Configuration
public class WebConfig {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(@NonNull InterceptorRegistry registry) {
                // 登录拦截器 order 1 后执行
                registry.addInterceptor(new LoginInterceptor()).
                        excludePathPatterns("/login/**").
                        order(1);
                // 前置拦截器 order 0 先执行
                registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).
                        order(0);
            }
        };
    }
}