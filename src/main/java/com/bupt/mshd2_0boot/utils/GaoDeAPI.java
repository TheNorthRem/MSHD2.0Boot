package com.bupt.mshd2_0boot.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

/**
 * 高德地图的API调用工具类
 */
@Component
@Slf4j
public class GaoDeAPI {

    /**
     * 根据地点获取行政区编码
     * <p>
     * 若返回null，说明地点不合规或者请求超时
     */
    public static String getAddressCode(String address) {
        // 请求URL
        JSONObject responseBody = GaoDeAPI.getGaoDeDistrictApi(address);
        if (responseBody == null) { //URL请求失败
            return null;
        }
        // 获取districts.adcode(行政区编码)，数据为空解析就会抛出异常，通过catch块返回空
        try {
            JSONArray districts = (JSONArray) responseBody.get("districts");
            return ((JSONObject) districts.get(0)).get("adcode").toString();
        } catch (Exception e) {
            log.error("JSON数据解析失败,Exception: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 根据行政区编码获取地点信息
     * <p>
     * 若返回null，说明行政区编码不合规或者请求超时
     */
    public static String getAddress(String addressCode) {
        // 请求URL
        JSONObject responseBody = GaoDeAPI.getGaoDeDistrictApi(addressCode);
        if (responseBody == null) { //URL请求失败
            return null;
        }
        // 获取districts.name(行政区名称)，数据为空解析就会抛出异常，通过catch块返回空
        try {
            JSONArray districts = (JSONArray) responseBody.get("districts");
            return ((JSONObject) districts.get(0)).get("name").toString();
        } catch (Exception e) {
            log.error("JSON数据解析失败,Exception: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 向高德的district api发起请求，获取行政区域和编码信息
     */
    private static JSONObject getGaoDeDistrictApi(String params) {
        // 参数不合规直接返回null
        if (StrUtil.isBlank(params)) {
            return null;
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) { // 创建HttpClient
            // 设置连接超时时间和请求超时时间
            int connectionTimeout = Integer.parseInt(EnvironmentValue.getParamSettings("ConnectionTimeout"));
            int socketTimeout = Integer.parseInt(EnvironmentValue.getParamSettings("SocketTimeout"));
            // 设置URL
            String URL = EnvironmentValue.getParamSettings("GaodeApiAddressCode");
            // 设置key
            String key = EnvironmentValue.getParamSettings("GaodeKey");

            // 设置请求配置(连接超时/请求超时)
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionTimeout)
                    .setSocketTimeout(socketTimeout)
                    .build();
            // 创建URL
            URIBuilder uriBuilder = new URIBuilder(URL);
            // 设置参数
            uriBuilder.setParameter("key", key);
            uriBuilder.setParameter("keywords", params);
            uriBuilder.setParameter("subdistrict", "0");
            uriBuilder.setParameter("extensions", "base");

            // 创建GET请求
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setConfig(requestConfig);

            // 执行请求
            CloseableHttpResponse response = httpClient.execute(httpGet);
            // 获取响应内容
            return JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            // 发生超时等异常直接返回null，并打印日志
            log.error("本次高德URL请求错误，可能是数据非法或者超时!, Exception: {}", e.getMessage());
            return null;
        }
    }
}
