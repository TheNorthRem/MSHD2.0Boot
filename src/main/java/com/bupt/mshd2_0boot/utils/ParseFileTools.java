package com.bupt.mshd2_0boot.utils;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import javax.sql.rowset.serial.SerialException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 解析文件的工具类
 */
public class ParseFileTools {
    /**
     * 解析文件(失败抛出异常)
     *
     * @param file          文件
     * @param type          反射时需要使用的Class
     * @param isDelete      解析后是否要删除文件
     * @param parseFunction 将字符串反射成对应类的方法
     * @param <T>           需要反射出来的类
     * @return 反射对应出来的实体类数组
     * @throws IOException 没有对应文件的异常|路径是目录的异常|文件权限不足地异常|反射解析失败的异常
     */
    public static <T> List<T> parseFile(File file, Class<T> type, boolean isDelete, BiFunction<String, Class<T>, List<T>> parseFunction) throws IOException {
        Path path = Paths.get(file.getPath());
        // 文件不存在
        if (!Files.exists(path)) {
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

        // 为空证明检测失败
        if (ans == null) {
            throw new RuntimeException(new SerialException("反射解析失败"));
        }

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
     * @return 反射出来的类数组(失败为null)
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
     * @return 反射出来的类数组(失败为null)
     */
    public static <T> List<T> parseXML(String content, Class<T> type) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.readValue(content, xmlMapper.getTypeFactory().constructCollectionType(List.class, type));
        } catch (JsonProcessingException jsonProcessingException) {
            throw new RuntimeException(jsonProcessingException);
        }
    }

    /**
     * 反射CSV字符串到类上
     *
     * @param content   CSV字符串
     * @param type      需要反射类的Class
     * @param <T>反射出来的类
     * @return 反射出来的类数组(失败为null)
     */
    public static <T> List<T> parseCSV(String content, Class<T> type) {
        StringReader stringReader = new StringReader(content);

        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(stringReader)
                .withType(type)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        return csvToBean.parse();
    }

   // public static <T> byte[] serializedObject(List<Map<String,String>> objects, Function<Map<String,String>, String> serializedMethod)

    /**
     * 序列化对象集合
     *
     * @param objects          序列化对象集合
     * @param serializedMethod 序列化方法(返回值是String,可以复用)
     * @param <T>              序列化的类
     * @return 序列化之后UTF_8的字节流
     */
    public static <T> byte[] serializedObject(List<T> objects, Function<List<T>, String> serializedMethod) {
        return serializedMethod.apply(objects).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 序列化对象集合为JSON字符串
     *
     * @param objects 对象集合
     * @param <T>     对象类型
     * @return 序列化后的字符串
     */
    public static <T> String serializedJSON(List<T> objects) {
        return JSON.toJSONString(objects);
    }

    /**
     * 序列化对象集合为XML字符串
     *
     * @param objects 对象集合
     * @param <T>     对象类型
     * @return 序列化后的字符串
     */
    public static <T> String serializedXML(List<T> objects) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.writeValueAsString(objects);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 序列化对象集合为CSV字符串
     *
     * @param objects 对象集合
     * @param <T>     对象类型
     * @return 序列化后的字符串
     */
    public static <T> String serializedCSV(List<T> objects) {
        try {
            StringWriter stringWriter = new StringWriter();
            StatefulBeanToCsv<T> statefulBeanToCsv = new StatefulBeanToCsvBuilder<T>(stringWriter)
                    .withApplyQuotesToAll(false)
                    .build();

            statefulBeanToCsv.write(objects);
            return stringWriter.toString();
        } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            throw new RuntimeException(e);
        }
    }
}
