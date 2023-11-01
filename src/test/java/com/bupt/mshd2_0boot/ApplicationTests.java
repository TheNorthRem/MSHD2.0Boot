package com.bupt.mshd2_0boot;

import com.bupt.mshd2_0boot.utils.EncodeUtils;
import com.bupt.mshd2_0boot.utils.GaoDeAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class ApplicationTests {
    @Autowired
    EncodeUtils util;


    @Test
    void decodeTest() {
        Map<String, String> decodes = util.decodes("632626200206202105220204001010302001");
        System.out.println(decodes);

    }


    @Test
    void EncodeTest() {
        Map<String, String> m = new HashMap<>();

        String[] key = {"SourceType", "SourceSub", "LoaderType", "DisasterType", "DisasterSub", "CategoryType", "CategorySub"};
        String[] value = {"泛在感知数据", "互联网感知", "音频", "人员伤亡及失踪", "受伤", "人员伤亡及失踪信息", "受灾人数"};

        for (int i = 0; i < key.length; i++) {
            m.put(key[i], value[i]);
        }

        String encodes = util.Encodes(m);

        System.out.println(encodes);

    }

    @Test
    void getAddressCodeTest() {
        System.out.println(GaoDeAPI.getAddressCode("海淀"));
        System.out.println(GaoDeAPI.getAddressCode("楚雄"));
        // System.out.println(GaoDeAPI.getAddressCode("哇奥")); //错误检测
    }

    @Test
    void getAddressTest() {
        System.out.println(GaoDeAPI.getAddress("110108"));
        System.out.println(GaoDeAPI.getAddress("532300"));
        // System.out.println(GaoDeAPI.getAddress("1231312312312")); //错误检测
    }
}
