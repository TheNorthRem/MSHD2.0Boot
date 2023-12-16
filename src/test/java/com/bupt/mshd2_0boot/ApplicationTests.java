package com.bupt.mshd2_0boot;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bupt.mshd2_0boot.entity.Disaster;
import com.bupt.mshd2_0boot.service.AddressCodeService;
import com.bupt.mshd2_0boot.service.DisasterService;
import com.bupt.mshd2_0boot.utils.EncodeUtils;
import com.bupt.mshd2_0boot.utils.GaoDeAPI;
import com.bupt.mshd2_0boot.utils.ParseFileTools;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ApplicationTests {
    private final ResourceLoader resourceLoader;
    private final EncodeUtils util;
    private final AddressCodeService addressCodeService;

    private final DisasterService disasterService;

    @Autowired
    public ApplicationTests(EncodeUtils utils, AddressCodeService addressCodeService, DisasterService service, ResourceLoader resourceLoader) {
        this.util = utils;
        this.addressCodeService = addressCodeService;
        this.disasterService = service;
        this.resourceLoader = resourceLoader;
    }

    @Test
    void PageTest() {
        Page<Disaster> disasterPage = disasterService.listAll(4);
        System.out.println(disasterPage.getRecords());
    }

    @Test
    void decodeTest() {
        //37012610421320210921203142002002202001
        Map<String, String> decodes = util.decodes("370126104213202109212031422002202001");
        System.out.println(decodes);

    }


    @Test
    void EncodeTest() {
        Map<String, String> m = new HashMap<>();

        String[] key = {"SourceType", "SourceSub", "LoaderType", "DisasterType", "DisasterSub", "CategorySub", "province", "city", "county", "town", "village", "Time"};
        String[] value = {"泛在感知数据", "互联网感知", "音频", "人员伤亡及失踪", "受伤", "受灾人数", "山东省", "济南市", "商河县", "龙桑寺镇", "张佑村委会", "2021-09-21 20:31:42"};

        for (int i = 0; i < key.length; i++) {
            m.put(key[i], value[i]);
        }

        String encodes = util.encodes(m);

        System.out.println(encodes);

    }

    @Test
    void getAddressCodeTest() {
        System.out.println(GaoDeAPI.getAddressCode("海淀"));
        System.out.println(GaoDeAPI.getAddressCode("楚雄"));
        System.out.println(GaoDeAPI.getAddressCode("东城区"));
        // System.out.println(GaoDeAPI.getAddressCode("哇奥")); //错误检测
    }

    @Test
    void getAddressTest() {
        System.out.println(GaoDeAPI.getAddress("110108"));
        System.out.println(GaoDeAPI.getAddress("532300"));
        System.out.println(GaoDeAPI.getAddress("110101"));
        // System.out.println(GaoDeAPI.getAddress("1231312312312")); //错误检测
    }

    @Test
    void getDbAddressCodeTest() {
        System.out.println(addressCodeService.getCode("asd", "asd", "asd", "", "laal"));
        System.out.println(addressCodeService.getCode("山东省", "济南市", "商河县", "龙桑寺镇", "张佑村委会"));
        System.out.println(addressCodeService.getCode("山东省", "济南市", "商河县", "龙桑寺镇", "张佑村"));
        System.out.println(addressCodeService.getCode("四川省", "成都市", "简阳市", "赤水街道", "万家坝社区居民委员会"));
    }

    @Test
    void getDbAddressTest() {
        System.out.println(addressCodeService.getAddress(""));
        System.out.println(addressCodeService.getAddress("123"));
        System.out.println(addressCodeService.getAddress("540224207204"));
        System.out.println(addressCodeService.getAddress("540224207207"));
        System.out.println(addressCodeService.getAddress("54022420720"));
    }

    @Test
    void TestParseFileJSON() throws IOException {
        Resource jsonResource = resourceLoader.getResource("classpath:parseFileTestJson.json");
        Path jsonPath = Paths.get(jsonResource.getURI());
        System.out.println(ParseFileTools.parseFile(jsonPath.toFile(), Disaster.class, false, ParseFileTools::parseJSON));
    }

    @Test
    void TestParseFileXML() throws IOException {
        Resource xmlResource = resourceLoader.getResource("classpath:parseFileTestXML.xml");
        Path xmlPath = Paths.get(xmlResource.getURI());
        System.out.println(ParseFileTools.parseFile(xmlPath.toFile(), Disaster.class, false, ParseFileTools::parseXML));
    }

    @Test
    void TestParseFileCSV() throws IOException {
        Resource csvResource = resourceLoader.getResource("classpath:parseFileTestCSV.csv");
        Path csvPath = Paths.get(csvResource.getURI());
        List<Disaster> disasterList = ParseFileTools.parseFile(csvPath.toFile(), Disaster.class, false, ParseFileTools::parseCSV);
        for (var x : disasterList) {
            System.out.println(x);
        }
    }

    @Test
    void TestSerializedJSON() {
        QueryWrapper<Disaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("limit 5");
        List<Disaster> disasterList = disasterService.list(queryWrapper);
        disasterList = ParseFileTools.parseJSON(new String(ParseFileTools.serializedObject(disasterList, ParseFileTools::serializedJSON), StandardCharsets.UTF_8), Disaster.class);
        if (disasterList != null) {
            for (var x : disasterList) {
                System.out.println(x);
            }
        }
    }

    @Test
    void TestSerializedXML() {
        QueryWrapper<Disaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("limit 5");
        List<Disaster> disasterList = disasterService.list(queryWrapper);
        disasterList = ParseFileTools.parseXML(new String(ParseFileTools.serializedObject(disasterList, ParseFileTools::serializedXML), StandardCharsets.UTF_8), Disaster.class);
        if (disasterList != null) {
            for (var x : disasterList) {
                System.out.println(x);
            }
        }
    }

    @Test
    void TestSerializedCSV() {
        QueryWrapper<Disaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("limit 5");
        List<Disaster> disasterList = disasterService.list(queryWrapper);
        disasterList = ParseFileTools.parseCSV(new String(ParseFileTools.serializedObject(disasterList, ParseFileTools::serializedCSV), StandardCharsets.UTF_8), Disaster.class);
        if (disasterList != null) {
            for (var x : disasterList) {
                System.out.println(x);
            }
        }
    }

    @Test
    void TestSelectType() {
        Page<Disaster> disasterPage = disasterService.selectByType(1, 1);

        List<Disaster> records = disasterPage.getRecords();
        for (Disaster disaster : records) {
            Map<String, String> decodes = util.decodes(disaster.getId()); //解码返回
            if (decodes == null) {
                continue;
            }
            System.out.println(decodes);
        }
    }

    @Test
    void TestMap() {
        List<Map<String, String>> mapValues = CollUtil.newArrayList();

        // 添加第一个Map
        Map<String, String> map1 = new HashMap<>();
        map1.put("key1", "value1");
        map1.put("key2", "value2");
        mapValues.add(map1);

        // 添加第二个Map
        Map<String, String> map2 = new HashMap<>();
        map2.put("key3", "value3");
        map2.put("key4", "value4");
        mapValues.add(map2);

        System.out.println(mapValues);
        System.out.println(new String(ParseFileTools.serializedListMap(mapValues, ParseFileTools::serializedJSON), StandardCharsets.UTF_8));
        System.out.println(new String(ParseFileTools.serializedListMap(mapValues, ParseFileTools::serializedXML), StandardCharsets.UTF_8));
    }
}
