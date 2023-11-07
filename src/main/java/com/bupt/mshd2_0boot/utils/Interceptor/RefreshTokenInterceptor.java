package com.bupt.mshd2_0boot.utils.Interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.bupt.mshd2_0boot.dto.UserDTO;
import com.bupt.mshd2_0boot.utils.ThreadLocal.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.bupt.mshd2_0boot.utils.RedisConstants.LOGIN_USER_KEY;
import static com.bupt.mshd2_0boot.utils.RedisConstants.LOGIN_USER_TTL;

/**
 * 刷新redis缓存时间使用，0级拦截器
 */
public class RefreshTokenInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 获取token
        String token = request.getParameter("authorization");
        if (StrUtil.isBlank(token)) {   // 没有直接放行
            return true;
        }

        // 加上前缀获取key，获取redis中的用户信息
        String key = LOGIN_USER_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);

        // 判断用户是否存在
        if (userMap.isEmpty()) {  // 不存在直接放行
            return true;
        }

        // 数据提取
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        // 保存到ThreadLocal
        UserHolder.saveUser(userDTO);
        // 刷新有效期
        stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);
        // 放行
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        //退出后移除用户
        UserHolder.removeUser();
    }
}
