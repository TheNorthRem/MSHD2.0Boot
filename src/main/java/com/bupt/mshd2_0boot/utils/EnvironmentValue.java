package com.bupt.mshd2_0boot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 配置文件中的环境变量
 */

@Component
public class EnvironmentValue {
    private static Environment environment;

    @Autowired
    public void setEnvironment(Environment environment) {
        EnvironmentValue.environment = environment;
    }

    /**
     * 传入key 从配置文件中获取Value
     * <p>
     * 例:
     * EnvironmentValue.getParamSettings("spring.data.redis.expirationTime")
     * <p>
     * 返回值为String 自行对结果的类型进行更改
     */
    public static String getParamSettings(String key) {
        return environment.getProperty(key);
    }
}
