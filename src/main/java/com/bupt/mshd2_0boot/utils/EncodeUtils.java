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

    private final AddressCodeService  addressCodeService;
    @Autowired
    public EncodeUtils(ResourceLoader resourceLoader,AddressCodeService addressCodeService){
        this.resourceLoader=resourceLoader;
        this.addressCodeService=addressCodeService;
        parseCode();
    }


    /**
     *SourceType","SourceSub","LoaderType","DisasterType","DisasterSub","CategorySub"
     * province  省
     * city 市
     * county 县
     * town  镇
     * village 乡
     * Time
     * 参考Code.json
     * 每个分类由主码和子码构成
     * Map中的所有传值都必须以键值对的形式
     * Key 对应 上面的
     * Value 必须和JSON 文件中的值一致
     * 示例见 ApplicationTest 中的 EncodeTest方法
     */

    public  String Encodes(Map<String,String> data){
        //检测需要的参数是否存在
        for(String it:EncodeUtils.keywords){
            if(!data.containsKey(it)){
                log.error("-------Encodes Parameters missing-----"+it);
                return null;
            }

        }
        String Code = "";
        try {
            String Time = data.get("Time");
            if(Time.length()!=14){
                log.error("Encodes  ----Time Length ERROR ------");
                return null;
            }
            String address = this.addressCodeService.getCode(data.get("province"), data.get("city"), data.get("county"), data.get("town"), data.get("village"));
            Code += address;
            Code += Time;
//            对各大类分别进行编码
            Code += _T(data, "SourceCode", "SourceType", "SourceSub");
            Code += this.encode.getJSONObject("LoaderCode").getString(data.get("LoaderType"));
            Code += _T(data, "DisasterCode", "DisasterType", "DisasterSub");
            Code += _T(data, "DisasterCategory", "DisasterType", "CategorySub");
        }catch(NullPointerException e){
            log.error("传入信息错误!");
            return null;
        }
        //如果编码中有null 则说明编码失败
        if(Code.contains("null")){
            log.error(Code);
            return null;
        }
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
        //对各个类分别进行解码
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

    //复用函数 对某一类进行编码
    private String _T(Map<String,String> data,String Main,String Sub,String SubType){
        String Code="";
        String SubData=data.get(Sub);
        if(Main.equals("DisasterCategory")){
            if(SubData.equals("震情")){
                SubData="地震事件信息";
            }else{
                SubData+="信息";
            }
        }
        //通过Main找到对应的大类，通过SubData拿到大类中的小类
        JSONObject  SourceType = this.encode.getJSONObject(Main).getJSONObject(SubData);
        if(SourceType==null){
            log.error("-----_T ----error------");
            log.error(Main+"-----"+Sub+"---------"+SubType+"---------");
            log.error(data.get(Sub)+"---------"+data.get(SubType));
            return null;
        }
        //DisasterCategory的大类代码和Disaster的大类代码一致，因此无需重复编码
        if(!Main.equals("DisasterCategory"))
            Code+=SourceType.getString("code");
        Code+=SourceType.getJSONObject("subCode").getString(data.get(SubType));
        return Code;
    }

    //复用函数， 对某一大类进行解码

    private void _A(String type,String code,String subCode,Map<String,String> m,String key,String subKey){
        JSONObject Tp = decode.getJSONObject(type);
        JSONObject SU = Tp.getJSONObject(code);
        if(SU==null) {
            log.error("Decode ERROR! _A  "+  type+"   code" + code);
            return;
        }
        m.put(key,SU.getString("type"));
        m.put(subKey,SU.getString(subCode));
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

    /**
     * 反转Code.json 格式为
     * XXX:{
     *     mainCode:{
     *      type: xxx
     *      subCode1:xxx
     *      subCode2:xxx
     *     }
     * }
     * SourceCode:{
     *     "1":{
     *         "type":"业务报送数据",
     *         "00": "前方地震应急指挥部",
     *         "01": "后方地震应急指挥部",
     *         ...
     *     }
     * }
     * */
    private void parseCode() {
        //获取编码配置文件
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
    public static final String[] keywords={
      "SourceType","SourceSub","LoaderType","DisasterType","DisasterSub","CategorySub","province","city","county"
            ,"town","village","Time"
    };
}
