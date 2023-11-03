package com.bupt.mshd2_0boot.config;

import com.bupt.mshd2_0boot.utils.EnvironmentValue;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Redis配置
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedissonClient redissonClient() {
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + EnvironmentValue.getParamSettings("spring.data.redis.host") + ":" + EnvironmentValue.getParamSettings("spring.data.redis.port"));
        // 创建RedissonClient对象
        return Redisson.create(config);
    }
}
