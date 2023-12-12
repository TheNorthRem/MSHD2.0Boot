package com.bupt.mshd2_0boot.controller;

import com.bupt.mshd2_0boot.entity.Disaster;
import com.bupt.mshd2_0boot.utils.ParseFileTools;
import com.bupt.mshd2_0boot.utils.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/Upload")
    public Result csvUpload(@RequestPart("file") MultipartFile file){

        File csv = new File(System.getProperty("java.io.tmpdir"));
        if(!csv.mkdir()) return Result.fail("ERROR");
        File tmp;
        try {
            tmp = File.createTempFile("tmp", ".csv");
            file.transferTo(tmp);
            List<Disaster> disasterList = ParseFileTools.parseFile(tmp, Disaster.class, false, ParseFileTools::parseCSV);
            System.out.println(disasterList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
