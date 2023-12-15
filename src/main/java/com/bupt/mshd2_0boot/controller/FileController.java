package com.bupt.mshd2_0boot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bupt.mshd2_0boot.entity.Disaster;
import com.bupt.mshd2_0boot.service.DisasterService;
import com.bupt.mshd2_0boot.utils.EncodeUtils;
import com.bupt.mshd2_0boot.utils.ParseFileTools;
import com.bupt.mshd2_0boot.utils.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/Csv")
@Slf4j
@Tag(name = "地址")
public class FileController {

    private final EncodeUtils encodeUtils;
    private final DisasterService disasterService;
    @Autowired
    public FileController(EncodeUtils encodeUtils, DisasterService disasterService){
        this.encodeUtils=encodeUtils;
        this.disasterService=disasterService;
    }

    @PostMapping("/Upload")
    public Result Upload(@RequestPart("file") MultipartFile file,@RequestParam("uploaderId") Integer Id){

        File csv = new File(System.getProperty("java.io.tmpdir"));
        if(!csv.mkdir()) return Result.fail("ERROR");

        File tmp;
        String format = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf('.'));
        try {
            tmp = File.createTempFile("tmp", format);
            file.transferTo(tmp);


            List<Disaster> disasterList;

            switch (format){
                case ".csv":
                    disasterList=ParseFileTools.parseFile(tmp, Disaster.class, false, ParseFileTools::parseCSV);
                    break;
                case ".json":
                    disasterList=ParseFileTools.parseFile(tmp, Disaster.class, false, ParseFileTools::parseJSON);

                    break;
                case ".xml":
                    disasterList=ParseFileTools.parseFile(tmp, Disaster.class, false, ParseFileTools::parseXML);
                    break;
                default:
                    return Result.fail("文件格式错误");
            }



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

    @GetMapping("/download")
    public Result csvDownload( HttpServletResponse response,@RequestParam("page") Integer page){
        Page<Disaster> disasterPage = disasterService.listAll(page);//查询所有灾情
        List<Disaster> records = disasterPage.getRecords();
        byte[] buffer = ParseFileTools.serializedObject(records, ParseFileTools::serializedCSV);


        try {
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition","attachment;filename=data.csv");
            response.addHeader("Content-Length",""+buffer.length);

            OutputStream outputStream=new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream;charset=utf-8");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.ok();
    }
}
