package com.bupt.mshd2_0boot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@TableName("disaster")
@Schema(description = "灾情数据")
@AllArgsConstructor
public class Disaster {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Integer disasterId;

    @TableField("id")
    @Schema(description = "灾情编码")
    private String id;

    @TableField("description")
    @Schema(description = "描述")
    private String description;

    @TableField("file_path")
    @Schema(description = "载体路径")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String filePath;

    @TableField("upload_time")
    @Schema(description = "上传时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp uploadTime;

    @TableField("uploader")
    @Schema(description = "上传者")
    private Integer uploader;

    @TableField("update_time")
    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updateTime;

    public Disaster(){
        this.setUploadTime(Timestamp.valueOf(LocalDateTime.now()));
        this.setUpdateTime(this.getUploadTime());
    }
}
