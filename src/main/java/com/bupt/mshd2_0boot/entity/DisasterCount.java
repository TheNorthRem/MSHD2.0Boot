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
@TableName("disasterCount")
@Schema(name = "灾情统计")
@NoArgsConstructor
@AllArgsConstructor
public class DisasterCount {
    @TableId(value = "id", type = IdType.INPUT)
    @Schema(description = "主键:地区代码")
    private String id;

    @TableField("count")
    @Schema(description = "地区灾情数量")
    private Integer count;

}
