package com.bupt.mshd2_0boot;

import com.alibaba.fastjson2.JSONObject;
import com.bupt.mshd2_0boot.utils.EncodeUtils;
import com.bupt.mshd2_0boot.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class ApplicationTests {
    @Autowired
    EncodeUtils util;


     @Test
    void decodeTest(){
         Map<String, String> decodes = util.decodes("632626200206202105220204001010302001");
         System.out.println(decodes);

     }



    @Test
    void EncodeTest() {
        Map<String,String> m=new HashMap<>();

        String [] key={"SourceType","SourceSub","LoaderType","DisasterType","DisasterSub","CategoryType","CategorySub"};
        String [] value={"泛在感知数据","互联网感知","音频","人员伤亡及失踪","受伤","人员伤亡及失踪信息","受灾人数"};

        for(int i=0;i< key.length;i++){
            m.put(key[i],value[i]);
        }

        String encodes = util.Encodes(m);

        System.out.println(encodes);

    }

}
