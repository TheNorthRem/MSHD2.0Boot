package com.bupt.mshd2_0boot.utils;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 工具类
 */
@Component
public class Tools {


    /**
     * 检查账号长度是否在6-16之间
     * 检查密码长度是否在6-18之间
     * 检查账号密码是否都不为空
     * 检查手机号是否为 11 位
     */
    public static boolean checkData(String username, String password, String phone) {
        if (username == null || password == null || phone == null) return false;
        return username.length() >= 6 && username.length() <= 16 && password.length() >= 6 && password.length() <= 18 && !isPhoneInvalid(phone);
    }

    /**
     * 检验电话号码是否无效
     */
    public static boolean isPhoneInvalid(String phone) {
        return mismatch(phone);
    }

    private static final List<String> video= Arrays.asList(".mp4",".avi",".mov");

    private static final List<String> image= Arrays.asList(".jpg",".png");

    private static final List<String> sound= Arrays.asList(".mp3",".wav");

    public  static boolean CheckFormat(String loaderType,String format){
        switch (loaderType){
            case "文字":
                if (!format.equals(".txt")) return false;
                break;
            case "图像":
                if(!image.contains(format)) return false;
                break;
            case "音频":
                if(!sound.contains(format)) return false;
                break;

            case"视频":
                if(!video.contains(format)) return false;
                break;
            case"其它":
                return true;
        }

            return true;
    }

    private static boolean mismatch(String str) {
        if (StrUtil.isBlank(str)) {
            return true;
        }
        return !str.matches(ConstData.PHONE_REGEX);
    }

    public static String formatTime(String time) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = inputFormat.parse(time);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String formatDate(String time) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = inputFormat.parse(time);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
