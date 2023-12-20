package com.bupt.mshd2_0boot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bupt.mshd2_0boot.entity.Disaster;
import com.bupt.mshd2_0boot.service.DisasterService;
import com.bupt.mshd2_0boot.utils.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@Resource
@RequestMapping("/File")
@Slf4j
@Tag(name = "文件")
public class FileController {

    private final EncodeUtils encodeUtils;
    private final DisasterService disasterService;
    @Autowired
    public FileController(EncodeUtils encodeUtils, DisasterService disasterService ){
        this.encodeUtils=encodeUtils;
        this.disasterService=disasterService;
    }



    @PostMapping("/Upload")
    @Operation(summary = "基于文件进行灾情信息上传")
    @Parameters({@Parameter(name="file",description = "文件"),@Parameter(name="uploaderId",description ="上传者Id")})
    public Result Upload(@RequestPart("file") MultipartFile file,@RequestParam("uploaderId") Integer Id){
        log.info("Upload!!!");
        File csv = new File(System.getProperty("java.io.tmpdir"));
        csv.mkdir();
        log.info("Upload!!!");
        File tmp=null;
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

            log.info("---"+disasterList);

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
        }finally {
            if(tmp!=null)
                tmp.delete();
        }
        return Result.ok();
    }

    @GetMapping("/download")
    @Operation(summary = "表单下载")
    @Parameters({@Parameter(name = "page",description = "下载第几页的数据"),@Parameter(name= "requestFormat",description = "下载格式，从 csv json xml 三个选一个 必须严格一致否则无法调用接口")})
    public void Download( HttpServletResponse response,@RequestParam("page") Integer page,@RequestParam String requestFormat){
        Page<Disaster> disasterPage = disasterService.listAll(page);//查询所有灾情
        List<Disaster> records = disasterPage.getRecords();
        List<Map<String,String>> result=new ArrayList<>();

        for (Disaster disaster:
             records) {
            Map<String, String> decodes = encodeUtils.decodes(disaster.getId());
            encodeUtils.addDecode(decodes,disaster);
            result.add(decodes);
        }

        byte[] buffer;
        switch (requestFormat){
            case "csv":

                buffer= ParseFileTools.serializedListMap(
                        result,
                        list -> ParseFileTools.serializedCSVWithHeader(list, header));

                break;
            case "json":
                buffer=ParseFileTools.serializedListMap(result, ParseFileTools::serializedJSON);
                break;
            case "xml":
                buffer=ParseFileTools.serializedListMap(result, ParseFileTools::serializedXML);
                break;
            default:
                return;
        }

        try {
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition","attachment;filename=data."+requestFormat);
            response.addHeader("Content-Length",""+buffer.length);
            response.addHeader("Access-Control-Allow-Origin","http://localhost:9528");
            OutputStream outputStream=new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/loaderUpload")
    @Operation(summary = "载体上传")
    @Parameters({@Parameter(name="disasterId",description = "灾情主键"),@Parameter(name="file",description = "载体文件")})
    public Result loaderUpload(@RequestParam(name="disasterId") Integer disasterId,@RequestParam(name="file") MultipartFile file){

        String path = EnvironmentValue.getParamSettings("file_path");

        Calendar instance = Calendar.getInstance();
        String month = (instance.get(Calendar.MONTH) + 1) + "月";
        path = path + month;

        String format = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf('.'));

        Disaster disaster = disasterService.getById(disasterId);

        Map<String, String> decodes = encodeUtils.decodes(disaster.getId());

        String loaderType = decodes.get("LoaderType");

        if(!Tools.CheckFormat(loaderType,format)){
            return Result.fail("文件格式不合规");
        }


        File realPath = new File(path);
        if (!realPath.exists()) {
            realPath.mkdirs();
        }

        String filename = "pg-" + UUID.randomUUID().toString().replaceAll("-", "") + format;
        File newfile = new File(realPath, filename);

        try {
            file.transferTo(newfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        disaster.setFilePath(month+"/"+filename);

        disasterService.updateById(disaster);

        return Result.ok();
    }

    private static final String []header={"code","province","city","county","town","village","Time","DisasterType","CategoryType","CategoryType","CategorySub"
    ,"SourceType","SourceSub","LoaderType","description","uploadTime","updateTime","uploader"};


}
