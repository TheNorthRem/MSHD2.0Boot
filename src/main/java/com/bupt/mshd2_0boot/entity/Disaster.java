package com.bupt.mshd2_0boot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.opencsv.bean.CsvBindByName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@TableName("disaster")
@Schema(description = "灾情数据")
@AllArgsConstructor
@JacksonXmlRootElement(localName = "Disaster")
public class Disaster {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    @JacksonXmlProperty(localName = "disasterId")
    @CsvBindByName(column = "disasterId")
    private Integer disasterId;

    @TableField("id")
    @Schema(description = "灾情编码")
    @JacksonXmlProperty(localName = "id")
    @CsvBindByName(column = "id")
    private String id;

    @TableField("description")
    @Schema(description = "描述")
    @JacksonXmlProperty(localName = "description")
    @CsvBindByName(column = "description")
    private String description;

    @TableField("file_path")
    @Schema(description = "载体路径")
    @JacksonXmlProperty(localName = "filepath")
    @CsvBindByName(column = "filePath")
    private String filePath;

    @TableField("upload_time")
    @Schema(description = "上传时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JacksonXmlProperty(localName = "uploadTime")
    @CsvBindByName(column = "uploadTime")
    private Timestamp uploadTime;

    @TableField("uploader")
    @Schema(description = "上传者")
    @JacksonXmlProperty(localName = "uploader")
    @CsvBindByName(column = "uploader")
    private Integer uploader;

    @TableField("update_time")
    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JacksonXmlProperty(localName = "updateTime")
    @CsvBindByName(column = "updateTime")
    private Timestamp updateTime;

    public Disaster() {
        this.setUploadTime(Timestamp.valueOf(LocalDateTime.now()));
        this.setUpdateTime(this.getUploadTime());
    }
}
