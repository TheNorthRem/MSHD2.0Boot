package com.bupt.mshd2_0boot.utils;

import com.alibaba.fastjson2.JSONObject;
import com.bupt.mshd2_0boot.entity.AddressCode;
import com.bupt.mshd2_0boot.service.AddressCodeService;
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


    private String _T(Map<String,String> data,String Main,String Sub,String SubType){
        String Code="";
        JSONObject  SourceType = this.encode.getJSONObject(Main).getJSONObject(data.get(Sub));
        Code+=SourceType.getString("code");
        Code+=SourceType.getJSONObject("subCode").getString(data.get(SubType));
        return Code;
    }

    /**
     *
     *SourceType","SourceSub","LoaderType","DisasterType","DisasterSub","CategoryType","CategorySub"
     *参考Code.json
     * Map中的所有传值都必须以键值对的形式
     * Key 对应 上面的
     * Value 必须和JSON 文件中的值一致
     * Type 指的是 主类信息
     * Sub 指的是子类信息
     * 示例见 ApplicationTest 中的 EncodeTest方法
     *
     */

    public  String Encodes(Map<String,String> data){

        String Code = "";
        String province = data.get("province");
        String city=data.get("city");
        String county=data.get("county");
        String town=data.get("town");
        String village=data.get("village");
        String address = this.addressCodeService.getCode(province, city, county, town, village);
        Code+=address;
        Code+=data.get("Time");
        Code+= _T(data,"SourceCode","SourceType","SourceSub");
        Code+= this.encode.getJSONObject("LoaderCode").getString(data.get("LoaderType"));
        Code+= _T(data,"DisasterCode","DisasterType","DisasterSub");
        Code+= _T(data,"DisasterCategory","CategoryType","CategorySub");
        return Code ;
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


    /**
     * 传入编码 ，返回 键值对
     * 参考示例见 ApplicationTest 中的 decodeTest 方法
     * */
    public   Map<String,String> decodes(String str){
        String Position=str.substring(0,12);
        String Time =str.substring(12,26);
        String Source=str.substring(26,27);
        String SourceSub=str.substring(27,29);
        String loaderType=str.substring(29,30);
        String DisasterType=str.substring(30,31);
        String DisasterTypeSub=str.substring(31,33);
        String CategorySub=str.substring(33,36);
        Map<String,String> res=new HashMap<>();
        _A("SourceCode",Source,SourceSub,res,"SourceType","SourceSub");
        res.put("LoaderType",decode.getJSONObject("loaderCode").getString(loaderType));
        _A("DisasterCode",DisasterType,DisasterTypeSub,res,"DisasterType","DisasterSub");
        _A("DisasterCategory",DisasterType,CategorySub,res,"CategoryType","CategorySub");
        res.put("Time",Time);
        AddressCode address = addressCodeService.getAddress(Position);
        res.put("省",address.getProvince());
        res.put("市",address.getCity());
        res.put("县",address.getCounty());
        res.put("镇",address.getTown());
        res.put("村",address.getVillage());
        return res;
    }
}
