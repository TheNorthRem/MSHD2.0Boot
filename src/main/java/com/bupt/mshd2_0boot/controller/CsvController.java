package com.bupt.mshd2_0boot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bupt.mshd2_0boot.entity.Disaster;
import com.bupt.mshd2_0boot.service.DisasterService;
import com.bupt.mshd2_0boot.utils.EncodeUtils;
import com.bupt.mshd2_0boot.utils.ParseFileTools;
import com.bupt.mshd2_0boot.utils.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/Csv")
@Slf4j
@Tag(name = "地址")
public class CsvController {

    private final EncodeUtils encodeUtils;
    private final DisasterService disasterService;
    @Autowired
    public CsvController(EncodeUtils encodeUtils,DisasterService disasterService){
        this.encodeUtils=encodeUtils;
        this.disasterService=disasterService;
    }

    @PostMapping("/Upload")
    public Result csvUpload(@RequestPart("file") MultipartFile file,@RequestParam("uploaderId") Integer Id){

        File csv = new File(System.getProperty("java.io.tmpdir"));
        if(!csv.mkdir()) return Result.fail("ERROR");
        File tmp;
        try {
            tmp = File.createTempFile("tmp", ".csv");
            file.transferTo(tmp);
            List<Disaster> disasterList = ParseFileTools.parseFile(tmp, Disaster.class, false, ParseFileTools::parseCSV);
//            System.out.println(disasterList);

            for (Disaster disaster:disasterList) {
                disaster.setUploader(Id);
                if(encodeUtils.decodes(disaster.getId())==null){
                    return Result.fail("编码格式错误 ：  "+disaster.getId());
                }
                QueryWrapper<Disaster> queryWrapper =  new QueryWrapper<>();

                queryWrapper.eq("id",disaster.getId());

                if(disasterService.count(queryWrapper)!=0){
                    return Result.fail("灾情信息已经上传 ----"+disaster.getId());
                }
            }
            for (Disaster disaster: disasterList) {
                disasterService.save(disaster);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.ok();
    }
}
