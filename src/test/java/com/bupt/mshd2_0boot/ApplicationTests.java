package com.bupt.mshd2_0boot;

import com.bupt.mshd2_0boot.service.AddressCodeService;
import com.bupt.mshd2_0boot.utils.EncodeUtils;
import com.bupt.mshd2_0boot.utils.GaoDeAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class ApplicationTests {
    private final EncodeUtils util;
    private final AddressCodeService addressCodeService;

    @Autowired
    public ApplicationTests(EncodeUtils utils, AddressCodeService addressCodeService) {
        this.util = utils;
        this.addressCodeService = addressCodeService;
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
        String[] value = {"泛在感知数据", "互联网感知", "音频", "人员伤亡及失踪", "受伤", "受灾人数", "山东省", "济南市", "商河县", "龙桑寺镇", "张佑村委会", "20210921203142"};

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
}
