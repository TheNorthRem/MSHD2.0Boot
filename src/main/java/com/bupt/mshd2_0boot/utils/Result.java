package com.bupt.mshd2_0boot.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Boolean success;
    private String errorMsg;
    private Object data;
    private Long total;
    private int statusCode; // 新增状态码字段

    public static Result ok() {
        return new Result(true, null, null, null, 200); // 默认状态码为200
    }

    public static Result ok(Object data) {
        return new Result(true, null, data, null, 200);
    }

    public static Result fail(String errorMsg) {
        return new Result(false, errorMsg, null, null, 400); // 默认失败状态码为400
    }
}
