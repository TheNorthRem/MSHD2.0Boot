package com.bupt.mshd2_0boot.utils;

import com.alibaba.fastjson2.JSONObject;
import com.bupt.mshd2_0boot.entity.AddressCode;
import com.bupt.mshd2_0boot.service.AddressCodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class EncodeUtils {


    private final ResourceLoader resourceLoader;

    private JSONObject encode;

    private JSONObject decode;

    private AddressCodeService addressCodeService;
    @Autowired
    public EncodeUtils(ResourceLoader resourceLoader,AddressCodeService addressCodeService){
        this.resourceLoader=resourceLoader;
        this.addressCodeService=addressCodeService;
        parseCode();
    }



    public void parseCode() {
        Resource resource = resourceLoader.getResource("classpath:Code.json");
        File file;

        String str=null;

        try {
            file = resource.getFile();
            str=FileUtils.readFileToString(file, "UTF-8");
        }catch (IOException e){
            System.err.println("ERROR !! ---------编码初始化失败----------");
        }

        JSONObject res=new JSONObject();
        JSONObject data=JSONObject.parseObject(str);
        this.encode=data;

        if(data==null){
            System.err.println("ERROR !! ---------编码初始化失败----------");
            return;
        }

        JSONObject loaderCode= data.getJSONObject("LoaderCode");
        JSONObject CodeLoader=new JSONObject();
        loaderCode.forEach((k,v)-> CodeLoader.put((String) v,k));

        res.put("loaderCode",CodeLoader);

        res.put("SourceCode",parse("SourceCode",data));

        res.put("DisasterCode",parse("DisasterCode",data));

        res.put("DisasterCategory",parse("DisasterCategory",data));

        this.decode=res;
    }


    private JSONObject parse(String code,JSONObject data){


        JSONObject type = data.getJSONObject(code);

        JSONObject typeReverse=new JSONObject();

        type.forEach((key,value)->{
            JSONObject v = (JSONObject)value;
            String mainCode = v.getString("code");
            JSONObject obj=new JSONObject();
            JSONObject subCode=v.getJSONObject("subCode");
            subCode.forEach((t,sCode)-> obj.put((String)sCode,t));
            obj.put("type",key);
            typeReverse.put(mainCode,obj);
        });

        return typeReverse;
    }




    /**
     *SourceType","SourceSub","LoaderType","DisasterType","DisasterSub","CategoryType","CategorySub"
     * 参考Code.json
     * 每个分类由主码和子码构成
     * Map中的所有传值都必须以键值对的形式
     * Key 对应 上面的
     * Value 必须和JSON 文件中的值一致
     * Type 指的是 主类信息
     * Sub 指的是子类信息
     * province  省
     * city 市
     * county 县
     * town  镇
     * village 乡
     * Time
     * 示例见 ApplicationTest 中的 EncodeTest方法
     */

    public  String Encodes(Map<String,String> data){
        for(String it:this.keywords){
            if(!data.containsKey(it)){
                log.error("-------Encodes Parameters missing-----"+it);
                return null;
            }

        }
        String Code = "";
        String Time=data.get("Time");
        String address = this.addressCodeService.getCode(data.get("province"), data.get("city"), data.get("county"), data.get("town"), data.get("village"));
        Code+=address;
        Code+=Time;
        Code+= _T(data,"SourceCode","SourceType","SourceSub");
        Code+= this.encode.getJSONObject("LoaderCode").getString(data.get("LoaderType"));
        Code+= _T(data,"DisasterCode","DisasterType","DisasterSub");
        Code+= _T(data,"DisasterCategory","CategoryType","CategorySub");
        return Code ;
    }

    /**
     * 解码，输入代码 返回Map集合
     * 参数：String
     * 返回值为Map 包含的信息 参考Encode 函数上的注释
     *
     */

    public   Map<String,String> decodes(String str){
        if(str.length()!=36){
            log.error("decodes fail !  code length != 36  !!! ");
            return null;
        }
        Map<String, String> code = subCode(str);
        Map<String,String> res=new HashMap<>();
        _A("SourceCode",code.get("Source"),code.get("SourceSub"),res,"SourceType","SourceSub");
        res.put("LoaderType",decode.getJSONObject("loaderCode").getString(code.get("loaderType")));
        _A("DisasterCode",code.get("DisasterType"),code.get("DisasterTypeSub"),res,"DisasterType","DisasterSub");
        _A("DisasterCategory",code.get("DisasterType"),code.get("CategorySub"),res,"CategoryType","CategorySub");
        res.put("Time",code.get("Time"));
        AddressCode address = addressCodeService.getAddress(code.get("Position"));
        res.put("省",address.getProvince());
        res.put("市",address.getCity());
        res.put("县",address.getCounty());
        res.put("镇",address.getTown());
        res.put("村",address.getVillage());
        return res;
    }

    private String _T(Map<String,String> data,String Main,String Sub,String SubType){
        String Code="";
        JSONObject  SourceType = this.encode.getJSONObject(Main).getJSONObject(data.get(Sub));
        if(Main!="DisasterCategory")
            Code+=SourceType.getString("code");
        Code+=SourceType.getJSONObject("subCode").getString(data.get(SubType));
        return Code;
    }

    private void _A(String type,String code,String subCode,Map<String,String> m,String key,String subKey){
        JSONObject Tp = decode.getJSONObject(type);
        JSONObject SU = Tp.getJSONObject(code);
        if(SU==null) {
            System.out.println(code);
            return;
        }
        m.put(key,SU.getString("type"));
        m.put(subKey,SU.getString(subCode));
    }



    private Map<String,String> subCode(String str){

        String []codes={str.substring(0,12),str.substring(12,26),str.substring(26,27),str.substring(27,29),
                str.substring(29,30),str.substring(30,31),str.substring(31,33),str.substring(33,36)};
        String [] keywords={"Position","Time","Source","SourceSub","loaderType","DisasterType","DisasterTypeSub","CategorySub"};
        Map<String,String> res=new HashMap<>();

        for(int i=0;i< codes.length;i++){
            res.put(keywords[i],codes[i]);
        }

        return res;
    }

    private String[] keywords={
      "SourceType","SourceSub","LoaderType","DisasterType","DisasterSub","CategoryType","CategorySub","province","city","county"
            ,"town","village"
    };
}
