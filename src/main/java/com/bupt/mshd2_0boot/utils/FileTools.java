package com.bupt.mshd2_0boot.utils;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.function.BiFunction;

/**
 * 操作文件的工具类
 */
@Slf4j
public class FileTools {
    /**
     * 解析文件(失败抛出异常)
     *
     * @param path          文件路径
     * @param type          反射时需要使用的Class
     * @param isDelete      解析后是否要删除文件
     * @param parseFunction 将字符串反射成对应类的方法
     * @param <T>           需要反射出来的类
     * @return 反射对应出来的实体类数组
     * @throws IOException 没有对应文件的异常|路径是目录的异常|文件权限不足地异常|反射解析失败的异常
     */
    public static <T> List<T> parseFile(Path path, Class<T> type, boolean isDelete, BiFunction<String, Class<T>, List<T>> parseFunction) throws IOException {
        // 文件不存在
        if (!Files.exists(path)) {
            log.error("对应路径:{}下没有对应文件", path);
            throw new NoSuchFileException(path.toString());
        }

        // 路径是个目录
        if (Files.isDirectory(path)) {
            throw new NoSuchFileException("The path is a directory");
        }

        // 获取文件内容
        String content = new String(Files.readAllBytes(path));
        // 反射到类上
        List<T> ans = parseFunction.apply(content, type);

        // 删除文件
        if (isDelete) {
            Files.delete(path);
        }

        return ans;
    }

    /**
     * 反射JSON字符串到类上
     *
     * @param content JSON字符串
     * @param type    需要反射类的Class
     * @param <T>     反射出来的类
     * @return 反射出来的类数组
     */
    public static <T> List<T> parseJSON(String content, Class<T> type) {
        return JSON.parseArray(content, type);
    }

    /**
     * 反射XML字符串到类上
     *
     * @param content XML字符串
     * @param type    需要反射类的Class
     * @param <T>     反射出来的类
     * @return 反射出来的类数组
     * @throws JsonProcessingException 解析失败
     */
    public static <T> List<T> parseXML(String content, Class<T> type) throws JsonProcessingException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(content, xmlMapper.getTypeFactory().constructCollectionType(List.class, type));
    }
}
