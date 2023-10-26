package com.bupt.mshd2_0boot.utils;

public class Utils {
    /**
     * 检查账号长度是否在6-16之间
     * 检查密码长度是否在6-18之间
     * 检查账号密码是否都不为空
     * 检查手机号是否为 11 位
     */
    public static boolean checkData(String username, String password,String phone) {
        if (username == null || password == null || phone==null ) return false;
        return username.length() >= 6 && username.length() <= 16 && password.length() >= 6 && password.length() <= 18 && phone.length()==11;
    }
}
