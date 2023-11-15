package com.bupt.mshd2_0boot.controller;

import com.alibaba.fastjson2.JSONObject;
import com.bupt.mshd2_0boot.entity.Disaster;
import com.bupt.mshd2_0boot.entity.DisasterCount;
import com.bupt.mshd2_0boot.entity.User;
import com.bupt.mshd2_0boot.service.DisasterCountService;
import com.bupt.mshd2_0boot.service.DisasterService;
import com.bupt.mshd2_0boot.service.UserService;
import com.bupt.mshd2_0boot.utils.EncodeUtils;
import com.bupt.mshd2_0boot.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Disaster")
@Slf4j
@Tag(name = "灾情")
public class DisasterController {
    private final DisasterService disasterService;
    private final EncodeUtils encodeUtils;

    private final UserService userService;

    private final DisasterCountService disasterCountService;
    @Autowired
    public DisasterController(DisasterService disasterService,EncodeUtils encodeUtils,UserService userService,DisasterCountService disasterCountService){
        this.disasterService=disasterService;
        this.encodeUtils=encodeUtils;
        this.userService=userService;
        this.disasterCountService=disasterCountService;
    }
    @GetMapping("/listDisasters")
    @Operation(summary = "查询所有的灾情信息")
    public Result ListDisasters(){
        List<Disaster> list = disasterService.list(); //查询所有灾情
        List<Map<String,String>> res=new ArrayList<>();
        for(Disaster disaster: list){
            Map<String, String> decodes = encodeUtils.decodes(disaster.getId()); //解码返回
            if(decodes==null){
                continue;
            }
            decodes.put("code",disaster.getId());
            decodes.put("description",disaster.getDescription());
            decodes.put("uploadTime",disaster.getUploadTime().toString());
            decodes.put("updateTime",disaster.getUpdateTime().toString());
            decodes.put("uploader",userService.getById(disaster.getUploader()).getUsername());
            res.add(decodes);
        }
        return Result.ok(res);
    }

    @PostMapping("/addDisasterData")
    @Operation(summary = "添加灾情信息")
    @Parameters({@Parameter(name = "province",description = "省"),@Parameter(name="city",description = "市"),@Parameter(name="county",description = "县")
    ,@Parameter(name="town",description = "镇"),@Parameter(name = "village",description = "村")
    ,@Parameter(name="SourceType",description = "来源大类"),@Parameter(name = "SourceSub",description = "来源子类")
    ,@Parameter(name="LoaderType",description = "载体形式"),@Parameter(name = "DisasterType",description = "灾情分类")
    ,@Parameter(name="DisasterSub",description = "灾情子类"),@Parameter(name = "CategorySub",description = "灾情指标")
    ,@Parameter(name = "description",description = "描述"),@Parameter(name="Time",description = "时间"),@Parameter(name="uploader",description = "上传者Id")})
    public Result AddDisasterData(@RequestBody JSONObject data){
        Map<String,String> dataMap=new HashMap<>();
        //检测参数是否完整
        for(String keyword:EncodeUtils.keywords){
            String key = data.getString(keyword);
            if(key==null) return Result.fail("信息缺失"+keyword);
            dataMap.put(keyword,key);
        }

        String encodes = encodeUtils.Encodes(dataMap); //编码

        if(encodes==null){
            return Result.fail("信息有误");
        }

        Disaster disaster=new Disaster();
        disaster.setId(encodes);
        disaster.setDescription(data.getString("description")); //补充描述信息
        disaster.setUploader(data.getInteger("uploader"));
        disasterService.save(disaster);
        String addressCode = encodes.substring(0,5)+"0";//通过灾情码前5位提取行政区代码
        DisasterCount cnt = disasterCountService.getById(addressCode);

        if(cnt==null){ // 添加灾情时更新统计信息
            DisasterCount disasterCount=new DisasterCount();
            disasterCount.setId(addressCode);
            disasterCount.setCount(1);
            disasterCountService.save(disasterCount);
        }else{
            cnt.setCount(cnt.getCount()+1);
            disasterCountService.updateById(cnt);
        }
        return Result.ok("添加成功");
    }

    @DeleteMapping("/deleteDisaster")
    @Operation(summary="删除灾情信息")
    @Parameters({@Parameter(name = "Id",description = "灾害主键"),@Parameter(name="userId",description = "用户主键")})
    public Result deleteDisaster(@RequestParam Integer Id,@RequestParam Integer userId){
        User user = userService.getById(userId); //获取用户
        if(user==null){
            return Result.fail("用户不存在");
        }

        if(user.getPrivilege()==0) return Result.fail("无权限"); //检测权限

        Disaster disaster = disasterService.getById(Id);

        String addressCode=disaster.getId().substring(0,5)+"0"; //通过灾情码前5位提取行政区代码

        if(disasterService.removeById(Id)){//删除灾情时更新统计信息
            DisasterCount cnt = disasterCountService.getById(addressCode);
            cnt.setCount(cnt.getCount()-1);
            disasterCountService.updateById(cnt);
            return Result.ok("删除成功");
        }
        return Result.fail("删除失败");
    }

    @GetMapping("/getDisasterCount")
    @Operation(summary = "获取灾情地域统计信息")
    public Result getDisasterCount(){
        return Result.ok(disasterCountService.list());
    }
}
