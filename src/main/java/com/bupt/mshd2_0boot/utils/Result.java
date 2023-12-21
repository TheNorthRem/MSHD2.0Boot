package com.bupt.mshd2_0boot.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.servlet.http.HttpServletResponse;

import java.net.HttpURLConnection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Boolean success;
    private String errorMsg;
    private Object data;
    private Long total;

    // 设置返回状态码
    private static final ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<>();

    public static Result ok() {
        return new Result(true, null, null, null); // 默认状态码为200
    }

    public static Result ok(Object data) {
        return new Result(true, null, data, null);
    }

    public static Result fail(String errorMsg) {
        int statusCode = HttpURLConnection.HTTP_BAD_REQUEST; // 默认状态码为400
        HttpServletResponse response = responseThreadLocal.get();
        // 设置HTTP状态码为400
        if (response != null) {
            response.setStatus(statusCode);
        }
        return new Result(false, errorMsg, null, null); // 默认失败状态码为400
    }

    // 设置 ThreadLocal 中的 HttpServletResponse
    public static void setResponse(HttpServletResponse response) {
        responseThreadLocal.set(response);
    }

    // 清除 ThreadLocal 中的 HttpServletResponse
    public static void clearResponse() {
        responseThreadLocal.remove();
    }
}
