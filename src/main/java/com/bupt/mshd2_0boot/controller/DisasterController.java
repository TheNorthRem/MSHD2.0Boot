package com.bupt.mshd2_0boot.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bupt.mshd2_0boot.entity.Disaster;
import com.bupt.mshd2_0boot.entity.DisasterCount;
import com.bupt.mshd2_0boot.service.DisasterCountService;
import com.bupt.mshd2_0boot.service.DisasterService;
import com.bupt.mshd2_0boot.utils.EncodeUtils;
import com.bupt.mshd2_0boot.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/Disaster")
@Slf4j
@Tag(name = "灾情")
public class DisasterController {
    private final DisasterService disasterService;
    private final EncodeUtils encodeUtils;


    private final DisasterCountService disasterCountService;

    @Autowired
    public DisasterController(DisasterService disasterService, EncodeUtils encodeUtils, DisasterCountService disasterCountService) {
        this.disasterService = disasterService;
        this.encodeUtils = encodeUtils;
        this.disasterCountService = disasterCountService;
    }

    @GetMapping("/listDisasters")
    @Operation(summary = "返回所有灾情信息")
    @Parameter(name = "page",description = "页数")

    public Result getAllDisasters(@RequestParam Integer page) {
        Page<Disaster> disasterPage = disasterService.listAll(page);//查询所有灾情
        return Result.ok(encodeUtils.decodePage(disasterPage));
    }

    @GetMapping("/getDisasterByType")
    @Operation(summary = "按type查询 type属性为Int  和灾情编码对应")
    @Parameters({@Parameter(name = "page",description = "页数"),@Parameter(name = "type",description = "类型")})

    public Result getDisasterByType(@RequestParam Integer page,@RequestParam Integer type) {
        if(type==0){
            Page<Disaster> disasterPage = disasterService.listAll(page);//查询所有灾情
            return Result.ok(encodeUtils.decodePage(disasterPage));
        }
        Page<Disaster> disasterPage = disasterService.selectByType(page,type);//查询所有灾情
        return Result.ok(encodeUtils.decodePage(disasterPage));
    }

    @PostMapping("/addDisasterData")
    @Operation(summary = "添加灾情信息")
    @Parameters({@Parameter(name = "province", description = "省"), @Parameter(name = "city", description = "市"), @Parameter(name = "county", description = "县")
            , @Parameter(name = "town", description = "镇"), @Parameter(name = "village", description = "村")
            , @Parameter(name = "SourceType", description = "来源大类"), @Parameter(name = "SourceSub", description = "来源子类")
            , @Parameter(name = "LoaderType", description = "载体形式"), @Parameter(name = "DisasterType", description = "灾情分类")
            , @Parameter(name = "DisasterSub", description = "灾情子类"), @Parameter(name = "CategorySub", description = "灾情指标")
            , @Parameter(name = "description", description = "描述"), @Parameter(name = "Time", description = "时间"), @Parameter(name = "uploader", description = "上传者Id")})
    public Result addDisasterData(@RequestBody JSONObject data) {
        Map<String, String> dataMap = new HashMap<>();
        //检测参数是否完整
        for (String keyword : EncodeUtils.KEYWORDS) {
            String key = data.getString(keyword);
            if (key == null) return Result.fail("信息缺失" + keyword);
            dataMap.put(keyword, key);
        }

        String encodes = encodeUtils.encodes(dataMap); //编码

        if (encodes == null) {
            return Result.fail("信息有误");
        }

        QueryWrapper<Disaster> queryWrapper =  new QueryWrapper<>();

        queryWrapper.eq("id",encodes);

        if(disasterService.count(queryWrapper)!=0){
            return Result.fail("该灾情信息已经上传");
        }

        Disaster disaster = new Disaster();
        disaster.setId(encodes);
        disaster.setDescription(data.getString("description")); //补充描述信息
        Integer uploader = data.getInteger("uploader");

        if(uploader==null){
            return Result.fail("uploader 为空");
        }

        disaster.setUploader(uploader);


        disasterService.save(disaster);
        String addressCode = encodes.substring(0, 5) + "0";//通过灾情码前5位提取行政区代码
        DisasterCount cnt = disasterCountService.getById(addressCode);

        if (cnt == null) { // 添加灾情时更新统计信息
            DisasterCount disasterCount = new DisasterCount();
            disasterCount.setId(addressCode);
            disasterCount.setCount(1);

            disasterCountService.save(disasterCount);
        } else {
            cnt.setCount(cnt.getCount() + 1);
            disasterCountService.updateById(cnt);
        }
        return Result.ok(disaster.getDisasterId());
    }



    @DeleteMapping("/deleteDisaster")
    @Operation(summary = "删除灾情信息")
    @Parameter(name = "Id", description = "灾害主键")
    public Result deleteDisaster(@RequestParam Integer Id) {

        Disaster disaster = disasterService.getById(Id);

        String addressCode = disaster.getId().substring(0, 5) + "0"; //通过灾情码前5位提取行政区代码

        if (disasterService.removeById(Id)) {//删除灾情时更新统计信息
            DisasterCount cnt = disasterCountService.getById(addressCode);
            cnt.setCount(cnt.getCount() - 1);
            disasterCountService.updateById(cnt);
            return Result.ok("删除成功");
        }
        return Result.fail("删除失败");
    }

    @PostMapping("/addDisasterByCode")
    @Operation(summary = "通过编码增加灾情信息")
    @Parameters({@Parameter(name="code",description = "灾情编码"),@Parameter(name="description",description = "灾情描述"),@Parameter(name = "uploaderId",description = "上传者主键")})
    public Result addDisasterByCode(@RequestBody JSONObject data){

        String code= data.getString("code");
        String description=data.getString("description");
        Integer uploaderId=data.getInteger("uploaderId");

        Map<String, String> decodes = encodeUtils.decodes(code);
        if(decodes==null)
            return Result.fail("编码格式错误!");

        Disaster disaster = new Disaster();

        QueryWrapper<Disaster> queryWrapper =  new QueryWrapper<>();

        queryWrapper.eq("id",code);

        if(disasterService.count(queryWrapper)!=0){
            return Result.fail("该灾情信息已经上传");
        }


        disaster.setId(code);

        disaster.setDescription(description);

        disaster.setUploader(uploaderId);

        disasterService.save(disaster);


        return Result.ok(disaster.getDisasterId());
    }
    @GetMapping("/getDisasterDetail")
    @Operation(summary = "获取灾情详情")
    @Parameter(name="code",description = "灾情码")
    public Result getDisasterDetail(@RequestParam(name="code") String code){

        QueryWrapper<Disaster> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("id",code);
        Disaster disaster = disasterService.getOne(queryWrapper);
        if (disaster == null) return Result.fail("灾情码不存在");
        Map<String, String> decodes = encodeUtils.decodes(disaster.getId());
        encodeUtils.addDecode(decodes,disaster);
        return Result.ok(decodes);
    }




}
