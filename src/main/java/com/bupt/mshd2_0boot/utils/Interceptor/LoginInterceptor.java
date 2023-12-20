package com.bupt.mshd2_0boot.utils.Interceptor;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.bupt.mshd2_0boot.utils.ThreadLocal.UserHolder;

/**
 * 登录拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 1. 判断是否需要拦截（ThreadLocal中是否有用户）
        if (UserHolder.getUser() == null) {
            // 没有，设置状态码，拦截
            response.setStatus(401);
            // 拦截
            return false;
        }
        // 有用户,放行（刷新交给前面的拦截器了）
        return true;
    }
}