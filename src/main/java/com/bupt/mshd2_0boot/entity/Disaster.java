package com.bupt.mshd2_0boot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("disaster")
@Schema(description = "灾情数据")
@AllArgsConstructor
@NoArgsConstructor
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
    private String filePath;
}
